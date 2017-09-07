<?php
/* ARC2 static class inclusion */ 

  include_once('semsol/ARC2.php'); 

$dbpconfig = array(
  "remote_store_endpoint" => "http://vmrestaure:3030/restaureallwords/query",
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


SELECT DISTINCT ?xcoord ?ycoord ?wrTraductionForm ?wrDef ?wrExPicard ?wrTraductionFrancaise ?ANC ?speech2 ?gender2 ?CoordinateWR
WHERE {
  ?x ontolex:writtenRep \"$output\".
  ?y ontolex:lexicalForm ?x.
{
    ?y restaure:TranslatableAsForm ?z1.
      ?z1 ontolex:writtenRep ?wrTraductionForm.
  }
  UNION
  {
   ?y restaure:TranslatableAsDef ?z2.
      ?z2 ontolex:writtenRep ?wrDef.
  }
  UNION
  {
   ?y restaure:ExampleInPicard ?z3.
      ?z3 ontolex:writtenRep ?wrExPicard.
  }
  UNION
  {
   ?y restaure:TranslatableInFrench ?z4.
      ?z4 ontolex:writtenRep ?wrTraductionFrancaise.
  }
  UNION
  {
    ?y restaure:oldInformation ?z5.
    ?z5 ontolex:writtenRep ?ANC.
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

}";


$querySeeAlso = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME:<http://www.w3.org/ns/lemon/lime#>

SELECT DISTINCT ?wrLink
WHERE {
  ?x ontolex:writtenRep \"$output\".
  ?y ontolex:lexicalForm ?x.
{
?y restaure:TranslatableInFrenchOneWord ?o.
  ?o ontolex:writtenRep ?wrLink.
  ?o2 ontolex:writtenRep ?wrLink.
  ?s ontolex:lexicalForm ?o2
  } UNION {
       ?y restaure:TranslatableInPicardOneWord ?opcd.
      ?opcd ontolex:writtenRep ?wrLink.
      ?o2pcd ontolex:writtenRep ?wrLink.
  ?spcd ontolex:lexicalForm ?o2pcd     
	}

}";
/* execute the query */
  $rows = $store->query($query, 'rows'); 
 
    if ($errs = $store->getErrors()) {
       echo "Query errors" ;
       print_r($errs);
    }

 $rowsSeeAlso = $store->query($querySeeAlso, 'rows'); 
/*(GROUP_CONCAT(DISTINCT ?wrExPicard;separator='\\n') as ?wrExPicardConcat) (GROUP_CONCAT(DISTINCT ?wrTraductionFrancaise;separator='\\n') AS ?wrTraductionFrancaiseConcat) (GROUP_CONCAT(DISTINCT ?ANC;separator='\\n') AS ?ANCConcat)*/
$queryLinks = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME: <http://www.w3.org/ns/lemon/lime#>


SELECT ?wrPicard ?gender2 ?speech2 ?xcoord ?ycoord ?CoordinateWR (GROUP_CONCAT(DISTINCT ?wrExPicard;SEPARATOR='\\n') as ?wrExPicardConcat) (GROUP_CONCAT(DISTINCT ?wrTraductionFrancaise;SEPARATOR='\\n') AS ?wrTraductionFrancaiseConcat) (GROUP_CONCAT(DISTINCT ?ANC;SEPARATOR='\\n') AS ?ANCConcat)
WHERE {
	?le ontolex:lexicalForm ?lf.
  ?lf ontolex:writtenRep \"$output\".
  restaure:LexiqueDaubyRouchiFra LIME:entry ?le.
    ?lf2 ontolex:writtenRep \"$output\".
  ?le2 restaure:TranslatableInFrenchOneWord ?lf2.
  ?le2 ontolex:lexicalForm ?lf3.
  ?lf3 ontolex:writtenRep ?wrPicard.
      OPTIONAL{?le2 lexinfo:gender ?gender.
  ?gender ontolex:writtenRep ?gender2}
    OPTIONAL {?le2 lexinfo:partOfSpeech ?speech.
  ?speech ontolex:writtenRep ?speech2}.
   ?le2 restaure:ExampleInPicard ?z3.
      ?z3 ontolex:writtenRep ?wrExPicard.
  ?le2 restaure:TranslatableInFrench ?z4.
      ?z4 ontolex:writtenRep ?wrTraductionFrancaise.
    ?le2 restaure:oldInformation ?z5.
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
}

GROUP BY ?wrPicard ?gender2 ?speech2 ?xcoord ?ycoord ?CoordinateWR";
/*GROUP BY ?wrPicard ?gender ?speech";*/
/* execute the query */
  $rowsLinks = $store->query($queryLinks, 'rows'); 

foreach($rowsLinks as $row){
	$dataLinks[] = $row;


}
	foreach($dataLinks as $row){
	$dataCoordx[$row[CoordinateWR]] = $row[xcoord]; 
	$dataCoordy[$row[CoordinateWR]] = $row[ycoord]; 
}

foreach($rows as $row){
	$wrTraductionForm[] = $row['wrTraductionForm'];
	$wrDef[] = $row['wrDef'];
	$wrExPicard[] = $row['wrExPicard'];
	$wrTraductionFrancaise[] = $row['wrTraductionFrancaise'];
	$ANC[] = $row['ANC'];
	$speech[] = $row['speech2'];
	$gender[] = $row['gender2'];

	$xcoord[] = $row['xcoord'];
	$ycoord[] = $row['ycoord'];
	$CoordinateWR[] = $row['CoordinateWR'];
}



foreach($rowsSeeAlso as $row){
	$wrLink[] = $row['wrLink'];
}

$queryLinks2 = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME: <http://www.w3.org/ns/lemon/lime#>


SELECT ?wr ?wrFrench ?gender2 ?speech2 ?xcoord ?ycoord ?CoordinateWR (GROUP_CONCAT(DISTINCT ?wrForm;SEPARATOR='\\n') as ?wrFormConcat)
WHERE {
	?le ontolex:lexicalForm ?lf.
  ?lf ontolex:writtenRep \"$output\".
  {
  restaure:LexiqueDebrie LIME:entry ?le.
  }
  UNION
  {
  restaure:LexiqueDaubyRouchiPicard LIME:entry ?le.
  }
    ?lf2 ontolex:writtenRep \"$output\".
  ?le2 restaure:TranslatableInPicardOneWord ?lf2.
  ?le2 ontolex:lexicalForm ?lf3.
  ?lf3 ontolex:writtenRep ?wrFrench.
      OPTIONAL{?le2 lexinfo:gender ?gender.
  ?gender ontolex:writtenRep ?gender2}
    OPTIONAL {?le2 lexinfo:partOfSpeech ?speech.
  ?speech ontolex:writtenRep ?speech2}.
   ?le2 restaure:TranslatableAsForm ?z3.
      ?z3 ontolex:writtenRep ?wrForm.
  OPTIONAL {
    restaure:LexiqueDebrie restaure:Coordinate ?coordinateAmiens.
      restaure:xAmiensontolex:writtenRep ?CoordinateWR.
    ?coordinateAmiens restaure:xAmiens ?xcoord.
    ?coordinateAmiens restaure:yAmiens ?ycoord.
  }
  OPTIONAL {
{
      restaure:LexiqueDaubyRouchiFra LIME:entry ?le.
  	restaure:LexiqueDaubyRouchiFra restaure:Coordinate ?cValenciennes.
}
UNION
{
      restaure:LexiqueDaubyRouchiPicard LIME:entry ?le.
  	restaure:LexiqueDaubyRouchiPicard restaure:Coordinate ?cValenciennes.
}
     restaure:xValenciennes ontolex:writtenRep ?CoordinateWR.
    ?cValenciennes restaure:xValenciennes ?xcoord.
    ?cValenciennes restaure:yValenciennes ?ycoord.
  }

}

GROUP BY ?wrFrench ?gender2 ?speech2 ?wr ?ycoord ?xcoord ?CoordinateWR";
/* execute the query */
  $rowsLinks2 = $store->query($queryLinks2, 'rows'); 

foreach($rowsLinks2 as $row){
	$dataLinks2[] = $row;
}
foreach($dataLinks2 as $row){
	$dataCoordx[$row[CoordinateWR]] = $row[xcoord]; 
	$dataCoordy[$row[CoordinateWR]] = $row[ycoord]; 
}

$querySources = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX LIME: <http://www.w3.org/ns/lemon/lime#>


SELECT ?wrLexiqueName ?wrAuthor ?wrDirection
WHERE {
	?le ontolex:lexicalForm ?lf.
  ?lf ontolex:writtenRep \"$output\".

  ?lex  LIME:entry ?le.
   ?lex restaure:lexiconNameWrittenRep ?wrLexiqueName.
  ?lex restaure:lexiconAuthorWrittenRep ?wrAuthor.
  ?lex restaure:lexiconDirectionWrittenRep ?wrDirection
  
}";
/* execute the query */
 $rowsSources = $store->query($querySources, 'rows'); 

foreach($rowsSources as $row){
	$dataSources[] = $row;
}
echo $dataSources['wrLexiqueName'];



$wrTraductionFrancaisekeys = array_combine($wrTraductionFrancaise,$CoordinateWR);
$wrDefkeys = array_combine($wrDef,$CoordinateWR);
unset($wrDefkeys[null]);
unset($wrTraductionFrancaisekeys[null]);
$xcoordkeys = array_combine($CoordinateWR,$xcoord);
$ycoordkeys = array_combine($CoordinateWR,$ycoord);
$speechkeys = array_combine($CoordinateWR,$speech);
$genderkeys = array_combine($CoordinateWR,$gender);
if (count(array_filter($wrTraductionForm)) > 0){
	$str = "(français)";
	$str2 = "Traduction picarde :";
}

if (count(array_filter($wrTraductionFrancaise)) >0 ||count(array_filter($wrDef)) >0){

	$str = "(picard)";
	$str2 = "Traduction française :";
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
