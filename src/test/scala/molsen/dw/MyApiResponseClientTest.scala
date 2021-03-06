package molsen.dw

import java.util.concurrent.TimeUnit
import javax.ws.rs.client.Client

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.porch.commons.response.ApiError
import com.porch.dropwizard.core.jersey.errors.{EarlyEofExceptionMapper, LoggingExceptionMapper}
import com.porch.dropwizard.core.jersey.jackson.JsonProcessingExceptionMapper
import com.porch.dropwizard.core.jersey.validation.ConstraintViolationExceptionMapper
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.junit.DropwizardClientRule
import org.junit.{Rule, Test}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class MyApiResponseClientTest {

  private def awaitResult[A](a: Future[A]): A = Await.result(a, Duration(1, TimeUnit.SECONDS))

  // Here we are running the actual resource class to test against
  // (In this case, it makes no external calls and returns static data)
  // Alternatively, we could build a mock resource class to test against
  // This would prevent downstream calls going to other services
  // and also allow us to specify (and predict) return values
  // Of course, when using a resource double, the test will not
  // catch path errors or problems caused by changes to the API
  // (You will have to update the mocks to reflect API changes)
  // Mocking is necessary to test a client for an external resource

  //@Path("test")
  //private class MyResourceDouble {
  //  @GET
  //  @Path("success/string")
  //  def respondWithString: ApiResponse[String] = ApiResponse.ok(MyResource.MY_STRING)
  //}

  private val dropwizard = new DropwizardClientRule(new MyResource,
    // Set up exception mapping to match the real service
    classOf[ConstraintViolationExceptionMapper],
    classOf[EarlyEofExceptionMapper],
    classOf[JsonProcessingExceptionMapper],
    classOf[LoggingExceptionMapper])

  // This is the only way I've found to use JUnit Rules in scala
  // @Rule cannot be applied to the val itself, so we apply it to a function

  // I did not find any way to use a @ClassRule in scala
  // The target must be both public and static
  // Even though the default scope is public, there is no public keyword, and this causes a problem
  // Furthermore, there is no static keyword, and the class rule does not seem to work on the companion object

  // Using @Rule instead of @ClassRule means that we will spin up a new FakeApplication for each test

  @Rule def DROPWIZARD: DropwizardClientRule = dropwizard

  private object MyClientProvider {
    assert(dropwizard.getEnvironment != null, "Dropwizard environment cannot be null!")
    dropwizard.getEnvironment.getObjectMapper.registerModule(DefaultScalaModule)
    val client: Client = new JerseyClientBuilder(dropwizard.getEnvironment).build("test-client")
    val myClient: MyClient = new MyApiResponseClient(dropwizard.baseUri, client)

    def get: MyClient = myClient
  }

  @Test
  def shouldReturnString() {
    awaitResult(MyClientProvider.get.respondWithString().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(apiResponse.isSuccess, "apiResponse should be successful")
      assert(apiResponse.getBody.isPresent, "apiResponse body should be present")
      val result: String = apiResponse.getBody.get()
      assert(MyResource.MY_STRING == result, "result should be my string")
    }))
  }

  @Test
  def shouldReturnDTO() {
    awaitResult(MyClientProvider.get.respondWithDto().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(apiResponse.isSuccess, "apiResponse should be successful")
      assert(apiResponse.getBody.isPresent, "apiResponse body should be present")
      val result: MyDTO = apiResponse.getBody.get()
      assert(MyResource.MY_DTO == result, "result should be my dto")
    }))
  }

  @Test
  def shouldReturnBadRequest() {
    awaitResult(MyClientProvider.get.respondBadRequest().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.BAD_REQUEST == error.getCode, s"error code should be ${ApiError.BAD_REQUEST}")
      assert(MyResource.MY_BAD_REQUEST_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_BAD_REQUEST_MESSAGE}")
    }))
  }

  @Test
  def shouldReturnConflict() {
    awaitResult(MyClientProvider.get.respondConflict().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.CONFLICT == error.getCode, s"error code should be ${ApiError.CONFLICT}")
      assert(MyResource.MY_CONFLICT_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_CONFLICT_MESSAGE}")
    }))
  }

  @Test
  def shouldReturnForbidden() {
    awaitResult(MyClientProvider.get.respondForbidden().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.FORBIDDEN == error.getCode, s"error code should be ${ApiError.FORBIDDEN}")
      assert(MyResource.MY_FORBIDDEN_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_FORBIDDEN_MESSAGE}")
    }))
  }

  @Test
  def shouldReturnNotFound() {
    awaitResult(MyClientProvider.get.respondNotFound().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.NOT_FOUND == error.getCode, s"error code should be ${ApiError.NOT_FOUND}")
      assert(MyResource.MY_NOT_FOUND_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_NOT_FOUND_MESSAGE}")
    }))
  }

  @Test
  def shouldReturnUnauthorized() {
    awaitResult(MyClientProvider.get.respondUnauthorized().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.UNAUTHORIZED == error.getCode, s"error code should be ${ApiError.UNAUTHORIZED}")
      assert(MyResource.MY_UNAUTHORIZED_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_UNAUTHORIZED_MESSAGE}")
    }))
  }

  @Test
  def shouldReturnValidationError() {
    awaitResult(MyClientProvider.get.respondValidationError().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.VALIDATION_ERROR == error.getCode, s"error code should be ${ApiError.VALIDATION_ERROR}")
      assert(error.getMessage.isEmpty, "error message should be empty")
      // TODO: Verify validation errors
    }))
  }

  @Test
  def shouldReturnValidationErrorWithCustomMessage() {
    awaitResult(MyClientProvider.get.respondValidationErrorWithCustomMessage().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.VALIDATION_ERROR == error.getCode, s"error code should be ${ApiError.VALIDATION_ERROR}")
      assert(MyResource.MY_VALIDATION_ERROR_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_VALIDATION_ERROR_MESSAGE}")
      // TODO: Verify validation errors
    }))
  }

  @Test
  def shouldReturnServerError() {
    awaitResult(MyClientProvider.get.respondISE().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.SERVER_ERROR == error.getCode, s"error code should be ${ApiError.SERVER_ERROR}")
      assert(MyResource.MY_ISE_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_ISE_MESSAGE}")
    }))
  }


  @Test
  def shouldAlsoReturnBadRequest() {
    awaitResult(MyClientProvider.get.throwBadRequest().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.BAD_REQUEST == error.getCode, s"error code should be ${ApiError.BAD_REQUEST}")
      assert(MyResource.MY_BAD_REQUEST_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_BAD_REQUEST_MESSAGE}")
    }))
  }

  @Test
  def shouldAlsoReturnConflict() {
    awaitResult(MyClientProvider.get.throwConflict().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.CONFLICT == error.getCode, s"error code should be ${ApiError.CONFLICT}")
      assert(MyResource.MY_CONFLICT_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_CONFLICT_MESSAGE}")
    }))
  }

  @Test
  def shouldAlsoReturnServerError() {
    awaitResult(MyClientProvider.get.throwISE().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.SERVER_ERROR == error.getCode, s"error code should be ${ApiError.SERVER_ERROR}")
      assert(MyResource.MY_ISE_MESSAGE == error.getMessage, s"error message should be ${MyResource.MY_ISE_MESSAGE}")
    }))
  }

  @Test
  def shouldReturnServerErrorWithLoggingExceptionMapperMessage() {
    awaitResult(MyClientProvider.get.npe().map(apiResponse => {
      assert(apiResponse != null, "apiResponse should not be null")
      assert(!apiResponse.isSuccess, "apiResponse should fail")
      assert(apiResponse.getError.isPresent, "apiResponse ApiError should be present")
      val error: ApiError = apiResponse.getError.get()
      assert(ApiError.SERVER_ERROR == error.getCode, s"error code should be ${ApiError.SERVER_ERROR}")
      assert(error.getMessage.contains(MyResource.LOGGING_EXCEPTION_MAPPER_MESSAGE), s"error message should contain ${MyResource.LOGGING_EXCEPTION_MAPPER_MESSAGE}")
    }))
  }

}
