package ivar.http
package server.event

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait AbstractEvent extends SprayJsonSupport with DefaultJsonProtocol
