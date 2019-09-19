package models

import play.api.libs.json.Json

case class StringResponse(msg:String)

case class StringResponseRef(continent:Seq[Continent],msg:String)

object StringResponse{
  implicit val responseReads =Json.reads[StringResponse]
  implicit val responseWrites =Json.writes[StringResponse]
}

object StringResponseRef{
  implicit val responseReads =Json.reads[StringResponseRef]
  implicit val responseWrites =Json.writes[StringResponseRef]
}
