package controllers

import Services.{AuthenticateUser, EarthService}
import javax.inject._
import models._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._


import scala.concurrent.{Await, ExecutionContext, Future}

class EarthController @Inject()(repo: EarthService,
                                authenticateUser:AuthenticateUser,
                                cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  def getContinents: Action[AnyContent] =authenticateUser.async { implicit request =>
      for {
        res <- repo.continentList()

      } yield{
        if(res.isEmpty){
              Ok(Json.toJson(StringResponseRef(res,"No data found")))
        }
          else{
              Ok(Json.toJson(StringResponseRef(res,"Data published")))
        }
      }
  }

  def getCountries: Action[AnyContent] =authenticateUser.async { implicit request =>
    repo.countryList().map{ xs =>
      if(!xs.isEmpty)
        Ok(Json.toJson(xs))
      else
        Ok(Json.toJson(StringResponse("No Country Data Found")))
    }
  }

  def addContinent: Action[JsValue] =authenticateUser.async(parse.json){
    implicit request:Request[JsValue]=>
      val result=request.body.validate[StringResponse]
      result.fold(
        errors=>{
          Future{Ok(Json.toJson(StringResponse("Bad Data")))}
        },
        continent=>{

         Future{ Ok(Json.toJson(StringResponse(repo.createContinent(continent.msg))))}
        }
      )
  }

  def addCountry: Action[JsValue] =authenticateUser(parse.json){
    implicit request:Request[JsValue]=>
      val result=request.body.validate[CountryRef]
      result.fold(
        errors=>{
          Ok(Json.toJson(StringResponse("Bad Data")))
        },
        country=>{
          Ok(Json.toJson(StringResponse(repo.createCountry(country))))
        }
      )
  }

  def deleteContinent(continent:String)= authenticateUser {

    Ok(repo.deleteContinent(continent))

  }

  def getContinentOfCountry(countryName:String): Action[AnyContent] =authenticateUser{
    Ok(Json.toJson(StringResponse(repo.getContinentOfACountry(countryName))))
  }

  def checkIfTheContinentsAreSame(country1:String,country2:String)= authenticateUser{
    Ok(Json.toJson(StringResponse(repo.checkIfTwoCountryLiesInSameContinent(country1,country2))))
  }

  def addCities:Action[JsValue]=authenticateUser(parse.json){
    implicit request:Request[JsValue]=>
      request.body.validate[Array[CityRef]].fold(
        errors=>{
          Ok(Json.toJson(StringResponse("Bad input")))
        },
        cities=>{
          Ok(Json.toJson(StringResponse(repo.addCitiesToTheCountry(cities))))
        }
      )

  }

  def getCitiesOfContinent(continent:String)=authenticateUser{
    val result=repo.getAllCitiesOfContinent(continent)
    result.isEmpty match {
      case true =>
        Ok(Json.toJson(StringResponse(s"No Data found for $continent")))
      case false=>
        Ok(Json.toJson(result))
    }
  }

  def getCitiesByFirstLetter=authenticateUser{
    val result=repo.groupCitiesByFirstLetter.map(value=>value._1->Json.toJson(value._2))
    Ok(Json.toJson(result.toMap))
  }
}



