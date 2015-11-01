package com.github.ybr.protectr

trait ResourceAccess[+R]
case class Granted[R](value: R) extends ResourceAccess[R]
case object Denied extends ResourceAccess[Nothing]