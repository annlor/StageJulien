/**
  * Created by khamphousone on 6/26/17.
  */

package commons

import fastparse.all._


class UnitParser() {
  /**
    * Classe permettant de définir les parsers unitaires
    */

  /**Lettres majuscules*/
  val UpperCaseLetter = P((CharIn('A' to 'Z')
    | "Ê" | "É" | "À" | "È" | "Ï" | "'" | "!" | "Ç").rep(min = 1).!)

  /**Lettres minuscules*/
  val LowerCaseLetter = P(CharIn('a' to 'z'
    , "-", "é", "à", "ù", "ç", "è", "ê", "â", "û", "ï", "î", "ü", "ô").rep(min = 1).!)
  val Letters = P((UpperCaseLetter | LowerCaseLetter | " ").rep(min = 1).!)

  /**Chiffre de 1 à 9*/
  val Numbers = P(CharIn('0' to '9').!)
  /**Date à deux chiffres minimum*/
  val Date = P(Numbers.rep(min = 2).!)

  val Parenthesis = P(("(" | ")" | " "| ",".? ~ " ou" | ";" | ".").rep(sep = UpperCaseLetter).?.!)

  val NumbersDef = P((Numbers ~ " -").!)

  val Ponctuation = P(("'" | "." | "-" | "," | "(" | ")" | "\"" | "/" | ";" | ":" | ")." | "=" | "!" | "?").rep(min = 1).!)

  val Definitions = P(Letters.rep(min = 1, sep = (Ponctuation | Date).rep).!)
  val PoncDefinitions = P(Ponctuation.? ~ (Definitions ~ Date.?).! ~ Ponctuation.rep)
  val Abreviation = P((LowerCaseLetter ~ ".").rep(min = 1, sep = " ").!)


  val LessPonctuation = P(("'" | "." | "-" | "(" | ")" | "\"" | "/" | ":" | ")." | "=" | "!" | "?").rep(min = 1).!)
  val LessDefinitions = P(Letters.rep(min = 1, sep = (LessPonctuation | Date).rep).!)
  val DefinitionsSecondParser = P(LessPonctuation.? ~ (LessDefinitions ~ Date.?).! ~ LessPonctuation.rep)
}

class XMLUnitParser{


  val UP = new UnitParser
  val XMLStructureGrammaticale = P(" ".? ~ (UP.LowerCaseLetter ~ ".").!.rep(min = 1, sep =" ") ~ " ".?)
}

class intermediateParser {
  val UP = new UnitParser
  val PonctuationNoQuotes = P(("'" | "." | "-" | "," | "(" | ")" | "/" | ";" | ":" | ")." | "=" | "!" | "?").rep(min = 1).!)
  val Definitions = P((PonctuationNoQuotes.? ~ UP.NumbersDef.? ~ PonctuationNoQuotes.? ~
    UP.Letters.rep(min = 1, sep = (PonctuationNoQuotes | UP.Date).rep) ~
    (PonctuationNoQuotes | UP.Date | " ").rep).!)
  val Auteur = P(("(" ~ UP.UpperCaseLetter ~ ")").!)
  val ExemplePicard = P((("ANC. : " | PonctuationNoQuotes).? ~ "\"" ~ UP.Letters.rep(sep = PonctuationNoQuotes) ~
    "\"" ~ " ".? ~ Auteur.? ~ " ".? ~ (("="|":") ~ Definitions).? ~
    ("(" ~ UP.Letters.rep(sep = PonctuationNoQuotes | UP.Date ~ ")")).? ~
    (PonctuationNoQuotes | " ").rep).!)
}