<?php
/* ARC2 static class inclusion */ 

  include_once('semsol/ARC2.php'); 

$dbpconfig = array(
  "remote_store_endpoint" => "http://vmrestaure:3030/restaure/query",
   );
$store = ARC2::getRemoteStore($dbpconfig); 
if ($errs = $store->getErrors()) {
     echo "<h1>getRemoteSotre error<h1>" ;
  }

$output = $_POST["mot"];

if (preg_match('/([a-zA-Z()\s\',]+)/',$output)<1){
	$output = "Entrée invalide";
}

$query = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME:<http://www.w3.org/ns/lemon/lime#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>


SELECT DISTINCT  ?xcoord ?ycoord ?verbatim ?wrDef ?wrExPicard ?ANC ?speech2 ?gender2 ?CoordinateWR
WHERE {
  ?x ontolex:writtenRep \"$output\".
  ?y ontolex:lexicalForm ?x.
  {
   ?y skos:definition ?z2.
     ?z2 ontolex:writtenRep ?wrDef.
  }
UNION{
   ?y restaure:hasExample ?z3.
      ?z3 ontolex:writtenRep ?wrExPicard.
  }
  UNION {
        ?y restaure:etymology ?z5.
    ?z5 ontolex:writtenRep ?ANC.
  }
  UNION {
   ?y  restaure:verbatim ?z6.
    ?z6 ontolex:writtenRep ?verbatim
  }
  OPTIONAL {?y lexinfo:partOfSpeech ?speech.
  ?speech ontolex:writtenRep ?speech2}.
    OPTIONAL{?y lexinfo:gender ?gender.
  ?gender ontolex:writtenRep ?gender2}
 
  OPTIONAL {
          restaure:LexiqueDebrie LIME:entry ?y.
    restaure:LexiqueDebrie restaure:Coordinate ?coordinateAmiens.
      restaure:xAmiens ontolex:writtenRep ?CoordinateWR.
    ?coordinateAmiens restaure:xAmiens ?xcoord.
    ?coordinateAmiens restaure:yAmiens ?ycoord.
  }
  OPTIONAL {
{
      restaure:LexiqueDaubyRouchiFra LIME:entry ?y.
  	restaure:LexiqueDaubyRouchiFra restaure:Coordinate ?cValenciennes.
}
UNION
{
      restaure:LexiqueDaubyRouchiPicard LIME:entry ?y.
  	restaure:LexiqueDaubyRouchiPicard restaure:Coordinate ?cValenciennes.
}
     restaure:xValenciennes ontolex:writtenRep ?CoordinateWR.
    ?cValenciennes restaure:xValenciennes ?xcoord.
    ?cValenciennes restaure:yValenciennes ?ycoord.
  }
}
";


/* execute the query */
  $rows = $store->query($query, 'rows'); 
 
    if ($errs = $store->getErrors()) {
       echo "Query errors" ;
       print_r($errs);
    }



foreach($rows as $row){
	$wrDef[] = $row['wrDef'];
	$verbatim[] = $row['verbatim'];
	$wrExPicard[] = $row['wrExPicard'];
	$ANC[] = $row['ANC'];
	$speech[] = $row['speech2'];
	$gender[] = $row['gender2'];

	$xcoord[] = $row['xcoord'];
	$ycoord[] = $row['ycoord'];
	$CoordinateWR[] = $row['CoordinateWR'];
}

$querySeeAlso = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME:<http://www.w3.org/ns/lemon/lime#>

SELECT DISTINCT ?wrLink
WHERE {
  ?x ontolex:writtenRep \"$output\".
  ?y ontolex:lexicalForm ?x.

?y restaure:translatableAs ?o.
  ?o ontolex:writtenRep ?wrLink.
  ?s ontolex:lexicalForm ?o2

}";

 $rowsSeeAlso = $store->query($querySeeAlso, 'rows'); 
foreach($rowsSeeAlso as $row){
	$wrLink[] = $row['wrLink'];
}

$queryLinks = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME: <http://www.w3.org/ns/lemon/lime#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>


SELECT DISTINCT ?wrPicard ?gender2 ?speech2 ?xcoord ?ycoord ?CoordinateWR (GROUP_CONCAT(DISTINCT ?wrExPicard;SEPARATOR='\\n') as ?wrExPicardConcat) (GROUP_CONCAT(DISTINCT ?wrTraductionFrancaise;SEPARATOR='\\n') AS ?wrTraductionFrancaiseConcat) (GROUP_CONCAT(DISTINCT ?ANC;SEPARATOR='\\n') AS ?ANCConcat)
WHERE {
	?le ontolex:lexicalForm ?lf.
  ?lf ontolex:writtenRep \"$output\".
  ?lexique LIME:entry ?le.
    ?lf2 ontolex:writtenRep \"$output\".
  ?le2 restaure:translatableAs ?lf2.
  ?le2 ontolex:lexicalForm ?lf3.
  ?lf3 ontolex:writtenRep ?wrPicard.
      OPTIONAL{?le2 lexinfo:gender ?gender.
  ?gender ontolex:writtenRep ?gender2}
    OPTIONAL {?le2 lexinfo:partOfSpeech ?speech.
  ?speech ontolex:writtenRep ?speech2}.
   ?le2 restaure:hasExample ?z3.
      ?z3 ontolex:writtenRep ?wrExPicard.
  ?le2 skos:definition ?z4.
      ?z4 ontolex:writtenRep ?wrTraductionFrancaise.
    ?le2 restaure:etymology ?z5.
    ?z5 ontolex:writtenRep ?ANC.
  OPTIONAL {
          restaure:LexiqueDebrie LIME:entry ?le.
    restaure:LexiqueDebrie restaure:Coordinate ?coordinateAmiens.
      restaure:xAmiens ontolex:writtenRep ?CoordinateWR.
    ?coordinateAmiens restaure:xAmiens ?xcoord.
    ?coordinateAmiens restaure:yAmiens ?ycoord.
  }
  OPTIONAL {
{
  	restaure:LexiqueDaubyRouchiFra restaure:Coordinate ?cValenciennes.
}
UNION
{
  	restaure:LexiqueDaubyRouchiPicard restaure:Coordinate ?cValenciennes.
}
     restaure:xValenciennes ontolex:writtenRep ?CoordinateWR.
    ?cAmiens restaure:xValenciennes ?xcoord.
    ?cAmiens restaure:yValenciennes ?ycoord.
  }
     VALUES ?lexique {restaure:LexiqueDaubyRouchiFra restaure:LexiqueDaubyRouchiPicard restaure:LexiqueDebrie}
}

GROUP BY ?wrPicard ?gender2 ?speech2 ?xcoord ?ycoord ?CoordinateWR";
/*GROUP BY ?wrPicard ?gender ?speech";*/
/* execute the query */
  $rowsLinks = $store->query($queryLinks, 'rows'); 

foreach($rowsLinks as $row){
	$dataLinks[] = $row;


}

$wrDefkeys = array_combine($wrDef,$CoordinateWR);
$verbatimkeys = array_combine($verbatim,$CoordinateWR);
unset($wrDefkeys[null]);
$xcoordkeys = array_combine($CoordinateWR,$xcoord);
$ycoordkeys = array_combine($CoordinateWR,$ycoord);
$speechkeys = array_combine($CoordinateWR,$speech);
$genderkeys = array_combine($CoordinateWR,$gender);


if (count(array_filter($wrDef)) >0){
	$str2 = "Traduction :";
}

if (count(array_filter($wrLink)) >0){
	$strLink = "Recherchez aussi :<br>";
}


if (count(array_filter($wrExPicard)) >0){
	$str3 = "Exemple picard :";
}

if(count(array_filter($ANC)) >0){
	$str4 = "Ancienne étymologie : <br>";
}

echo "<h style = \"font-size:150%;\">{$output} ${str}</h>";
?>
