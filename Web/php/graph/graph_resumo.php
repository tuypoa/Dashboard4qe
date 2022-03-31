<?php 
require_once("../config/configuracao.php"); 
require_once('phplot.php');

$ppe = $_GET["pe"]=="1";
$ppv = $_GET["pv"]=="1";
$ppd = $_GET["pd"]=="1";
$pps = $_GET["ps"]=="1";
$id_resumo = is_numeric($_GET["rid"])? $_GET["rid"] : NULL ;

$cor_param = "#4895ff";
if($ppe){
	$cor_param = "#ff7575";
	$query = "SELECT qi.scfcycles,qi.enthalpy valor
			FROM qeresumo r
				INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
			WHERE r.codigo = :rid
			ORDER BY scfcycles ";
}else if($ppv){
	$cor_param = "#63d3ff";
	$query = "SELECT qi.scfcycles,qi.volume valor
			FROM qeresumo r
				INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
			WHERE r.codigo = :rid
			ORDER BY scfcycles ";
}else if($ppd){
	$cor_param = "#76b5a0";
	$query = "SELECT qi.scfcycles,qi.density valor
			FROM qeresumo r
				INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
			WHERE r.codigo = :rid
			ORDER BY scfcycles ";
}else if($pps){
	$cor_param = "#820081";
	$query = "SELECT qi.scfcycles, (max(cputime)-min(cputime))/60 valor
			FROM qeresumo r
				INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
				INNER JOIN qeinfoiteration qie ON r.codigo=qie.qeresumo_codigo AND qi.scfcycles=qie.scfcycles
			WHERE r.codigo = :rid
			GROUP BY qi.scfcycles
			ORDER BY scfcycles ";
}

$stBusca = $con->prepare($query);	
$stBusca->bindParam(':rid', $id_resumo, PDO::PARAM_INT);
$stBusca->execute();
$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
if(sizeof($rsBusca)>0){
	$dados = array();
	foreach ($rsBusca as $obj){
	//	$registro = array();
		array_push( $dados, $obj );
	}
}else{
	
}
unset($rsBusca);
$rsBusca = null;
$stBusca->closeCursor();


$plot = new PHPlot(460, 250);
#Indicamos o título do gráfico e o título dos dados no eixo X e Y do mesmo
//$plot->SetTitle("Aqruivo 1 vs. Arquivo 2");
//$plot->SetXTitle("Self-consistent Steps");
if($ppe){
	$plot->SetTitle("Entalpia");
	$plot->SetYTitle("Rydberg");
}else if($ppv){
	$plot->SetTitle("Volume");
	$plot->SetYTitle("Angstrom^3");
}else if($ppd){
	$plot->SetTitle("Densidade");
	$plot->SetYTitle("g/cm^3");
}else if($pps){
	$plot->SetTitle("Tempo de CPU");
	$plot->SetYTitle("Minutos");
}
$plot->SetPlotType('lines');
$plot->SetXDataLabelPos('plotin');
//$plot->SetXTickIncrement(100);
$plot->SetXTickLabelPos('xaxis');
$plot->SetLineWidths(2);

$plot->SetDataColors(array($cor_param));

//$plot->SetXTickIncrement(M_PI / 8.0);
//$plot->SetNumXTicks(1000);
$plot->SetPointSizes(1);
$plot->SetDataValues($dados);
//$plot->SetXTickIncrement(1000);
$plot->SetXTickPos('none');

#Exibimos o gráfico
$plot->DrawGraph();

//print_r ($dados);

?>
