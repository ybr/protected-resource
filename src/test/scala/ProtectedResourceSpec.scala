package com.ybrdx.protect

import scalaz.Scalaz._

import org.specs2._

class ProtectedResourceSpec extends Specification {
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
    protectedResource.grant[Rights.Read](me) must beTypedEqualTo(Granted(resource))
  }

  def denyReadAccess = {
    val me = User("me")
    val otherResource = OtherResource("you", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    protectedOtherResource.grant[Rights.Read](me) must beTypedEqualTo(Denied)
  }

  def monadiclyGrantReadAccess = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val otherResource = OtherResource("me", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    val access = for {
      r2 <- protectedOtherResource.grant[Rights.Read](me)
      r1 <- protectedResource.grant[Rights.Read](me)
    } yield (r1, r2)
    access must beTypedEqualTo(Granted(resource -> otherResource))
  }

  def monadiclyDenyReadAccess = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val otherResource = OtherResource("you", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    val access = for {
      r2 <- protectedOtherResource.grant[Rights.Read](me)
      r1 <- protectedResource.grant[Rights.Read](me)
    } yield (r1, r2)
    access must beTypedEqualTo(Denied)
  }

  def monadiclyDenyWriteAccessFirst = {
    val me = User("me")
    val resource = Resource("you", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val otherResource = OtherResource("me", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Write](me)
      r2 <- protectedOtherResource.grant[Rights.Write](me)
    } yield (r1, r2)
    access must beTypedEqualTo(Denied)
  }

  def monadiclyDenyWriteAccessSecond = {
    val me = User("me")
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val otherResource = OtherResource("you", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Write](me)
      r2 <- protectedOtherResource.grant[Rights.Write](me)
    } yield (r1, r2)
    access must beTypedEqualTo(Denied)
  }

  def monadiclyGrantAdminReadAccess = {
    val me = Admin()
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val otherResource = OtherResource("you", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Read](me)
      r2 <- protectedOtherResource.grant[Rights.Read](me)
    } yield (r1, r2)
    access must beTypedEqualTo(Granted(resource -> otherResource))
  }

  def monadiclyGrantAdminWriteAccess = {
    val me = Admin()
    val resource = Resource("me", "some highly valuable data")
    val protectedResource = ProtectedResource(resource)
    val otherResource = OtherResource("you", 7)
    val protectedOtherResource = ProtectedResource(otherResource)
    val access = for {
      r1 <- protectedResource.grant[Rights.Write](me)
      r2 <- protectedOtherResource.grant[Rights.Write](me)
    } yield (r1, r2)
    access must beTypedEqualTo(Granted(resource -> otherResource))
  }
}

object Rights {
  trait MyAppRight
  case class Read() extends MyAppRight
  case class Write() extends MyAppRight

  implicit val resourceReadRight: RightChecker[Resource, User, Read] = new RightChecker[Resource, User, Read] {
    def check(resource: Resource, credentials: User): ResourceAccess[Resource] = Granted(resource)
  }

  implicit val resourceWriteRight: RightChecker[Resource, User, Write] = new RightChecker[Resource, User, Write] {
    def check(resource: Resource, credentials: User): ResourceAccess[Resource] = {
      if(resource.owner == credentials.pseudo) Granted(resource)
      else Denied
    }
  }

  implicit val otherResourceReadRight: RightChecker[OtherResource, User, Read] = new RightChecker[OtherResource, User, Read] {
    def check(resource: OtherResource, credentials: User): ResourceAccess[OtherResource] = {
      if(resource.owner == credentials.pseudo) Granted(resource)
      else Denied
    }
  }

  implicit val otherResourceWriteRight: RightChecker[OtherResource, User, Write] = new RightChecker[OtherResource, User, Write] {
    def check(resource: OtherResource, credentials: User): ResourceAccess[OtherResource] = {
      if(resource.owner == credentials.pseudo) Granted(resource)
      else Denied
    }
  }

  implicit def anyResourceAdminRight[A, B <: MyAppRight]: RightChecker[A, Admin, B] = new RightChecker[A, Admin, B] {
    def check(resource: A, credentials: Admin): ResourceAccess[A] = Granted(resource)
  }
}

case class Admin()
case class User(pseudo: String)
case class Resource(owner: String, content: String)
case class OtherResource(owner: String, content: Int)