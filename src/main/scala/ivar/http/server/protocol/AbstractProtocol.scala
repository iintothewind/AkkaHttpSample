package ivar.http
package server.protocol

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait AbstractProtocol extends SprayJsonSupport with DefaultJsonProtocol
