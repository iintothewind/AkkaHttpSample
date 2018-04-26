package ivar.http.server.route

import java.io.File

import akka.http.scaladsl.server.Directives._

trait UploadRoutes extends AbstractRoutes {
  private val upload = path("upload") {
    (post & storeUploadedFile("image", fileInfo => new File(fileInfo.getFieldName))) { case (fileInfo, file) =>
      file.delete
      complete(s"Successfully upload ${fileInfo.fieldName}:${fileInfo.fileName}")
    }
  }

  register(upload)
}