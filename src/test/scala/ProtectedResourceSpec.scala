package com.github.ybr.protectr

import Fixtures._
import org.specs2._
import scalaz.Scalaz._

object ProtectedResourceSpec extends Specification {
  def is = s2"""
  This is the specification for ProtectedResource
    grant read access                     $grantReadAccess
    deny read access                      $denyReadAccess
    monadicly grant read access           $monadiclyGrantReadAccess
    monadicly deny read access            $monadiclyDenyReadAccess
    monadicly deny write access on first  $monadiclyDenyWriteAccessFirst
    monadicly deny write access on second $monadiclyDenyWriteAccessSecond
    monadicly grant read access to admin  $monadiclyGrantAdminReadAccess
    monadicly grant write access to admin $monadiclyGrantAdminWriteAccess
  """

  def grantReadAccess = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    protectedResource.grant[Rights.Read](me) must beEqualTo(Granted(resource))
  }

  def denyReadAccess = {
    val me = User("me")
    val publicResource = PublicResource("you", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    protectedPublicResource.grant[Rights.Read](me) must beEqualTo(Granted(publicResource))
  }

  def monadiclyGrantReadAccess = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val publicResource = PublicResource("me", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    val access = for {
      r2 <- protectedPublicResource.grant[Rights.Read](me)
      r1 <- protectedResource.grant[Rights.Read](me)
    } yield (r1, r2)
    access must beEqualTo(Granted(resource -> publicResource))
  }

  def monadiclyDenyReadAccess = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val publicResource = PublicResource("you", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    val access = for {
      r2 <- protectedPublicResource.grant[Rights.Read](me)
      r1 <- protectedResource.grant[Rights.Read](me)
    } yield (r1, r2)
    access must beEqualTo(Granted(resource -> publicResource))
  }

  def monadiclyDenyWriteAccessFirst = {
    val me = User("me")
    val resource = Resource("you", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val publicResource = PublicResource("me", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Write](me)
      r2 <- protectedPublicResource.grant[Rights.Write](me)
    } yield (r1, r2)
    access must beEqualTo(Denied)
  }

  def monadiclyDenyWriteAccessSecond = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val publicResource = PublicResource("you", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Write](me)
      r2 <- protectedPublicResource.grant[Rights.Write](me)
    } yield (r1, r2)
    access must beEqualTo(Denied)
  }

  def monadiclyGrantAdminReadAccess = {
    val me = Admin()
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val publicResource = PublicResource("you", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Read](me)
      r2 <- protectedPublicResource.grant[Rights.Read](me)
    } yield (r1, r2)
    access must beEqualTo(Granted(resource -> publicResource))
  }

  def monadiclyGrantAdminWriteAccess = {
    val me = Admin()
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val publicResource = PublicResource("you", 7)
    val protectedPublicResource = ProtectedResource(publicResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Write](me)
      r2 <- protectedPublicResource.grant[Rights.Write](me)
    } yield (r1, r2)
    access must beEqualTo(Granted(resource -> publicResource))
  }
}