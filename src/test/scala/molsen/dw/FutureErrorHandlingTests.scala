package molsen.dw

import java.util.concurrent.TimeUnit
import javax.ws.rs.{InternalServerErrorException, WebApplicationException}

import com.porch.commons.response.ApiResponse
import org.scalatest.{BeforeAndAfterEach, FunSpec}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}


// TEST VARIOUS METHODS OF HANDLING ERRORS IN FUTURES


class FutureErrorHandlingTests extends FunSpec with BeforeAndAfterEach {

  import scala.concurrent.ExecutionContext.Implicits.global

  describe("For Comprehension") {

    it("should yield a result if futures succeed") {
      val eventualResponse = for {
        result <- Future.apply("Hello World")
      } yield ApiResponse.ok(result)

      val result = Await.result(eventualResponse, FutureErrorHandlingTests.defaultTimeout)

      assertResult(ApiResponse.ok("Hello World"))(result)
    }

    it("should throw an exception if a future fails") {
      val eventualResponse = for {
        result <- Future.failed(new Exception("BOOM"))
      } yield ApiResponse.ok(result)

      intercept[Exception] {
        Await.result(eventualResponse, FutureErrorHandlingTests.defaultTimeout)
      }
    }

  }

  describe("Try and Match") {

    it("should execute success case if future succeeds") {
      Try(Await.result(Future.apply("Hello World"), FutureErrorHandlingTests.defaultTimeout)) match {
        case Success(f) => println("Success!")
        case Failure(e) => throw new Exception("Should not execute")
      }
    }

    it("should execute Failure case if future fails") {
      Try(Await.result(Future.failed(new Exception("BOOM")), FutureErrorHandlingTests.defaultTimeout)) match {
        case Success(f) => throw new Exception("Should not execute")
        case Failure(e) => println("Failed as expected")
      }
    }

  }

  describe("Future onFailure callback") {

    it("should execute if future fails") {
      val f: Future[String] = Future.failed(new WebApplicationException("BOOM"))
      f.onFailure({ case (e) => println("Executing onFailure") })

      intercept[WebApplicationException] {
        ApiResponse.ok(Await.result(f, FutureErrorHandlingTests.defaultTimeout))
      }
    }

    it("should execute in a separate thread") {
      val f: Future[String] = Future.failed(new WebApplicationException("BOOM"))
      // onFailure executes on another thread, so the original WAE bubbles up but ISEE is not intercepted
      f.onFailure({ case (e) =>
        println(s"Executing onFailure callback on thread ${Thread.currentThread().getName}")
        throw new InternalServerErrorException("My onFailure code failed!", e)
      })

      intercept[WebApplicationException] {
        Await.result(f, FutureErrorHandlingTests.defaultTimeout)
      }
    }

  }

  describe("Future recover") {

    it("should not execute if future succeeds") {
      val f: Future[String] = Future.apply("Hello World") recover {
        case (e: WebApplicationException) =>
          throw new Exception("Should not execute")
      }
      val result = Await.result(f, FutureErrorHandlingTests.defaultTimeout)
      assertResult("Hello World")(result)
    }

    it("should execute if future fails") {
      val f: Future[String] = Future.failed(new WebApplicationException("BOOM")) recover {
        case (e: WebApplicationException) =>
          println(s"Recover Caught ${e.getClass}")
          "Hello World"
      }
      val result = Await.result(f, FutureErrorHandlingTests.defaultTimeout)
      assertResult("Hello World")(result)
    }

    it("can be used to wrap or transform an exception") {
      val f: Future[String] = Future.failed(new WebApplicationException("BOOM")) recover {
        case (e: WebApplicationException) =>
          throw new InternalServerErrorException("My future failed!", e)
      }

      intercept[InternalServerErrorException] {
        Await.result(f, FutureErrorHandlingTests.defaultTimeout)
      }
    }
  }

}

object FutureErrorHandlingTests {

  private final val defaultTimeout = Duration(3, TimeUnit.SECONDS)

}
