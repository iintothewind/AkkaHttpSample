package ivar.http
package serdes

import spray.json.RootJsonFormat

sealed case class HelloMsg(id: Int, msg: String)

trait HelloSerdes extends AbstractSerdes {
  implicit val helloMsgFormat: RootJsonFormat[HelloMsg] = jsonFormat2(HelloMsg.apply)
}

