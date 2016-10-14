package molsen.dw

import javax.ws.rs.core.MediaType
import javax.ws.rs.{WebApplicationException, _}

import com.porch.commons.response.{ApiError, ApiResponse}
import com.porch.dropwizard.scala.service.AsyncResource
import io.swagger.annotations.Api
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

@Api
@Path("test/futures")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.APPLICATION_JSON))
class FuturesResource extends AsyncResource {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val defaultTimeout: Duration = Duration(30, "seconds")

  private val LOG: Logger = LoggerFactory.getLogger(classOf[FuturesResource])

  @GET
  @Path("await/success")
  def testAwaitSuccess(): ApiResponse[String] = {
    ApiResponse.ok(Await.result(Future.apply("Hello World"), defaultTimeout))
  }

  @GET
  @Path("await/failure")
  def testAwaitFailure(): ApiResponse[String] = {
    ApiResponse.ok(Await.result(Future.failed(new WebApplicationException("BOOM")), defaultTimeout))
  }

  @GET
  @Path("sync/success")
  def testSyncSuccess() = synchronous[ApiResponse[String]] {
    Future.apply(ApiResponse.ok("Hello World"))
  }

  @GET
  @Path("sync/failure")
  def testSyncFailure() = synchronous[ApiResponse[String]] {
    Future.failed(new WebApplicationException("BOOM"))
  }

  @GET
  @Path("for-comprehension/success")
  def testForComprehensionSuccess() = synchronous[ApiResponse[String]] {
    for {
      result <- Future.apply("Hello World")
    } yield ApiResponse.ok(result)
  }

  @GET
  @Path("for-comprehension/failure")
  def testForComprehensionFailure() = synchronous[ApiResponse[String]] {
    for {
      result <- Future.failed(new Exception("BOOM"))
    } yield ApiResponse.ok(result)
  }

  @GET
  @Path("try-and-return/success")
  def testTryAndReturnSuccess(): ApiResponse[String] = {
    Try(Await.result(Future.apply("Hello World"), defaultTimeout)) match {
      case Success(f) => ApiResponse.ok(f)
      case Failure(e) => ApiResponse.failed(new ApiError(ApiError.SERVER_ERROR, e.getMessage))
    }
  }

  @GET
  @Path("try-and-return/failure")
  def testTryAndReturnFailure(): ApiResponse[String] = {
    Try(Await.result(Future.failed(new WebApplicationException("BOOM")), defaultTimeout)) match {
      case Success(f) => ApiResponse.ok(f)
      case Failure(e) => ApiResponse.failed(new ApiError(ApiError.SERVER_ERROR, e.getMessage))
    }
  }

  @GET
  @Path("try-and-throw/success")
  def testTryAndThrowSuccess(): ApiResponse[String] = {
    Try(Await.result(Future.apply("Hello World"), defaultTimeout)) match {
      case Success(dto) => ApiResponse.ok(dto)
      case Failure(e) => throw new InternalServerErrorException("My future failed!", e)
    }
  }

  @GET
  @Path("try-and-throw/failure")
  def testTryAndThrowFailure(): ApiResponse[String] = {
    Try(Await.result(Future.failed(new WebApplicationException("BOOM")), defaultTimeout)) match {
      case Success(dto) => ApiResponse.ok(dto)
      case Failure(e) => throw new InternalServerErrorException("My future failed!", e)
    }
  }

  @GET
  @Path("on-failure")
  def testOnFailure(): ApiResponse[String] = {
    val f: Future[String] = Future.failed(new WebApplicationException("BOOM"))
    // onFailure executes on another thread, so the original WAE bubbles up to the ApiResponse
    f.onFailure({ case (e) => throw new InternalServerErrorException("My future failed!", e) })
    ApiResponse.ok(Await.result(f, defaultTimeout))
  }

  @GET
  @Path("recover")
  def testRecover(): ApiResponse[String] = {
    val f: Future[String] = Future.failed(new WebApplicationException("BOOM")) recover {
      case (e: Throwable) =>
        LOG.info("Recover Caught Throwable " + e)
        throw new InternalServerErrorException("My future failed!", e)
    }
    ApiResponse.ok(Await.result(f, defaultTimeout))
  }

}
