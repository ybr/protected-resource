package com.github.ybr.protectr

object Fixtures {
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

    implicit val publicResourceReadRight: RightChecker[PublicResource, User, Read] = new RightChecker[PublicResource, User, Read] {
      def check(resource: PublicResource, credentials: User): ResourceAccess[PublicResource] = Granted(resource)
    }

    implicit val otherResourceWriteRight: RightChecker[PublicResource, User, Write] = new RightChecker[PublicResource, User, Write] {
      def check(resource: PublicResource, credentials: User): ResourceAccess[PublicResource] = {
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
  case class PublicResource(owner: String, content: Int)
}