package molsen.dw

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.porch.commons.response.{ApiError, ApiResponse, ValidationError}
import com.porch.dropwizard.scala.service.AsyncResource

import scala.collection.JavaConverters._


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

  val validationErrors = Seq(new ValidationError("id", "id is required"), new ValidationError("name", "That's a crap name"))

  // SUCCEED

  @GET
  @Path("success/string")
  def respondWithString(): ApiResponse[String] = ApiResponse.ok("Hello Test")

  @GET
  @Path("success/dto")
  def respondWithDto(): ApiResponse[MyDTO] = ApiResponse.ok(MyDTO(1L, "Thing1"))

  // FAIL

  @GET
  @Path("failure/bad-request")
  def respondBadRequest(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.BAD_REQUEST)

  @GET
  @Path("failure/conflict")
  def respondConflict(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.CONFLICT)

  @GET
  @Path("failure/forbidden")
  def respondForbidden(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.FORBIDDEN)

  @GET
  @Path("failure/not-found")
  def respondNotFound(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.NOT_FOUND)

  @GET
  @Path("failure/unauthorized")
  def respondUnauthorized(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.UNAUTHORIZED)

  @GET
  @Path("failure/validation-error")
  def respondBadRequestWithMessage(): ApiResponse[MyDTO] = ApiResponse.failed(new ApiError(ApiError.BAD_REQUEST, "Validation failed", validationErrors.toList.asJava))

  @GET
  @Path("failure/ise")
  def responseISE(): ApiResponse[MyDTO] = ApiResponse.failed(ApiError.SERVER_ERROR)

  // THROW

  @GET
  @Path("throw/bad-request")
  def throwBadRequest(): ApiResponse[MyDTO] = {
    throw new BadRequestException("That didn't make sense")
  }

  @GET
  @Path("throw/conflict")
  def throwConflict(): ApiResponse[MyDTO] = {
    throw new WebApplicationException("OMG CONFLICT!", Response.Status.CONFLICT)
  }

  @GET
  @Path("throw/forbidden")
  def throwForbidden(): ApiResponse[MyDTO] = {
    throw new ForbiddenException("You can't have that")
  }

  @GET
  @Path("throw/not-found")
  def throwNotFound(): ApiResponse[MyDTO] = {
    throw new NotFoundException("I didn't find it")
  }

  @GET
  @Path("throw/unauthroized")
  def throwUnauthorized(): ApiResponse[MyDTO] = {
    throw new WebApplicationException("Who are you?", Response.Status.UNAUTHORIZED)
  }

  @GET
  @Path("throw/validation-error")
  def throwValidationError(): ApiResponse[MyDTO] = {
    throw new BadRequestException(s"One of your things has the wrong thing:\n\n${validationErrors.map(e => e.getMessage).mkString("\n")}")
  }

  @GET
  @Path("throw/ise")
  def throwISE(): ApiResponse[MyDTO] = {
    throw new InternalServerErrorException("EXPLODE!")
  }

  @GET
  @Path("throw/wae")
  def throwWAE(): ApiResponse[MyDTO] = {
    throw new WebApplicationException("EXPLODE!")
  }

  // BREAK

  @GET
  @Path("npe")
  def npe(): ApiResponse[MyDTO] = {
    val myDto: MyDTO = null
    ApiResponse.ok(MyDTO(myDto.id, myDto.name))
  }

}

case class MyDTO
(
  id: Long,
  name: String
)