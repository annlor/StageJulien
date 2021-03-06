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
    <link rel="icon" href="favicon.ico">

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
        for (var i = 0; i < nombreOnglets; i++)
          document.getElementById("contenuOnglet" + i).style.display = "none";

        // Puis on affiche celui qui a été sélectionné
        document.getElementById("contenuOnglet" + numero).style.display = "block";
      }

 </script>


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
        <p class="lead">Vous avez cherché :</p>
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

$output = $_GET["mot"];

if (preg_match('/([a-zA-Z()\s\',]+)/',$output)<1){
	$output = "Entrée invalide";
}

$query = "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lexinfo: <http://lexinfo.net/ontology/2.0/lexinfo#>
PREFIX lex: <http://purl.org/lex#>


SELECT ?wr1 ?wr2 ?wr3 ?wr4 ?speech2 ?gender2
WHERE {
  ?x ontolex:writtenRep \"${output}\".
  ?y ontolex:lexicalForm ?x.
  
  {
    ?y restaure:TranslatableAsForm ?z1.
      ?z1 ontolex:writtenRep ?wr1.
  }
  UNION
  {
   ?y restaure:TranslatableAsDef ?z2.
      ?z2 ontolex:writtenRep ?wr2.
  }
  UNION
  {
   ?y restaure:ExampleInPicard ?z3.
      ?z3 ontolex:writtenRep ?wr3.
  }
  UNION
  {
   ?y restaure:TranslatableInFrench ?z4.
      ?z4 ontolex:writtenRep ?wr4.
  }

  OPTIONAL {?y lexinfo:partOfSpeech ?speech.
  ?speech ontolex:writtenRep ?speech2}.
    OPTIONAL{?y lexinfo:gender ?gender.
  ?gender ontolex:writtenRep ?gender2}


}";

/* execute the query */
  $rows = $store->query($query, 'rows'); 
 
    if ($errs = $store->getErrors()) {
       echo "Query errors" ;
       print_r($errs);
    }
foreach($rows as $row){
	$wr1[] = $row['wr1'];
	$wr2[] = $row['wr2'];
	$wr3[] = $row['wr3'];
	$wr4[] = $row['wr4'];
	$speech[] = $row['speech2'];
	$gender[] = $row['gender2'];
}
if (count(array_filter($wr1)) > 0){
	$str = "(français)";
	$str2 = "Traduction picarde :";
}

if (count(array_filter($wr4)) >0 ||count(array_filter($wr2)) >0){

	$str = "(picard)";
	$str2 = "Traduction française :";
}


if (count(array_filter($wr3)) >0){
	$str3 = "Exemple picard :";
}

echo "{$output} ${str}<br> {$speech[0]} {$gender[0]}";
?><br><br>


<div class="container">
  <ul class="nav nav-pills">
    <li class="active"><a data-toggle="pill" href="#home">Home</a></li>
    <li><a data-toggle="pill" href="#contenuOnglet1">Menu 1</a></li>
  </ul>
  
  <div class="tab-content">
    <div id="home" class="tab-pane fade in active">
      <ul class="list-group" style="list-style-type:circle">
	<li class="list-group-item"><p> <?php

	echo "<p style=\"font-weight:bold;\"> ${str2} </p>"."<p id = \"output\">".implode($wr1).implode($wr4)."</p>"."<br><br>"."<p style=\"font-weight:bold;\"> ${str3} </p>".implode($wr3);

?>

</p></li>
	<li class="list-group-item">Alsacien :</li>
	<li class="list-group-item">Occitan :</li>
	</ul>
    </div>
    <div id="contenuOnglet1" class="tab-pane fade" style="display:block;">
<div id="map">
</div>
    </div>

  </div>
</div>
    
    
<div id="contenuOnglet1" style="display:block;">

            </div>
 <br>
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
	var amiens=[49.895, 2.3022];
	// création de la carte, centrée autour d'Amiens pour le moment²
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
                    2.3022,
                    49.895
                ]
            },
            "type": "Feature",
            "properties": {
                "popupContent": "Amiens: " + document.getElementById("output").innerHTML
            },
            "id": 51
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
changeOnglet(0);

</script>



    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="bootstrap/docs/assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="bootstrap/docs/dist/js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="bootssrap/docs/assets/js/ie10-viewport-bug-workaround.js"></script>
</body>
</html>
