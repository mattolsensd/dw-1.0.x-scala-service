package molsen.dw

import javax.ws.rs.container.{AsyncResponse, Suspended}
import javax.ws.rs.core.MediaType
import javax.ws.rs.{WebApplicationException, _}

import com.porch.commons.response.ApiResponse
import com.porch.dropwizard.scala.service.AsyncResource
import io.swagger.annotations.Api
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


// TEST VARIOUS METHODS OF HANDLING OF FUTURES IN A RESOURCE
// Compare blocking with Await.result to AsyncResource.synchronous
// Compare returning asynchronous result instead of blocking


@Api
@Path("test/futures")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.APPLICATION_JSON))
class FuturesResource extends AsyncResource {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val defaultTimeout: Duration = Duration(30, "seconds")

  private val LOG: Logger = LoggerFactory.getLogger(classOf[FuturesResource])

  // Block with Await.result

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

  // Block with AsyncResource.synchronous

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

  // Return async with AsyncResource.async

  @GET
  @Path("async/success")
  def testAsyncSuccess()(implicit @Suspended response: AsyncResponse) = async[ApiResponse[String]] {
    Future.apply(ApiResponse.ok("Hello World"))
  }

  @GET
  @Path("async/failure")
  def testAsyncFailure()(implicit @Suspended response: AsyncResponse) = async[ApiResponse[String]] {
    Future.failed(new WebApplicationException("BOOM"))
  }

}
