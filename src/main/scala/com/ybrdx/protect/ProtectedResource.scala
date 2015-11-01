package com.github.ybr.protectr

class ProtectedResource[R](resource: R) {
  def grant[Right] = new ApplyRightChecker[R, Right] {
    def apply[C](credentials: C)(implicit checker: RightChecker[R, C, Right]): ResourceAccess[R] = checker.check(resource, credentials)
  }
}

object ProtectedResource {
  def apply[R](resource: R): ProtectedResource[R] = new ProtectedResource(resource)
}

trait ApplyRightChecker[R, Right] {
  def apply[C](credentials: C)(implicit checker: RightChecker[R, C, Right]): ResourceAccess[R]
}