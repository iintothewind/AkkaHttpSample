package ivar.http
package server.protocol

import spray.json.RootJsonFormat

case class HelloMessage(id: Int, msg: String)

trait HelloProtocol extends AbstractProtocol {
  implicit val helloMessageFormat: RootJsonFormat[HelloMessage] = jsonFormat2(HelloMessage.apply)
}
