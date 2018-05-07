package ivar.test.serdes

import akka.http.scaladsl.unmarshalling.Unmarshal
import ivar.http._
import ivar.http.serdes.{News, ZhihuSerdes}
import org.junit.Test

import scala.util.{Failure, Success}

class ZhihuSerdesTest extends ZhihuSerdes {
  val latestJson =
    """
{
   "date": "20180507",
   "stories": [
      {
         "images": [
            "https://pic3.zhimg.com/v2-e8e4359df52a0c81dd4480f1021a161a.jpg"
         ],
         "type": 0,
         "id": 9680479,
         "ga_prefix": "050709",
         "title": "听说「红色食物」有助于减肥，看来我必须得试试了"
      },
      {
         "images": [
            "https://pic3.zhimg.com/v2-3dd5a7e0108cf38c619ff5038aa5dade.jpg"
         ],
         "type": 0,
         "id": 9680974,
         "ga_prefix": "050708",
         "title": "从「不做手游」到 「手游大厂」，任天堂只用了 3 年"
      },
      {
         "images": [
            "https://pic1.zhimg.com/v2-7bd1399dd2721ef5ab8d44e6d91ff978.jpg"
         ],
         "type": 0,
         "id": 9680824,
         "ga_prefix": "050707",
         "title": "我女朋友爱玩的游戏，这画风跟我玩的也差太多了……"
      },
      {
         "images": [
            "https://pic1.zhimg.com/v2-b985bdb8560ac0e9dc7e225828a01d30.jpg"
         ],
         "type": 0,
         "id": 9680815,
         "ga_prefix": "050707",
         "title": "只把自己定位成「IT 民工」，你永远成不了一名出色的数据分析师"
      },
      {
         "images": [
            "https://pic1.zhimg.com/v2-67517da366f624aab2644754d876f170.jpg"
         ],
         "type": 0,
         "id": 9681443,
         "ga_prefix": "050706",
         "title": "瞎扯 · 如何正确地吐槽"
      }
   ],
   "top_stories": [
      {
         "image": "https://pic3.zhimg.com/v2-8dbc228fd527af1eca0e9278014b77f2.jpg",
         "type": 0,
         "id": 9680824,
         "ga_prefix": "050707",
         "title": "我女朋友爱玩的游戏，这画风跟我玩的也差太多了……"
      },
      {
         "image": "https://pic2.zhimg.com/v2-b2edd95598e442e2b9ecf47e1e6b3e95.jpg",
         "type": 0,
         "id": 9680815,
         "ga_prefix": "050707",
         "title": "只把自己定位成「IT 民工」，你永远成不了一名出色的数据分析师"
      },
      {
         "image": "https://pic1.zhimg.com/v2-0db2156c7d835ca875675901fb2cecdc.jpg",
         "type": 0,
         "id": 9680479,
         "ga_prefix": "050709",
         "title": "听说「红色食物」有助于减肥，看来我必须得试试了"
      },
      {
         "image": "https://pic1.zhimg.com/v2-74c8697edab19e112bd1bfbb832c53d0.jpg",
         "type": 0,
         "id": 9681419,
         "ga_prefix": "050609",
         "title": "本周热门精选 · 「被陨落」的奥数天才"
      },
      {
         "image": "https://pic4.zhimg.com/v2-910daa5cf5375b920e0aff9dae670db3.jpg",
         "type": 0,
         "id": 9680858,
         "ga_prefix": "050607",
         "title": "为什么大多数人宁愿吃生活的苦，也不愿吃学习的苦？"
      }
   ]
}
      """.stripMargin

  @Test
  def testUnmarshal(): Unit = {
    Unmarshal("42").to[Int].onComplete {
      case Success(n) => n == 42
      case Failure(e) => throw e
    }
  }

  @Test
  def testUnmarshalLatest(): Unit = {
    Unmarshal(latestJson).to[News].onComplete {
      case Success(news) => news.stories.foreach(println(_))
      case Failure(e) => throw e
    }

  }

}
