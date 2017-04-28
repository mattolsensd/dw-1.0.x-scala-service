package molsen.dw

import javax.ws.rs._
import javax.ws.rs.container.{AsyncResponse, Suspended}
import javax.ws.rs.core.{MediaType, Response}

import com.porch.commons.response.{ApiError, ApiResponse, ValidationError}
import com.porch.dropwizard.scala.service.AsyncResource
import com.porch.partner.auth.{Secure, SecurePartnerDTO}

import scala.collection.JavaConverters._
import scala.concurrent.Future


// TEST API RESPONSES, EXCEPTION MAPPING, AND RESPONSE STATUS CODES

// TLDR: Throw WAEs that map to failed ApiResponses; Never return a failed ApiResponse directly

// If DefaultExceptionMappingBundle is added, throwing any Exception that extends WAE will be mapped to an ApiResponse
// and the response status code will be whatever was specified in the WAE

// Returning a failed ApiResponse will result in a 200 status code (just like returning any other value)
// If, for some reason, you want to return a failure response directly, with proper status code, you can do something like:
// Response.status(Response.Status.BAD_REQUEST).entity(ApiResponse.failed(ApiError.BAD_REQUEST, "The request was bad")).build()
// There's no reason to do this if our exception mappers are registered


@Path("test")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.APPLICATION_JSON))
class MyResource extends AsyncResource {

  import scala.concurrent.ExecutionContext.Implicits.global


  @GET
  @Path("auth")
  def secureEndpoint(@Secure partner: SecurePartnerDTO): ApiResponse[SecurePartnerDTO] = ApiResponse.ok(partner)

  // SUCCEED

  @GET
  @Path("success/string")
  def respondWithString(): ApiResponse[String] = ApiResponse.ok(MyResource.MY_STRING)

  @GET
  @Path("success/dto")
  def respondWithDto(): ApiResponse[MyDTO] = ApiResponse.ok(MyResource.MY_DTO)

  @GET
  @Path("success/future/dto/sync")
  def respondWithFutureDto(): ApiResponse[MyDTO] = synchronous[ApiResponse[MyDTO]] {
    Future.apply(ApiResponse.ok(MyResource.MY_DTO))
  }

  @GET
  @Path("success/future/dto/async")
  def respondWithAsyncFutureDto()(implicit @Suspended response: AsyncResponse): Unit = async[ApiResponse[MyDTO]] {
    Future.apply(ApiResponse.ok(MyResource.MY_DTO))
  }

  // FAIL

  @GET
  @Path("failure/bad-request")
  def respondBadRequest(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.BAD_REQUEST, MyResource.MY_BAD_REQUEST_MESSAGE)

  @GET
  @Path("failure/conflict")
  def respondConflict(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.CONFLICT, MyResource.MY_CONFLICT_MESSAGE)

  @GET
  @Path("failure/forbidden")
  def respondForbidden(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.FORBIDDEN, MyResource.MY_FORBIDDEN_MESSAGE)

  @GET
  @Path("failure/not-found")
  def respondNotFound(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.NOT_FOUND, MyResource.MY_NOT_FOUND_MESSAGE)

  @GET
  @Path("failure/unauthorized")
  def respondUnauthorized(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.UNAUTHORIZED, MyResource.MY_UNAUTHORIZED_MESSAGE)

  @GET
  @Path("failure/validation-error")
  def respondValidationError(): ApiResponse[MyDTO] = ApiResponse.failed(new ApiError(MyResource.MY_VALIDATION_ERRORS.toList.asJava))

  @GET
  @Path("failure/validation-error-with-custom-message")
  def respondValidationErrorWithCustomMessage(): ApiResponse[MyDTO] = ApiResponse.failed(new ApiError(ApiError.VALIDATION_ERROR, MyResource.MY_VALIDATION_ERROR_MESSAGE, MyResource.MY_VALIDATION_ERRORS.toList.asJava))

  @GET
  @Path("failure/ise")
  def respondISE(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.SERVER_ERROR, MyResource.MY_ISE_MESSAGE)

  // THROW

  @GET
  @Path("throw/bad-request")
  def throwBadRequest(): ApiResponse[MyDTO] = {
    throw new BadRequestException(MyResource.MY_BAD_REQUEST_MESSAGE)
  }

  @GET
  @Path("throw/conflict")
  def throwConflict(): ApiResponse[MyDTO] = {
    throw new WebApplicationException(MyResource.MY_CONFLICT_MESSAGE, Response.Status.CONFLICT)
  }

  @GET
  @Path("throw/forbidden")
  def throwForbidden(): ApiResponse[MyDTO] = {
    throw new ForbiddenException(MyResource.MY_FORBIDDEN_MESSAGE)
  }

  @GET
  @Path("throw/not-found")
  def throwNotFound(): ApiResponse[MyDTO] = {
    throw new NotFoundException(MyResource.MY_NOT_FOUND_MESSAGE)
  }

  @GET
  @Path("throw/unauthroized")
  def throwUnauthorized(): ApiResponse[MyDTO] = {
    throw new WebApplicationException(MyResource.MY_UNAUTHORIZED_MESSAGE, Response.Status.UNAUTHORIZED)
  }

  @GET
  @Path("throw/validation-error")
  def throwValidationError(): ApiResponse[MyDTO] = {
    throw new MyValidationException(MyResource.MY_VALIDATION_ERRORS)
  }

  @GET
  @Path("throw/validation-error-with-custom-message")
  def throwValidationErrorWithCustomMessage(): ApiResponse[MyDTO] = {
    throw new MyValidationException(MyResource.MY_VALIDATION_ERRORS, Some(MyResource.MY_VALIDATION_ERROR_MESSAGE))
  }

  @GET
  @Path("throw/ise")
  def throwISE(): ApiResponse[MyDTO] = {
    throw new InternalServerErrorException(MyResource.MY_ISE_MESSAGE)
  }

  @GET
  @Path("throw/wae")
  def throwWAE(): ApiResponse[MyDTO] = {
    throw new WebApplicationException(MyResource.MY_WAE_MESSAGE)
  }

  // BREAK

  @GET
  @Path("npe")
  def npe(): ApiResponse[MyDTO] = {
    val myDto: MyDTO = null
    ApiResponse.ok(MyDTO(myDto.id, myDto.name))
  }

}

object MyResource {
  val MY_STRING: String = "Hello Test"

  val MY_DTO: MyDTO = MyDTO(1L, "Thing1")

  val MY_BAD_REQUEST_MESSAGE: String = "The server cannot or will not process the request due to an apparent client error (e.g., malformed request syntax, too large size, invalid request message framing, or deceptive request routing)."
  val MY_CONFLICT_MESSAGE: String = "Indicates that the request could not be processed because of conflict in the request, such as an edit conflict between multiple simultaneous updates."
  val MY_FORBIDDEN_MESSAGE: String = "The request was valid, but the server is refusing action. The user might not have the necessary permissions for a resource."
  val MY_NOT_FOUND_MESSAGE: String = "The requested resource could not be found but may be available in the future. Subsequent requests by the client are permissible."
  val MY_UNAUTHORIZED_MESSAGE: String = "Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided."
  val MY_ISE_MESSAGE: String = "A generic error message, given when an unexpected condition was encountered and no more specific message is suitable."
  val MY_WAE_MESSAGE: String = "Throwing a WebApplicationException will also result in a server error response"

  // Validation Errors are a special form of badRequest response
  val MY_VALIDATION_ERROR_MESSAGE: String = "Request validation failed"

  val MY_VALIDATION_ERRORS = Seq(new ValidationError("id", "id is required"), new ValidationError("name", "That's a crap name"))

  val LOGGING_EXCEPTION_MAPPER_MESSAGE = "There was an error processing your request. It has been logged"
}

case class MyDTO
(
  id: Long,
  name: String
)