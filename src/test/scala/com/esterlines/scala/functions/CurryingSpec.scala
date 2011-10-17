package com.esterlines.scala.functions

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class CurryingSpec extends FlatSpec with ShouldMatchers {
  behavior of "Function Currying"

  it should "allow applying of parameters after the initial call" in {
    val addTwo = Service.add(2)(_)

    addTwo(3) should equal(5)
  }

  it should "remember object state in curryied function" in {
    val adder = new Adder(5)
    val addTwo = adder.add(2)(_)

    addTwo(4) should equal(11)
  }

//  it should "allow regular functions to be curried" in {
//    val curried = Service.simpleAdd.curried
//
//    curried(3)(5) should equal(8)
//  }
}

object Service {
  def simpleAdd(a:Int, b:Int):Int = a + b
  def add(a:Int)(b:Int):Int = a + b
}

class Adder(initial:Int) {
  def add(a:Int)(b:Int):Int = initial + a + b
}