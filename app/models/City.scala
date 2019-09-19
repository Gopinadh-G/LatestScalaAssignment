package models

import play.api.libs.json.Json

case class City(id : Long,cityName:String,countryName:String)

object City{
  implicit val cityReads=Json.reads[City]
  implicit val cityWrites=Json.writes[City]
}

case class CityRef(cityName:String,countryName:String)

object CityRef{
  implicit val cityReads=Json.reads[CityRef]
  implicit val cityWrites=Json.writes[CityRef]
}
