package com.ybrdx.protect

class ProtectedResource[R](private val resource: R) {
  def grant[Right] = new ApplyRightChecker[R, Right] {
    def apply[C](credentials: C)(implicit checker: RightChecker[R, C, Right]): ResourceAccess[R] = checker.check(resource, credentials)
  }
}

trait ApplyRightChecker[R, Right] {
  def apply[C](credentials: C)(implicit checker: RightChecker[R, C, Right]): ResourceAccess[R]
}

object ProtectedResource {
  def apply[R](resource: R): ProtectedResource[R] = new ProtectedResource(resource)
}