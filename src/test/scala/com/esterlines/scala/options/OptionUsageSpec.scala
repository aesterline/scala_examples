package com.esterlines.scala.options

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class OptionUsageSpec extends FlatSpec with ShouldMatchers {
  behavior of "Options"

  it should "return Some when the value is not null" in {
    Option("Me") should equal(Some("Me"))
  }

  it should "return None when the value is null" in {
    Option(null) should equal(None)
  }

  /*
  Using a match with an Option doesn't seem like the idiomatic way.
  It seems like it can be useful, but almost all sources I have seen say
  it is not the idiomatic way.
   */
  it should "match some" in {
    val uppercase = Service.find("me") match {
      case Some(name) => Some(name.toUpperCase)
      case _ => None
    }

    uppercase should equal(Some("ME"))
  }

  it should "match nothing" in {
    val uppercase = Service.find[String](null) match {
      case Some(name) => Some(name.toUpperCase)
      case _ => None
    }

    uppercase should equal(None)
  }

  /*
  Using an Option as a monad seems to be the more idiomatic way.   Using it this way,
  you don't have to worry about the None case.   If any piece along the way evaluates to None
  the whole expression will be None.
   */
  it should "work in a for comprehension" in {
    val uppercase = for {
      name <- Service.find("adam")
      upper <- Some(name.toUpperCase)
    } yield upper

    uppercase.get should equal("ADAM")
  }

  it should "work even when the value is None" in {
    val uppercase = for {
      name <- Service.find[String](null)
      upper <- Some(name.toUpperCase)
    } yield upper

    uppercase should equal(None)
  }

  /*
  This can be useful when working with nested Options
   */
  it should "return the lastname when it has a value" in {
    val user = new User("Adam", Some("Esterline"))

    val surname = for {
      u <- Service.find(user)
      lastName <- u.lastName
    } yield lastName

    surname should equal(Some("Esterline"))
  }

  it should "return none when user is not found" in {
    val surname = for {
      u <- Service.find[User](null)
      lastName <- u.lastName
    } yield lastName

    surname should equal(None)
  }

  it should "return none when lastName is none" in {
    val user = new User("Adam", None)

    val surname = for {
      u <- Service.find(user)
      lastName <- u.lastName
    } yield lastName

    surname should equal(None)
  }

  object Service {
    def find[T](name:T):Option[T] = {
      Option(name)
    }
  }

  case class User(firstName:String, lastName:Option[String])
}