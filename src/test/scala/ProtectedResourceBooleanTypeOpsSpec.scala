package com.github.ybr.protectr

import com.github.ybr.protectr.BooleanTypeOps._
import Fixtures._
import org.specs2._
import scalaz.Scalaz._

object ProtectedResourceBooleanTypeOpsSpec extends Specification {
  def is = s2"""
  This is the specification for ProtectedResource
    grant read or write access                          $grantReadOrWriteAccessToResource
    grant read and write access                         $grantReadAndWriteAccessToResource
    deny not read access                                $denyNotReadAccessToResource
    grant not not read access                           $grantNotNotReadAccessToResource
    grant read or write access to public resource       $grantReadOrWriteAccessToPublicResource
    deny read and write access to public resource       $denyReadAndWriteAccessToPublicResource
    grant read and not write access to public resource  $grantReadAndNotWriteAccessToPublicResource
  """

  def grantReadOrWriteAccessToResource = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[Rights.Read || Rights.Write](me) must beEqualTo(Granted(resource))
  }

  def grantReadAndWriteAccessToResource = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[Rights.Read && Rights.Write](me) must beEqualTo(Granted(resource))
  }

  def denyNotReadAccessToResource = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[![Rights.Read]](me) must beEqualTo(Denied)
  }

  def grantNotNotReadAccessToResource = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[![![Rights.Read]]](me) must beEqualTo(Granted(resource))
  }

  def grantReadOrWriteAccessToPublicResource = {
    val me = User("me")
    val resource = PublicResource("you", 7)
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[Rights.Read || Rights.Write](me) must beEqualTo(Granted(resource))
  }

  def denyReadAndWriteAccessToPublicResource = {
    val me = User("me")
    val resource = PublicResource("you", 7)
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[Rights.Read && Rights.Write](me) must beEqualTo(Denied)
  }

  def grantReadAndNotWriteAccessToPublicResource = {
    val me = User("me")
    val resource = PublicResource("you", 7)
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[Rights.Read && ![Rights.Write]](me) must beEqualTo(Granted(resource))
  }
}