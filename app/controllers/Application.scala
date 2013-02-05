package controllers

import play.api._
import libs.concurrent.Promise
import libs.{ws, oauth}
import libs.oauth._
import libs.oauth.ConsumerKey
import libs.oauth.OAuth
import libs.oauth.RequestToken
import libs.oauth.ServiceInfo
import libs.ws.WS
import play.api.mvc._
import io.Source
import collection.{mutable, Set}
import collection.immutable.HashSet

object Application extends Controller {

    val LINKEDIN_KEY = new ConsumerKey("vfpkgpqp4agn", "MQcsUJZ7Vm3xk8ji")
    val APP_TOKEN = RequestToken("0aafc360-7562-4e31-a176-d89aa34becb5", "7c52482c-dc45-405a-9c76-7bfa688c871c")

    var LINKEDIN = OAuth(ServiceInfo(
        "https://api.linkedin.com/uas/oauth/requestToken",
        "https://api.linkedin.com/uas/oauth/accessToken",
        "http://api.linkedin.com/uas/oauth/authenticate",
        LINKEDIN_KEY
    ));
  
    def index = Action { implicit request =>
        val postalCodes: Set[String] = getPostalCodes()

        APP_TOKEN match {
            case t: RequestToken => {

//                postalCodes.foreach((p: String) => {
//                    WS.url("http://api.linkedin.com/v1/people-search?keywords=Java&country-code=fr&postal-code=" + p)
//                })
                val resp: Promise[Response] = WS.url("http://api.linkedin.com/v1/people-search?keywords=Java&country-code=fr&postal-code=92800")
                        .sign(OAuthCalculator(LINKEDIN_KEY, APP_TOKEN))
                        .get()

//                        WS.url("http://api.linkedin.com/v1/people-search?keywords=Java&country-code=fr&postal-code=92800")
//                            .sign(OAuthCalculator(LINKEDIN_KEY, APP_TOKEN))
//                            .get
//                            .map(resp => Ok(resp.xml))
//                    })
            }
            case _ => Ok(views.html.index("Your new application is ready."))
        }
    }

    def getPostalCodes(): Set[String] = {
        val lines: Iterator[String] = Source.fromFile("communes.txt").getLines()
        var postalCodes: Set[String] = new HashSet[String]()

        lines.foreach((l: String) => {
            postalCodes += l.split("\t")(1)
        })

        postalCodes = postalCodes.filter(s => s.toInt % 1000 == 0)

        postalCodes.foreach(println)

        return postalCodes
    }
  
}