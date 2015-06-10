package com.ybrdx.protect

trait ResourceAccess[+R]
case class Granted[R](value: R) extends ResourceAccess[R]
case object Denied extends ResourceAccess[Nothing]