package models

import play.api.libs.json.Json


case class Country(id : Long ,countryName:String,continentName:String)

object Country{
  implicit val countryReads=Json.reads[Country]
  implicit val countryWrites=Json.writes[Country]
}

case class CountryRef(countryName:String,continentName:String)

object CountryRef{
  implicit val countryReads=Json.reads[CountryRef]
  implicit val countryWrites=Json.writes[CountryRef]
}

