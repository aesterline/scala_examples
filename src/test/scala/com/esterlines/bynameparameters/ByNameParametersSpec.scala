package com.esterlines.bynameparameters

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ByNameParametersSpec extends FlatSpec with ShouldMatchers {
  behavior of "By-Name Parameters"

  it should "evaluate the parameter each time it is used" in {
    val messaging = new MessageComponent()

    Logging.log(messaging.generateMessage())

    messaging.numberOfMessages should equal(2)
  }

  it should "only evaluate the parameter when it is used not when the method is called" in {
    val messaging = new MessageComponent()

    Logging.doNothing(messaging.generateMessage())

    messaging.numberOfMessages should equal(0)
  }

  it should  "work evaluate before the method call if the parameter is not a by-name parameter" in {
    val messaging = new MessageComponent()

    Logging.notByName(messaging.generateMessage())

    messaging.numberOfMessages should equal(1)
  }
}

object Logging {
  def log(msg: => String):String = {
    printMessage(msg)
    msg
  }

  def doNothing(msg: => String) {}
  def notByName(msg:String) {}

  private def printMessage(msg:String) {
    // Do nothing.   I didn't want to string printed to STDOUT during a test run.
  }
}

class MessageComponent {
  var numberOfMessages = 0

  def generateMessage():String = {
    numberOfMessages += 1
    "Generated Message"
  }
}