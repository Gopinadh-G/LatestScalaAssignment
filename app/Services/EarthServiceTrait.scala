package Services

import models._

import scala.concurrent.Future

trait EarthServiceTrait {

  def createContinent(name: String): String

  def addCitiesToTheCountry(newCities:Array[CityRef]) : String
  def createCountry(countryVal: CountryRef): String
  def getContinentOfACountry(countryName:String):String

  def checkIfTwoCountryLiesInSameContinent(first:String,second:String) : String

  def getAllCitiesOfContinent(continent:String):Seq[City]

  def groupCitiesByFirstLetter: List[(String,Seq[City])]

  def continentList(): Future[Seq[Continent]]
  def countryList(): Future[Seq[Country]]

  def deleteContinent(continentName : String) : String
}
