<?php
$traductions=array();


foreach (array_unique($CoordinateWR) as $cWRar){
$i = 0;
	
	echo "<div id = \"$cWRar\" class=\"list-group-item\" style=\"text-align:left;border-width:5px;border-radius: 15px;\"><p style = \"font-weight:bold;font-size:150%;\">${cWRar} </p>";
	if (count($speechkeys[$cWRar]) > 0 | count($genderkeys[$cWRar]) > 0){
	echo "<div><p style=\"font-weight:bold;\">Partie du discours et genre :</p><p>$speechkeys[$cWRar] $genderkeys[$cWRar]</p></div>";
}
		if (count(array_filter($wrTraductionForm)) > 0 && $cWRar =='Amiens')  {
	echo "<p style=\"font-weight:bold;\"> ${str2} </p>";
			foreach($wrTraductionForm as $value){
				$content = "<p class =\"Amiens\"> $value</p>";
				echo $content;
				$traductions['Amiens'].= $content;
				$i = 1;
			}
		}	
	
if (count(array_filter($wrTraductionFrancaisekeys)) > 0){
if ($i == 0){
echo "<p style=\"font-weight:bold;\"> ${str2} </p>";
}
$traductions['Valenciennes']="";
	foreach (array_filter($wrTraductionFrancaisekeys) as $key => $value) {
		if ($value == $cWRar){
			if (strlen($value) > 2 && !empty($key) && strlen($key) > 3){
				
				if(!array_key_exists($value,$traductions))
					$traductions[$value]="";
				$content="<p class =\"$value\"> $key</p>";
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
	if(count(array_filter($wrExPicard))>1){
echo "<p style=\"font-weight:bold;\"> ${str3} </p>";
		echo '<ul>';
		echo '<li>' . implode( '</li><li>', array_filter($wrExPicard)) . '</li>';
		echo '</ul><br>';
	}
	else{
echo "<p style=\"font-weight:bold;\"> ${str3} </p>";
		echo implode('<br>',array_filter($wrExPicard));
	}
	if (count(array_filter($ANC)) >0 && strlen(implode(array_filter($ANC))) > 1){
	echo "<p style = \"font-weight:bold;\">${str4} </p>".implode($ANC)."<br>";
}
	if (count(array_filter($wrLink)) >0){
	echo "<p style = \"font-weight:bold;\">${strLink} </p>".implode(', ',$wrLink)."<br>";
}
}



echo '</div><br>';
}
foreach ($dataLinks as $row){
echo "<div class=\"list-group-item\" style=\"text-align:left;font-style: italic;border-radius: 15px;\">";
if ($row[CoordinateWR] == 'Amiens'){
echo "<p  style = \"font-weight:bold;font-size:140%;\"> $row[wrPicard]</p>";
$traductions["Amiens"].= "<p class =\"Amiens\" style=\"font-style: italic;\"> $row[wrPicard]</p>";
}
else if ($row[CoordinateWR] == 'Valenciennes'){
echo "<p  style = \"font-weight:bold;font-size:140%;\"> $row[wrPicard]</p>";
$traductions["Valenciennes"].= "<p class =\"Valenciennes\" style=\"font-style: italic;\"> $row[wrPicard]</p>";
}
if(count($row[gender2]) > 0 | count($row[speech2])>0){ 
echo "<p style=\"font-weight:bold;font-size:90%;\">Partie du discours et genre :</p><p>$row[gender2] $row[speech2]</p>";}
if(count($row[wrTraductionFrancaiseConcat])>0){
echo "<p style=\"font-weight:bold;font-size:90%;\"> Traduction Française :</p><p>".$row[wrTraductionFrancaiseConcat]."</p>";
}
if(count($row[wrExPicardConcat])>0){
echo "<p style=\"font-weight:bold;font-size:90%;\"> Exemple Picard : </p><p>".$row[wrExPicardConcat]."</p>";
}
if(count($row[ANCConcat])>0 && strlen($row[ANCConcat]) > 2){
echo "<p style=\"font-weight:bold;font-size:90%;\"> Ancienne étymologie : </p><p>".$row[ANCConcat]."</p>";
}
echo "</div>";
}

foreach ($dataLinks2 as $row){

echo "<div class=\"list-group-item\" style=\"text-align:left;font-style: italic;border-radius: 15px;\">";
if ($row[CoordinateWR] == 'Amiens'){
echo "<p  style = \"font-weight:bold;font-size:140%;\"> $row[wrFrench]</p>";
$traductions["Amiens"].= "<p class =\"Amiens\" style=\"font-style: italic;\"> $row[wrFrench]</p>";
}
else if ($row[CoordinateWR] == 'Valenciennes'){
echo "<p  style = \"font-weight:bold;font-size:140%;\"> $row[wrFrench]</p>";
$traductions["Valenciennes"].= "<p class =\"Valenciennes\" style=\"font-style: italic;\"> $row[wrFrench]</p>";
}
if(count($row[gender2]) > 0 | count($row[speech2])>0){ 
echo "<p style=\"font-weight:bold;font-size:90%;\">Partie du discours et genre :</p><p>$row[gender2] $row[speech2]</p>";}
if(count($row[wrFormConcat])>0){
echo "<p style=\"font-weight:bold;font-size:90%;\"> Traduction Picarde :</p><p>".$row[wrFormConcat]."</p>";
}
echo "</div>";
}


?>
