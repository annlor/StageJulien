/**
  * Created by khamphousone on 6/26/17.
  */

package commons

import fastparse.all._


class UnitParser() {
  /**
    * Classe permettant de définir les parsings unitaires
    */

  val UpperCaseLetter = P((CharIn('A' to 'Z') | "Ê" | "É" | "À" | "È" | "Ï" | "'" | "!" | "Ç").rep(min = 1).!)
  val LowerCaseLetter = P(CharIn('a' to 'z', " ", "-", "é", "à", "ù", "ç", "è", "ê", "â", "û", "ï", "î", "ü", "ô").rep(min = 1).!)
  val Letters = P((UpperCaseLetter | LowerCaseLetter).rep(min = 1).!)
  val Parenthesis = P(("(" | ")" | " "| ",".? ~ " ou" | ";" | ".").rep(sep = UpperCaseLetter).?.!)
  val Numbers = P(CharIn('0' to '9').!)
  val Date = P(Numbers.rep(min = 2).!)
  val NumbersDef = P((Numbers ~ " -").!)
  val Ponctuation = P(("'" | "." | "-" | "," | "(" | ")" | "\"" | "/" | ";" | ":" | ")." | "=" | "!" | "?").rep(min = 1).!)
  val Definitions = P(Letters.rep(min = 1, sep = (Ponctuation | Date).rep).!)
  val PoncDefinitions = P(Ponctuation.? ~ (Definitions ~ Date.?).! ~ Ponctuation.rep)
  val Abreviation = P((LowerCaseLetter ~ ".").rep(min = 1, sep = " ").!)

}