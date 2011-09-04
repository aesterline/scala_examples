package com.esterlines.passwordcomplexity

object IsPasswordComplex {
  def apply(password:String, rules:List[(String) => Boolean] = DEFAULT_RULES, requiredRules:Int = 2) = {
    password match {
      case "" => false
      case null => false
      case _ => rules.count((rule) => rule(password)) == requiredRules
    }
  }

  val containsNumber = (word:String) => word.matches(".*\\d.*")
  val containsLowercase = (word:String) => word.matches(".*[a-z].*")
  val containsUppercase = (word:String) => word.matches(".*[A-Z].*")
  val containsSpecial = (word:String) => word.matches(".*[@!~#$].*")

  private val DEFAULT_RULES = List(containsLowercase, containsUppercase, containsNumber, containsSpecial)
}