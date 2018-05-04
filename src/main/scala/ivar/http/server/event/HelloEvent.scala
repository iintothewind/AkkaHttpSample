package ivar.http
package server.event

import spray.json.RootJsonFormat

sealed case class HelloMsg(id: Int, msg: String)

trait HelloEvent extends AbstractEvent {
  implicit val helloMsgFormat: RootJsonFormat[HelloMsg] = jsonFormat2(HelloMsg.apply)
}

