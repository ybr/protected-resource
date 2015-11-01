package com.github.ybr.protectr

trait RightChecker[R, C, Right] {
  def check(resource: R, credentials: C): ResourceAccess[R]
}