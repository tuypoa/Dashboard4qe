<?php 
require_once("config/configuracao.php"); 

$id_molecula = is_numeric($_GET["molid"])? $_GET["molid"] : NULL ;
$molecula = null;

$query = "SELECT m.codigo, m.nome
		FROM molecula m
		WHERE m.codigo = :molid ";
	$stBusca = $con->prepare($query);	
	$stBusca->bindParam(':molid', $id_molecula, PDO::PARAM_INT);
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
		$molecula = $rsBusca[0];
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();

?>
<html>
<head><?php include_once 'include/head.php'; ?></head>
<body>
<?php include_once 'include/menu.php'; ?>

<div width="100%" style="padding:20px;">
<span style="font-size:16px;font-weight:bold;"><?php echo $molecula["nome"]; ?></span>
<br><br>


<div width="100%" style="padding:5px;">
	<span style="font-size:16px;font-weight:bold;color:#9f9f9f;">Cen&aacute;rio completo</span>
	<br><br>
	<table width="100%" cellspacing="0" cellpadding="5" border="0" >

	</table>
</div>


</div>

<?php include_once 'include/bottom.php'; ?>
</body>
</html>



