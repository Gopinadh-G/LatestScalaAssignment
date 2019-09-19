package Services

import javax.inject.{Inject, Singleton}
import models._
import dao._
import scala.concurrent.{Await, ExecutionContext, Future}

import scala.concurrent.duration._

@Singleton
class EarthService @Inject()(repo : EarthRepository)(implicit ec: ExecutionContext)  extends EarthServiceTrait {



  def continentList(): Future[Seq[Continent]] =repo.continentList()
  def countryList(): Future[Seq[Country]] = repo.countryList()



  def createContinent(name: String): String = {
    if(name.matches("[A-Za-z]+")) {
      println("success1")
      if(!repo.getAll.exists(p => p.continentName.equalsIgnoreCase(name))) {
        val result = Await.result(repo.createContinent(name),5.seconds)
        println(result)
        if(!result.continentName.isEmpty) {
          "Continent added successfully"
        }else{
          "Continent not added successfully"
        }


      }
      else "Continent is already existing"
    }
    else "Continent's name must be only alphabets/code can be only between 1-7"
  }

  def addCitiesToTheCountry(newCities:Array[CityRef])={

    val filtered =for{
      eachCity <- newCities
      if(!repo.getAllCities.exists(_.cityName.equalsIgnoreCase(eachCity.cityName)))
      if(repo.getAllCountries.exists(_.countryName.equalsIgnoreCase(eachCity.countryName)))
    } yield eachCity
    val errors=newCities.diff(filtered)
    if(errors.isEmpty){
      for (x <- filtered){
        repo.addCitiesToTheCountry(x)
      }
      "All cities added successfully"
    }
    else
      errors.map(_.cityName).mkString("The following cities",","," are either available or with wrong country code ")
  }

  def createCountry(countryVal: CountryRef): String = {

    if(countryVal.countryName.matches("[A-Za-z]+")) {
      if(repo.getAll.exists(p => p.continentName.equalsIgnoreCase(countryVal.continentName))) {
        if(!repo.getAllCountries.exists(p => p.countryName.equalsIgnoreCase(countryVal.countryName))) {
          repo.createCountry(countryVal)
          "Country added successfully"
        }
        else s"Country ${countryVal.countryName} already present in ${countryVal.continentName} continent."
      }
      else s"Continent ${countryVal.continentName} does not exist in the world."
    }
    else "Continent's name must be only alphabets/code can be only between 1-7"
  }

  def getContinentOfACountry(countryName:String):String={
    val filteredCountry=repo.getAllCountries.find(_.countryName.equalsIgnoreCase(countryName))
    filteredCountry.isDefined match{
      case true =>
        filteredCountry.get.continentName
      case false=>
        "No Country Details found"
    }
  }

  def checkIfTwoCountryLiesInSameContinent(first:String,second:String): String ={

    val countryList = repo.getAllCountries
    if(first.isEmpty || second.isEmpty){
      "Either of the given input is empty."
    }else{
      countryList.find(_.countryName.equalsIgnoreCase(first)).getOrElse(Country(0,"","A")).continentName.equalsIgnoreCase(countryList.find(_.countryName.equalsIgnoreCase(second)).getOrElse(Country(1,"","B")).continentName) match {
        case true => {
          val countryDetail = countryList.find(p=>p.countryName.equalsIgnoreCase(first)).get.continentName
          s"Both ${first} and ${second} are present in same continent ${countryDetail}"
        }
        case false => s"Both ${first} and ${second} are not present in same continent"
      }
    }

  }

  def getAllCitiesOfContinent(continent:String):Seq[City]={
    for{
      continent <- repo.getAll.filter(_.continentName.equalsIgnoreCase(continent))
      country <- repo.getAllCountries.filter(_.continentName.equalsIgnoreCase(continent.continentName))
      city <- repo.getAllCities.filter(_.countryName.equalsIgnoreCase(country.countryName))
    } yield city

  }

  def groupCitiesByFirstLetter=repo.getAllCities.groupBy(_.cityName.charAt(0).toString).toList



  def deleteContinent(continentName : String) : String = {
    val resultList = repo.getAll
    val contobject: Continent = resultList.find(_.continentName.equalsIgnoreCase(continentName)).getOrElse(Continent(0,""))
    if(contobject.continentName.equalsIgnoreCase(continentName)){
      repo.deleteContinent(contobject)

      s" ${continentName} continent Deleted Successfully."
    }else{
      s"There is no continent with name ${continentName}"
    }

  }



}

