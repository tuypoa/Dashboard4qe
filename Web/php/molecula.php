<?php 
require_once("config/configuracao.php"); 

$ppe = $_GET["pe"]=="1"?1:0;
$ppv = $_GET["pv"]=="1"?1:0;
$ppd = $_GET["pd"]=="1"?1:0;
$pps = $_GET["ps"]=="1"?1:0;


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


<div width="100%" style="padding:5px;">
	
	<table width="100%" cellspacing="0" cellpadding="5" border="0" >
	<tr>
		<td colspan="2"><span style="font-size:16px;font-weight:bold;color:#9f9f9f;">Sistemas cristalinos iniciais</span></td>
		
		<td align="center"><a href="molecula.php?molid=<?php echo $id_molecula; ?>&pe=1&pv=<?php echo $ppv; ?>&pd=<?php echo $ppd; ?>&ps=<?php echo $pps; ?>" >ENTALPIA</a><br>(Ry)</td>
		<td align="center"><a href="molecula.php?molid=<?php echo $id_molecula; ?>&pe=<?php echo $ppe; ?>&pv=1&pd=<?php echo $ppd; ?>&ps=<?php echo $pps; ?>" >VOLUME</a><br>(Ang^3)</td>
		<td align="center"><a href="molecula.php?molid=<?php echo $id_molecula; ?>&pe=<?php echo $ppe; ?>&pv=<?php echo $ppv; ?>&pd=1&ps=<?php echo $pps; ?>" >DENSIDADE</a><br>(g/cm^3)</td>
		<td align="center"><a href="molecula.php?molid=<?php echo $id_molecula; ?>&pe=1&pv=1&pd=1&ps=1" >TEMPO CPU</a><br>(total)</td>
		<td align="center">SCF STEP<br>(total)</td>
		<td></td>
	</tr>
	<?php
		$query = "SELECT m.maquina_codigo, maq.nome maquina_nome, m.hasharqin, m.ignorar, a.nome, a.codigo, a.descricao,
					max(r.codigo) output_codigo, count(r.codigo) as qtde_output
				FROM maquina_qearquivoin m
					INNER JOIN maquina maq ON m.maquina_codigo=maq.codigo
					INNER JOIN qearquivoin a ON m.qearquivoin_codigo=a.codigo
					INNER JOIN molecula mol ON mol.codigo=a.molecula_codigo
					INNER JOIN qeresumo r ON r.qearquivoin_codigo=a.codigo
				WHERE mol.codigo = :molid
				GROUP BY m.maquina_codigo, maq.nome, m.hasharqin, m.ordem, m.ignorar,a.nome, a.descricao, a.codigo
				ORDER BY m.ordem, a.descricao, a.codigo";
		$stBusca = $con->prepare($query);	
		$stBusca->bindParam(':molid', $id_molecula, PDO::PARAM_INT);
		$stBusca->execute();
		$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
		if(sizeof($rsBusca)>0){
			foreach ($rsBusca as $obj){
				?>					
				<tr>
				<!-- <td style="border-bottom: solid 1px #cccccc;" width="30"></td> -->
				<td style="border-bottom: solid 1px #cccccc;"><a href="resumo.php?rid=<?php echo $obj["output_codigo"]; ?>" style="font-size:14px;text-decoration:none;"><?php echo $obj["descricao"]; ?></a></td>
					<?php
						$query2 = "SELECT concluido,executando,erro,tamanhokb,ciclos,horas,ultimalida,
							qi.enthalpy, qi.volume, qi.density, qi.cellparams
						FROM (
							SELECT r.codigo,r.concluido,r.executando,r.erro,r.tamanhokb,
								MAX(qi.scfcycles) AS ciclos,	
								(MAX(qie.cputime)/3600) as horas,
								TO_CHAR(r.ultimalida,'DD/MM/YY HH24:MI') AS ultimalida
							FROM qeresumo r
								INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
								INNER JOIN qeinfoiteration qie ON qie.qeresumo_codigo=r.codigo AND qi.scfcycles=qie.scfcycles
							WHERE r.codigo = :rid
							GROUP BY r.codigo,r.concluido,r.executando,r.erro,r.tamanhokb,r.ultimalida
							) AS tb 
						INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=tb.codigo AND qi.scfcycles=tb.ciclos";
						$stBusca2 = $con->prepare($query2);	
						$stBusca2->bindParam(':rid', $obj["output_codigo"], PDO::PARAM_INT);
						$stBusca2->execute();
						$rsBusca2 = $stBusca2->fetchAll(PDO::FETCH_ASSOC);
						if(sizeof($rsBusca2)>0){
								$op = $rsBusca2[0];
								?>
								<td style="border-bottom: solid 1px #cccccc;" align="center">
									<span style="font-size:14px;color:#666666;"><?php definirNomeSistemaCristal($op['cellparams']); ?></span>
								</td>
								
								<td style="border-bottom: solid 1px #cccccc;" align="center">
									<?php if($ppe){ ?>
										<img src="graph/graph_molecula_mini.php?rid=<?php echo $obj["output_codigo"]; ?>&pe=1" width="120" height="60"/>
										<br>
									<?php } ?>
									<span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($op["enthalpy"],3))); ?></span>
								</td>
								<td style="border-bottom: solid 1px #cccccc;" align="center">
									<?php if($ppv){ ?>
										<img src="graph/graph_molecula_mini.php?rid=<?php echo $obj["output_codigo"]; ?>&pv=1" width="120" height="60"/>
										<br>
									<?php } ?>
									<span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($op["volume"],1))); ?></span>
								</td>
								<td style="border-bottom: solid 1px #cccccc;" align="center">
									<?php if($ppd){ ?>
										<img src="graph/graph_molecula_mini.php?rid=<?php echo $obj["output_codigo"]; ?>&pd=1" width="120" height="60"/>
										<br>
									<?php } ?>
									<span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($op["density"],3))); ?></span>
								</td>
								<td style="border-bottom: solid 1px #cccccc;" align="center">
									<?php if($pps){ ?>
										<img src="graph/graph_molecula_mini.php?rid=<?php echo $obj["output_codigo"]; ?>&ps=1" width="120" height="60"/>
										<br>
										<?php 										 										
									} ?>
									<span style="font-size:14px;"><?php 
									$stBusca3 = $con->prepare("SELECT r.codigo, max(cputime) cputime
																FROM qeresumo r
																	INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
																	INNER JOIN qeinfoiteration qie ON r.codigo=qie.qeresumo_codigo AND qi.scfcycles=qie.scfcycles
																WHERE r.codigo = :rid 
																GROUP BY r.codigo");	
											$stBusca3->bindParam(':rid', $obj["output_codigo"], PDO::PARAM_INT);
											$stBusca3->execute();
											$rsBusca3 = $stBusca3->fetchAll(PDO::FETCH_ASSOC);
											if(sizeof($rsBusca3)>0){
												$segundos = $rsBusca3[0]["cputime"];
												$dias = ($segundos/3600)/24;
												echo ($dias>1?number_format($dias,0)."d ":"").gmdate("H", $segundos)."h ".gmdate("i", $segundos)."m";
											}
											unset($rsBusca3);
											$rsBusca3 = null;
											$stBusca3->closeCursor(); ?></span>
								</td>
								<td style="border-bottom: solid 1px #cccccc;" align="center">
									<span style="font-size:14px;"><?php echo $op["ciclos"]; ?></span>
								</td>
								<td style="border-bottom: solid 1px #cccccc;" align="center"><?php
								if($op["erro"]==null){
								?>								
								<a href="resumo.php?rid=<?php echo $obj["output_codigo"]; ?>" class="<?php echo ($op["executando"]?"fblue":($op["concluido"]?"fgreen":"fgray")); ?>" style="font-size:12px;font-weight:bold;" ><?php echo ($op["executando"]?"EM ANDAMENTO":($op["concluido"]?"CONCLU&Iacute;DO":"INTERROMPIDO")); ?></a>								
								<br><span style="font-size:12px;color:#9f9f9f;">(<?php echo number_format($op["tamanhokb"]/1024,2); ?> MB)</span>
								<?php 
									if($op["concluido"]){
									?><br><span style="font-size:12px;color:#9f9f9f;">Fim <?php echo $op["ultimalida"]; ?></span>
									<?php 
									}									
								}else{
									?>
									<a href="resumo.php?rid=<?php echo $obj["output_codigo"]; ?>" class="fred" style="font-size:12px;font-weight:bold;" >N&#195;O CONVERGIU</a>
									<br><span style="font-size:12px;color:#9f9f9f;">(<?php echo number_format($op["tamanhokb"]/1024,2); ?> MB)</span>										
									<?php
								}	
								?></td>
								<?php
						}else{
							?>
							<td style="border-bottom: solid 1px #cccccc;" align="center" height="35"></td>
							<td style="border-bottom: solid 1px #cccccc;" align="center" ><span style="font-size:14px;">-</span></td>
							<td style="border-bottom: solid 1px #cccccc;" align="center" ><span style="font-size:14px;">-</span></td>
							<td style="border-bottom: solid 1px #cccccc;" align="center" ><span style="font-size:14px;">-</span></td>
							<td style="border-bottom: solid 1px #cccccc;" align="center" ><span style="font-size:14px;">-</span></td>
							<td style="border-bottom: solid 1px #cccccc;" align="center" ><span style="font-size:14px;">1</span></td>
							<td style="border-bottom: solid 1px #cccccc;" align="center"><span class="fblue" style="font-size:12px;font-weight:bold;" >INICIANDO</span></td>
							<?php
						}
						unset($rsBusca2);
						$rsBusca2 = null;
						$stBusca2->closeCursor();
						?>						
				</tr>
				<?php
			}
		}
		unset($rsBusca);
		$rsBusca = null;
		$stBusca->closeCursor();
		?>
	</table>
</div>


</div>

<?php include_once 'include/bottom.php'; ?>
</body>
</html>



