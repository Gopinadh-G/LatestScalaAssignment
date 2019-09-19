package dao

import scala.concurrent.Future
import models._

trait EarthRepoTrait {


  def createContinent(name: String) : Future[Continent]


  def addCitiesToTheCountry(newCities:CityRef) : Future[Country]


  def createCountry(countryVal: CountryRef) : Future[Country]


  def continentList(): Future[Seq[Continent]]
  def countryList(): Future[Seq[Country]]
  def cityList(): Future[Seq[City]]

  def getAll: Seq[Continent]
  def getAllCountries: Seq[Country]
  def getAllCities: Seq[City]




}


