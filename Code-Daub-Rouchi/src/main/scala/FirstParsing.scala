/**
  * Created by khamphousone on 6/22/17.
  */
import scala.io.Source
import java.io._

import fastparse.all._
import scala.xml._


/**
  * Cette case classe contriendra les éléments de chaque ligne
  */
case class TraductionPicard(mot : String, abreviation : String, definitions : Seq[String])
case class ListTraductionPicard(ListTraductions : List[TraductionPicard])

/**
  * Classe permettant de définir les différents parsings
  */
class FirstParsing {
  /**
    * Classe permettant de définir les parsings unitaires
    */
  class UnitParser() {

    val UpperCaseLetter = P((CharIn('A' to 'Z') | "Ê" | "É" | "À" | "È" | "Ï" | "'" | "!").rep(min = 1).!)
    val LowerCaseLetter = P(CharIn('a' to 'z', " ", "-", "é", "à", "ù", "ç", "è", "ê", "â", "û", "ï", "î").rep(min = 1).!)
    val Letters = P((UpperCaseLetter | LowerCaseLetter).rep(min = 1).!)
    val Parenthesis = P(("(" | ")" | " ").rep(sep = UpperCaseLetter).?.!)
    val Numbers = P(CharIn('0' to '9').!)
    val NumbersDef = P((Numbers ~ " -").!)
    val Ponctuation = P(("'" | "." | "-" | "," | "(" | ")" | "\"" | "/" | ";" | ":" | ")." | "=" | "!").rep(min = 1).!)
    val Definitions = P(Letters.rep(min = 1, sep = Ponctuation).!)
    val PoncDefinitions = P(Ponctuation.? ~ Definitions ~ Ponctuation.?)

  }

  /**
    *
    * @param str String : chaîne de caractères à parser
    * @return String après parsage
    */
  def Parser(str: String): TraductionPicard = {
    val UP = new UnitParser()

    val MotPicard = P(("* ".? ~ UP.Letters.rep(sep = UP.Parenthesis | ",".? ~ " ou ") ~ " ".? ~ UP.Parenthesis).!)
    val Abreviation = P((UP.LowerCaseLetter ~ ".").rep(min = 1, sep = " ").!)



    val Debut: Parser[(String, String, Seq[String])] = P(MotPicard.! ~ ", ".? ~ Abreviation.?.! ~ " : ".?
  ~ (UP.PoncDefinitions | UP.NumbersDef ~ UP.PoncDefinitions).!.rep(min = 1))


   Debut.parse(str) match {
     case Parsed.Success((str1: String, str2: String, seq: Seq[String]), _) => TraductionPicard(str1, str2, seq)
     case Parsed.Failure(_, i: Int, _) => TraductionPicard("Failure", s"$i", Seq(""))
     case Parsed.Success(_,_) => TraductionPicard("Error","",Seq(""))

   }

  }
  class Trad(mot : String, abreviation : String, definitions : Seq[String]){

    def SeqtoXml(seq : Seq[String]) : Seq[Elem] ={
      val res = for (elements <- seq) yield {
        <definition>{elements}</definition>
      }
      return res
    }

    def toXml =
      <TraductionPicard>
        <mot>{mot}</mot>
        <abreviation>{abreviation}</abreviation>
        {SeqtoXml(definitions)}
      </TraductionPicard>


  }
}


object Demo {


  def main(args: Array[String]): Unit = {

    val buff: Source = Source.fromFile("/people/khamphousone/Documents/Dictionnaires/daub_rouchi_197S_CU.txt")
    val writer = new PrintWriter(new File("/people/khamphousone/Documents/ParsersScala/FileParsers.txt"))
    val Parsing = new FirstParsing()
    val UP = new Parsing.UnitParser()


    val CaseClassePicard = new ListTraductionPicard(List())
    val liste1 = for (line <- buff.getLines.slice(1213, 7446)) yield {
      Parsing.Parser(line)
    }

    val liste2 = liste1.toList
    val listAZ = for (elements <- ('A' to 'Z').toList) yield {
      val listTrad = for (trad <- liste2 if (trad.mot.startsWith(s"$elements") | trad.mot.startsWith(s"* $elements"))) yield {
        val x:Boolean = trad.mot.startsWith(s"$elements")
          println(s"$x")
          val Traduction = new Parsing.Trad(trad.mot, trad.abreviation, trad.definitions)
          Traduction.toXml


      }

      val listXml = <Traduction>{listTrad}</Traduction>

      XML.save(s"/people/khamphousone/Documents/ParsersScala/XML/$elements",listXml,"utf-8",true,null)

      listTrad
    }





  }
}