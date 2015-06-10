package com.ybrdx.protect

trait RightChecker[R, C, Right] {
  def check(resource: R, credentials: C): ResourceAccess[R]
}