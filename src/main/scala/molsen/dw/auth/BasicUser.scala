package molsen.dw.auth

import java.security.Principal
import java.util

class BasicUser(val name: String, val roles: util.Set[String]) extends Principal {
  override def equals(another: Any): Boolean = false

  override def toString: String = null

  override def hashCode: Int = 0

  override def getName: String = null

  def getRoles: util.Set[String] = roles
}
