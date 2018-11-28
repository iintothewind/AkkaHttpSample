package ivar.http
package server.route

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import ivar.http.serdes.{HelloMsg, HelloSerdes}

trait HelloRoutes extends HelloSerdes {
  val divByZeroHandler = ExceptionHandler {
    case _: ArithmeticException => complete(StatusCodes.BadRequest -> "You've got your arithmetic wrong, fool!")
  }

  routeBuf() = (get & path("hello")) {
    complete("Hello, Akka Http")
  }

  routeBuf("test") = (get & path("hello")) {
    complete("Hello, Akka Http")
  }

  routeBuf() = (get & pathPrefix("name") & path(Segment)) { name =>
    complete(ToResponseMarshallable(s"Your name is: $name"))
  }

  routeBuf() = (post & path("helloMessage") & entity(as[HelloMsg])) { msg =>
    complete(msg)
  }

  routeBuf() = (get & path("divide" / IntNumber / IntNumber)) { (a, b) =>
    handleExceptions(divByZeroHandler) {
      complete(s"The result is ${a / b}")
    }
  }

}
