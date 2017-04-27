package molsen.dw

import java.net.URI
import javax.ws.rs.client.{Client, WebTarget}
import javax.ws.rs.core.GenericType

import com.porch.commons.response.ApiResponse
import com.porch.dropwizard.scala.client.ScalaApiClient

import scala.concurrent.{ExecutionContext, Future}

class MyClient(val baseUri: URI, val client: Client) extends ScalaApiClient(baseUri, client) {

  private val STRING_RESPONSE = new GenericType[ApiResponse[String]]() {}
  private val MY_DTO_RESPONSE = new GenericType[ApiResponse[MyDTO]]() {}

  def getWebTarget: WebTarget = baseTarget.path("test")

  def respondWithString()(implicit ec: ExecutionContext): Future[ApiResponse[String]] =
    request(STRING_RESPONSE).from(getWebTarget.path("success/string")).get

  def respondWithDto()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("success/dto")).get

  def respondWithFutureDto()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("success/future/dto/sync")).get

  def respondWithAsyncFutureDto()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("success/future/dto/async")).get

  // FAIL

  def respondBadRequest()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/bad-request")).get

  def respondConflict()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/conflict")).get

  def respondForbidden()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/forbidden")).get

  def respondNotFound()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/not-found")).get

  def respondUnauthorized()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/unauthorized")).get

  def respondValidationError()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/validation-error")).get

  def respondValidationErrorWithCustomMessage()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/validation-error-with-custom-message")).get

  def respondISE()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("failure/ise")).get

  // THROW

  def throwBadRequest()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/bad-request")).get

  def throwConflict()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/conflict")).get

  def throwForbidden()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/forbidden")).get

  def throwNotFound()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/not-found")).get

  def throwUnauthorized()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/unauthroized")).get

  def throwValidationError()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/validation-error")).get

  def throwISE()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/ise")).get

  def throwWAE()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    request(MY_DTO_RESPONSE).from(getWebTarget.path("throw/wae")).get

  // BREAK

  def npe()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] = {
    request(MY_DTO_RESPONSE).from(getWebTarget.path("npe")).get
  }

}
