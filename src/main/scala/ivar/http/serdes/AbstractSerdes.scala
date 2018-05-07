package ivar.http
package serdes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait AbstractSerdes extends SprayJsonSupport with DefaultJsonProtocol
