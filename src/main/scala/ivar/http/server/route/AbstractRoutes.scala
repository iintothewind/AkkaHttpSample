package ivar.http
package server.route

import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MissingFormFieldRejection, _}
import akka.http.scaladsl.server.directives.BasicDirectives.{extractRequestContext => _, provide => _, _}
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.directives.FutureDirectives.{onSuccess => _, _}
import akka.http.scaladsl.server.directives.MarshallingDirectives.{as => _, entity => _, _}
import akka.http.scaladsl.server.directives.RouteDirectives.{reject => _, _}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString

import scala.concurrent.ExecutionContextExecutor

trait AbstractRoutes {

  def extractFormdata(fieldName: String): Directive1[(FileInfo, Source[ByteString, Any])] =
    entity(as[Multipart.FormData]).flatMap { formData ⇒
      extractRequestContext.flatMap { ctx ⇒
        implicit val mat: Materializer = ctx.materializer
        implicit val ec: ExecutionContextExecutor = ctx.executionContext

        val onePartSource: Source[(FileInfo, Source[ByteString, Any]), Any] = formData.parts
          .filter(part ⇒ part.filename.isDefined && part.name == fieldName)
          .map(part ⇒ (FileInfo(part.name, part.filename.get, part.entity.contentType), part.entity.dataBytes))
          .take(1)

        val onePartF = onePartSource.runWith(Sink.headOption[(FileInfo, Source[ByteString, Any])])

        onSuccess(onePartF)
      }

    }.flatMap {
      case Some(tuple) ⇒ provide(tuple)
      case None ⇒ reject(MissingFormFieldRejection(fieldName))
    }


  def register(moreRoutes: Route*): Unit = {
    routes.append(moreRoutes.map(pathPrefix("api")(_)): _*)
  }

  def registerAll(moreRoutes: Seq[Route]): Unit = {
    routes.appendAll(moreRoutes.map(pathPrefix("api")(_)))
  }
}
