package com.phenan.scalalr

sealed trait TokenList
case class TokenListCons [+A, +B <: TokenList] (head: A, tail: B) extends TokenList
sealed trait TokenListSentinel extends TokenList
case object TokenListSentinel extends TokenListSentinel
