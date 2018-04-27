package ivar.http
package server.route

import java.io.File

import akka.http.scaladsl.server.Directives._

trait UploadRoutes {
  register() = path("upload") {
    (post & storeUploadedFile("image", fileInfo => new File(fileInfo.getFieldName))) { case (fileInfo, file) =>
      file.delete
      complete(s"Successfully upload ${fileInfo.fieldName}:${fileInfo.fileName}")
    }
  }
}
