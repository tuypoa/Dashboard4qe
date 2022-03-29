<?php 
require("config/configuracao.php"); 
require('phplot.php');

$id_padrao = is_numeric($_GET["pid"])? $_GET["pid"] : NULL ;
$id_equipamento = is_numeric($_GET["eid"])? $_GET["eid"] : NULL ;
$id_software = is_numeric($_GET["sid"])? $_GET["sid"] : NULL ;

$query = "
	SELECT angulo, 
		(CASE WHEN codigo=0 THEN valor ELSE NULL END) as e1, 
		(CASE WHEN codigo=1 THEN valor ELSE NULL END) as p1
	FROM
		(SELECT ROUND(angulo, 2) as angulo, 0 as codigo, normatizado AS valor
			FROM equipodados
			WHERE equipamento_codigo = :eid AND angulo BETWEEN 1 and 90
		UNION    
		SELECT ROUND(pd.angulo * ( (SELECT wavelength FROM equipamento WHERE codigo=:eid )/ p.wavelength), 2) as angulo, 1 as codigo, pd.normatizado AS valor
			FROM padrao p
				INNER JOIN padraodados pd ON p.codigo=pd.padrao_codigo
			WHERE p.codigo=:pid AND pd.software_codigo=:sid AND pd.angulo BETWEEN 1 and 90
		) AS tbu
	ORDER BY angulo
    	";

$stBusca = $con->prepare($query);	
$stBusca->bindParam(':eid', $id_equipamento, PDO::PARAM_INT);
$stBusca->bindParam(':pid', $id_padrao, PDO::PARAM_INT);
$stBusca->bindParam(':sid', $id_software, PDO::PARAM_INT);
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


$plot = new PHPlot(800, 350);
#Indicamos o título do gráfico e o título dos dados no eixo X e Y do mesmo
//$plot->SetTitle("Aqruivo 1 vs. Arquivo 2");
$plot->SetXTitle("2theta: equipo vs. cif");
$plot->SetYTitle("Intensidade");

$plot->SetXDataLabelPos('plotin');
$plot->SetXTickIncrement(100);
//$plot->SetXTickLabelPos('xaxis');
$plot->SetLineWidths(2);

$plot->SetDataColors(array('blue', 'gray'));

//$plot->SetXTickIncrement(M_PI / 8.0);
//$plot->SetNumXTicks(1000);
$plot->SetPointSizes(1);
$plot->SetDataValues($dados);
//$plot->SetXTickIncrement(1000);
//$plot->SetXTickPos('none');

#Exibimos o gráfico
$plot->DrawGraph();

//print_r ($dados);

?>
