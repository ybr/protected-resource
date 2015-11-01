package com.github.ybr

import scalaz._
import scalaz.Scalaz._

package object protectr {
  implicit val resourceAccessMonad = new Monad[ResourceAccess] {
    def point[A](a: => A) = Granted(a)
    def bind[A, B](ma: ResourceAccess[A])(f: A => ResourceAccess[B]) = ma match {
      case Granted(a) => f(a)
      case Denied => Denied
    }
  }
}