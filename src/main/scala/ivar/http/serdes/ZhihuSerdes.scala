package ivar.http.serdes

import spray.json.{JsObject, JsString, JsValue, RootJsonFormat, _}

sealed case class Story(id: Int, title: String, share_url: Option[String], body: Option[String])

sealed case class News(date: String, stories: List[Story], topStories: List[Story])


trait ZhihuSerdes extends AbstractSerdes {
  implicit val storySerdes: RootJsonFormat[Story] = jsonFormat4(Story.apply)
  implicit val newsSerdes: RootJsonFormat[News] = new RootJsonFormat[News] {
    override def write(news: News): JsValue =
      JsObject(
        "date" -> JsString(news.date),
        "stories" -> news.stories.toJson,
        "top_stories" -> news.topStories.toJson)

    override def read(json: JsValue): News =
      json.asJsObject.getFields("date", "stories", "top_stories") match {
        case Seq(JsString(date), stories, topStories) =>
          News(date, stories.convertTo[List[Story]], topStories.convertTo[List[Story]])
        case _ => deserializationError("unsupported format")
      }
  }
}






