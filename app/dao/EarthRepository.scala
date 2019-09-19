package dao

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}


@Singleton
class EarthRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  extends EarthRepoTrait {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  private class ContinentTable(tag: Tag) extends Table[Continent](tag, "continent") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def continentName = column[String]("continentName")
    def * = (id, continentName) <> ((Continent.apply _).tupled, Continent.unapply)
  }

  private class CountryTable(tag: Tag) extends Table[Country](tag, "country") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def countryName = column[String]("countryName")
    def continentName = column[String]("continentName")
    def * = (id,countryName, continentName) <> ((Country.apply _).tupled, Country.unapply)
  }

  private class CityTable(tag: Tag) extends Table[City](tag, "city") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def cityName = column[String]("cityName")
    def countryName = column[String]("countryName")
    def * = (id,cityName, countryName) <> ((City.apply _).tupled, City.unapply)
  }

  private val continent = TableQuery[ContinentTable]
  private val country = TableQuery[CountryTable]
  private val city = TableQuery[CityTable]


  def createContinent(name: String) =
        db.run {
          (continent.map(p => p.continentName)
            returning continent.map(_.id)
            into ((continentName, id) => Continent(id, continentName))
            ) += (name)
        }



  def addCitiesToTheCountry(newCities:CityRef)=


        db.run {

          (city.map(p => (p.cityName, p.countryName))
            returning city.map(_.id)
            into ((nameCount, id) => Country(id, nameCount._1, nameCount._2))
            ) += (newCities.cityName, newCities.countryName)
        }


  def createCountry(countryVal: CountryRef)= db.run {
            (country.map(p => (p.countryName, p.continentName))
              returning country.map(_.id)
              into ((nameCont, id) => Country(id, nameCont._1, nameCont._2))
              ) += (countryVal.countryName, countryVal.continentName)
          }


  def continentList(): Future[Seq[Continent]] = db.run {continent.result}
  def countryList(): Future[Seq[Country]] = db.run {country.result}
  def cityList(): Future[Seq[City]] = db.run {city.result}


  def getAll: Seq[Continent] = Await.result(continentList, 5.seconds)
  def getAllCountries: Seq[Country] = Await.result(countryList, 5.seconds)
  def getAllCities: Seq[City] = Await.result(cityList, 5.seconds)


  def deleteCity(continentVal : City) = db.run{
    continent.filter(_.continentName.toUpperCase === continentVal.countryName.toUpperCase).delete
  }

  def deleteCountry(continentVal : Country) = db.run{
    continent.filter(_.continentName.toUpperCase === continentVal.continentName.toUpperCase).delete
  }

  def deleteContinent(continentVal : Continent) = db.run{
    val cities = for{
      continent <- getAll.filter(_.continentName.equalsIgnoreCase(continentVal.continentName))
      country <- getAllCountries.filter(_.continentName.equalsIgnoreCase(continent.continentName))
      city <- getAllCities.filter(_.countryName.equalsIgnoreCase(country.countryName))
    } yield city
    for (x <- cities){
      db.run{
        city.filter(_.countryName.toUpperCase === x.countryName.toUpperCase).delete
      }
    }
    val countryList = getAllCountries.filter(_.continentName.equalsIgnoreCase(continentVal.continentName))
    for (x <- countryList){
      db.run{
        country.filter(_.continentName.toUpperCase === x.continentName.toUpperCase).delete
      }
    }
    continent.filter(_.continentName.toUpperCase === continentVal.continentName.toUpperCase).delete
  }


}