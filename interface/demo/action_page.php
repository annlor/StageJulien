<!DOCTYPE html>
<html>
<head>
<title>Projet RESTAURE : Lexique et carte</title>
<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">


<link rel="stylesheet" href="https://unpkg.com/leaflet@1.1.0/dist/leaflet.css" integrity="sha512-wcw6ts8Anuw10Mzh9Ytw4pylW8+NAD4ch3lqm9lzAsTxg0GFeJgoAtxuCLREZSC5lUXdVyo/7yfsqFjQ4S+aKw==" crossorigin=""/>

<script src="https://unpkg.com/leaflet@1.1.0/dist/leaflet.js" integrity="sha512-mNqn2Wg7tSToJhvHcqfzLMU6J4mkOImSPTxVZAdo+lcPlk+GhZmYgACEe0x35K7YzW1zJ7XyJV/TT1MrdXvMcA==" crossorigin=""></script>
    <!-- Bootstrap core CSS -->
    <link href="bootstrap/docs/dist/css/bootstrap.css" rel="stylesheet">

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <link href="bootstrap/docs/assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="./starter-template.css" rel="stylesheet">
<script src="bootstrap/docs/assets/js/ie-emulation-modes-warning.js"></script>

<script type="text/javascript">
      var nombreOnglets = 2;
      function changeOnglet(numero)
      {
        // On commence par tout masquer
        for (var i = 0; i < nombreOnglets; i++) {
          document.getElementById("contenuOnglet" + i).style.display = "none";
	}

        // Puis on affiche celui qui a été sélectionné
        document.getElementById("contenuOnglet" + numero).style.display = "block";
      }

 </script>
<style>
.nav-pills > li.active > a, .nav-pills > li.active > a:focus {
    color: #f8f8f8;
    background-color: #2d2d2d;
    border-color: #000000;
    }

        .nav-pills > li.active > a:hover {
    color: #222222;
    background-color: #f7f7f7;
    border-color: #d3d3d3;
        }

.nav-pills > li > a, .nav-pills > li > a:focus {
    color: #000000;
    background-color: #ffffff;
    border-color: #d3d3d3;
}
        .nav-pills > li > a:hover {
    color: #222222;
    background-color: #f7f7f7;
    border-color: #d3d3d3;
}
</style>

</head>
<body>


<nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="index.php">RESTAURE</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="index.php">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

<div class="container-fluid">

      <div class="starter-template">
        <h2>Vous avez cherché :</h2>
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
    restaure:LexiqueDebrie restaure:Coordinate ?coordinateValenciennes.
      restaure:xValenciennes ontolex:writtenRep ?CoordinateWR.
    ?coordinateValenciennes restaure:xValenciennes ?xcoord.
    ?coordinateValenciennes restaure:yValenciennes ?ycoord.
  }
  OPTIONAL {
{
      restaure:LexiqueDaubyRouchiFra LIME:entry ?y.
  	restaure:LexiqueDaubyRouchiFra restaure:Coordinate ?cAmiens.
}
UNION
{
      restaure:LexiqueDaubyRouchiPicard LIME:entry ?y.
  	restaure:LexiqueDaubyRouchiPicard restaure:Coordinate ?cAmiens.
}
     restaure:xAmiens ontolex:writtenRep ?CoordinateWR.
    ?cAmiens restaure:xAmiens ?xcoord.
    ?cAmiens restaure:yAmiens ?ycoord.
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
 $rowsSeeAlso = $store->query($querySeeAlso, 'rows'); 


foreach($rowsSeeAlso as $row){
	$wrLink[] = $row['wrLink'];
}

$wrTraductionFrancaisekeys = array_combine($wrTraductionFrancaise,$CoordinateWR);
unset($wrTraductionFrancaisekeys[null]);
$xcoordkeys = array_combine($CoordinateWR,$xcoord);
$ycoordkeys = array_combine($CoordinateWR,$ycoord);
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

echo "{$output} ${str}<br> {$speech[0]} {$gender[0]}";
?><br><br>
</div>

<div class="container-fullwidth">

  
<div class="row">
	<div class="col-md-6">
<ul><li class="list-group-item" style="text-align:left;">
<?php
$traductions=array();



foreach (array_unique($CoordinateWR) as $cWRar){
	echo "<div id = \"$cWRar\"><p style = \"font-weight:bold;font-size:150%;\">${cWRar} </p>";
		if (count(array_filter($wrTraductionForm)) > 0 && $cWRar =='Amiens')  {
	echo "<p style=\"font-weight:bold;\"> ${str2} </p>";
			foreach($wrTraductionForm as $value){
				$content = "<p class =\"Amiens\"> $value<br></p>";
				echo $content;
				$traductions['Amiens'].= $content;
			}
		}	
	
if (count(array_filter($wrTraductionFrancaisekeys)) > 0){
echo "<p style=\"font-weight:bold;\"> ${str2} </p>";
	foreach (array_filter($wrTraductionFrancaisekeys) as $key => $value) {
		if ($value == $cWRar){
			if (strlen($value) > 2 && !empty($key) && strlen($key) > 3){
				
				if(!array_key_exists($value,$traductions))
					$traductions[$value]="";
				$content="<p class =\"$value\"> $key <br></p>";
				echo $content;
				$traductions[$value].=$content;
			}
		}		
	}
}
else if (count(array_filter($wrDef)) > 0){
	echo implode($wrDef);
}
if ($cWRar == 'Amiens'){
	echo "<p style=\"font-weight:bold;\"> ${str3} </p>";
	if(count(array_filter($wrExPicard))>1){
		echo '<ul>';
		echo '<li>' . implode( '</li><li>', array_filter($wrExPicard)) . '</li>';
		echo '</ul>';
	}
	else{
		echo implode('<br>',array_filter($wrExPicard));
	}
	echo "<p style = \"font-weight:bold;\">${str4} </p>".implode($ANC);
	echo "<p style = \"font-weight:bold;\">${strLink} </p>".implode(', ',$wrLink);
}



echo '</div>';

}
?>


</li>
	<li class="list-group-item" style="text-align:left;">Alsacien :</li>
	<li class="list-group-item" style="text-align:left;">Occitan :</li>
	</ul>
</div>
    <div id="contenuOnglet1" class ="col-md-6"  style="display:block;">
<div id="map">
</div>

  </div>
</div> <br>
</div>
    </div><!-- /.container -->

     

<!--
<script type="text/javascript">
var res;
var xhttp = new XMLHttpRequest();
xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	res = myFunction(this, <?php echo json_encode($_GET["mot"]); ?>);
document.getElementById("output").innerHTML = eval("res")

    }
};
xhttp.open("GET", "a.xml", true);
xhttp.send();
function myFunction(xml, test) {
    var xmlDoc = xml.responseXML;
    var xEntry = xmlDoc.getElementsByTagName('Entrée');
    var xLexie = xmlDoc.getElementsByTagName('Lexie');
for (var i = 0; i < xEntry.length; i++){
	if (xEntry[i].childNodes[0].nodeValue == test) {
	
	var y = xLexie[i].childNodes[0];
    
	}
}
return y.nodeValue; 
}


</script>
-->


<script>
	// coordonnées d'Amiens

	var xAmiens=<?php echo json_encode($xcoordkeys["Amiens"]); ?>;
	var yAmiens=<?php echo json_encode($ycoordkeys["Amiens"]); ?>;
	var xValenciennes=<?php echo json_encode($xcoordkeys["Valenciennes"]); ?>;
	var yValenciennes=<?php echo json_encode($ycoordkeys["Valenciennes"]); ?>;
	xAmiens = xAmiens == null ? xValenciennes : xAmiens;
	yAmiens = yAmiens == null ? yValenciennes : yAmiens;
	// création de la carte, centrée autour d'Amiens pour le moment²

	var amiens=[xAmiens, yAmiens];
	var map = L.map('map').setView(amiens, 7);
	// création du fond de la carte
	L.tileLayer( //'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
		'https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png', {
		//'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
			'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
			'Imagery © <a href="http://mapbox.com">Mapbox</a>',
		id: 'mapbox.light'
	}).addTo(map);
function onEachFeature(feature, layer) {
	layer.bindPopup(feature.properties.popupContent);
	}


var unmot = {
    "type": "FeatureCollection",
    "features": [
        {
            "geometry": {
                "type": "Point",
                "coordinates": [
                    yAmiens,
                    xAmiens
                ]
            },
            "type": "Feature",
            "properties": {
                "popupContent": <?php echo '"'.addslashes($traductions["Amiens"]).'"' ?>
            },
            "id": 28
        },
        {
            "geometry": {
                "type": "Point",
                "coordinates": [
                    yValenciennes,
                    xValenciennes
                ]
            },
            "type": "Feature",
            "properties": {
                "popupContent": <?php echo '"'.addslashes($traductions["Valenciennes"]).'"' ?>
            },
            "id": 23
        },
    ]
};

// récupération des données JSON qui sont dans le fichier unmot.js
	// et ajout de ces données à la carte
	L.geoJSON([unmot], {

		style: function (feature) {
			return feature.properties && feature.properties.style;
		},

		onEachFeature: onEachFeature,

		pointToLayer: function (feature, latlng) {
			return L.circleMarker(latlng, {
				radius: 6,
				fillColor: "#0000ff",
				color: "#0000ff",
				weight: 1,
				opacity: 1,
				fillOpacity: 0.8
			});
		}
	}).addTo(map);
//changeOnglet(0);
//changeOnglet(1);

</script>



    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="bootstrap/docs/assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="bootstrap/docs/dist/js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="bootstrap/docs/assets/js/ie10-viewport-bug-workaround.js"></script>
</body>
</html>
