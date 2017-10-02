package molsen.dw

import javax.annotation.security.{PermitAll, RolesAllowed}
import javax.ws.rs._
import javax.ws.rs.core.MediaType

@Path("basic-auth")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.APPLICATION_JSON))
class BasicAuthTestResource {

  @GET
  @Path("test1")
  @PermitAll
  def test1(): String = {
    "test1 success"
  }

  @GET
  @Path("test2")
  @RolesAllowed(Array("TestRole2"))
  def test2(): String = {
    "test2 success"
  }

  @GET
  @Path("test3")
  @RolesAllowed(Array("TestRole1", "TestRole2"))
  def test3(): String = {
    "test3 success"
  }


}