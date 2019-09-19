package models

import play.api.libs.json.Json

case class Continent(id : Long,continentName:String)

object Continent{
  implicit val continentReads=Json.reads[Continent]
  implicit val continentWrites=Json.writes[Continent]
}
