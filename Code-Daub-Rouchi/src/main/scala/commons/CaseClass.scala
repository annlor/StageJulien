package commons


case class TraductionPicard(mot: String, abreviation: String, definitions: Seq[String])

case class ListTraductionPicard(ListTraductions: List[TraductionPicard])

case class TraductionFrançaise(mot: String, complement: Option[String],
                                 abreviation: String, traduction: Seq[String])

case class ArticlePicard(Entrée: String, StructureGrammaticale: String, Lexies: Seq[String])

case class Dictionnaire(Article: Seq[ArticlePicard])

