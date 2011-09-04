package com.esterlines.passwordcomplexity

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.esterlines.passwordcomplexity.IsPasswordComplex._

class IsPasswordComplexSpec extends FlatSpec with ShouldMatchers {
  def alwaysTrue(password:String) = {true}
  def alwaysFalse(password:String) = {false}

  it should  "be complex when the required number of rules pass" in {
    IsPasswordComplex("abc", List(alwaysTrue), 1) should equal(true)
    IsPasswordComplex("abc!2", List(alwaysFalse), 1) should equal(false)
    IsPasswordComplex("abc!!", List(alwaysTrue, alwaysTrue), 2) should equal(true)
    IsPasswordComplex("abc!#@", List(alwaysTrue, alwaysFalse), 2) should equal(false)

    IsPasswordComplex("", List(alwaysTrue), 1) should equal(false)
    IsPasswordComplex(null, List(alwaysTrue), 1) should equal(false)
  }

  it should "contain when password contains at least one number" in {
    containsNumber("a23") should equal(true)
    containsNumber("abc") should equal(false)
  }

  it should "contain when password contains at least one lowercase letter" in {
    containsLowercase("aBC") should equal(true)
    containsLowercase("ABC") should equal(false)
  }

  it should  "contain when password contains at least one uppercase letter" in  {
    containsUppercase("Abc") should equal(true)
    containsUppercase("abc") should equal(false)
  }

  it should "contain when password contains at least one special character" in {
    containsSpecial("a@b") should equal(true)
    containsSpecial("a~b") should equal(true)
    containsSpecial("a!b") should equal(true)
    containsSpecial("a#b") should equal(true)
    containsSpecial("a$b") should equal(true)
    containsSpecial("abc") should equal(false)
  }

  it should "be complex using the default two rules" in {
    IsPasswordComplex("aBc") should equal(true)
    IsPasswordComplex("a1c") should equal(true)
    IsPasswordComplex("a@b") should equal(true)
    IsPasswordComplex("1@2") should equal(true)
    IsPasswordComplex("abc") should equal(false)
  }

}