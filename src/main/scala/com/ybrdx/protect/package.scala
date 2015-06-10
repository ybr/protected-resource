package com.ybrdx

import scalaz._
import scalaz.Scalaz._

package object protect {
  implicit val com_ybrdx_protect_ResourceAccessMonad = new Monad[ResourceAccess] {
    def point[A](a: => A) = Granted(a)
    def bind[A, B](ma: ResourceAccess[A])(f: A => ResourceAccess[B]) = ma match {
      case Granted(a) => f(a)
      case Denied => Denied
    }
  }
}