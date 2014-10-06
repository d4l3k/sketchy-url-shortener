package lc.fn.sketchy

import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class SketchySpec extends MutableScalatraSpec {

  addServlet(classOf[lc.fn.sketchy.Sketchy], "/*")

  "GET / on HelloWorldServlet" should {
    "return status 200" in {
      get("/") {
        status must_== 200
      }
    }
  }
}
