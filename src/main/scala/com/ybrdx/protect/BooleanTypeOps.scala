package com.github.ybr.protectr

object BooleanTypeOps {
  type ![A] = A => Nothing
  type &&[A, B] = A with B
  type ||[A, B] = ![![A] && ![B]]

  implicit def notRightChecker[R, C, Right](implicit rc: RightChecker[R, C, Right]) = new RightChecker[R, C, ![Right]] {
    def check(resource: R, credentials: C): ResourceAccess[R] = rc.check(resource, credentials) match {
      case Granted(_) => Denied
      case Denied => Granted(resource)
    }
  }

  implicit def andRightChecker[R, C, Right1, Right2](implicit rc1: RightChecker[R, C, Right1], rc2: RightChecker[R, C, Right2]) = new RightChecker[R, C, Right1 && Right2] {
    def check(resource: R, credentials: C): ResourceAccess[R] = rc1.check(resource, credentials) match {
      case Granted(r) => rc2.check(resource, credentials)
      case Denied => Denied
    }
  }

  implicit def orRightChecker[R, C, Right1, Right2](implicit rc1: RightChecker[R, C, Right1], rc2: RightChecker[R, C, Right2]) = new RightChecker[R, C, Right1 || Right2] {
    def check(resource: R, credentials: C): ResourceAccess[R] = rc1.check(resource, credentials) match {
      case Granted(r) => Granted(r)
      case Denied => rc2.check(resource, credentials)
    }
  }
}