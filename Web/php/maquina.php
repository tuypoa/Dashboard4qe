<?php 
require_once("config/configuracao.php"); 

$id_maquina = is_numeric($_GET["mid"])? $_GET["mid"] : NULL ;
$maquina = null;

$query = "SELECT m.codigo, m.nome, m.cpuused, m.memused, m.online, m.ignorar,
			TO_CHAR(m.ultimoacesso,'dd/mm HH:MI:ss') AS ultimoacesso
		FROM maquina m
		WHERE m.codigo = :mid ";
	$stBusca = $con->prepare($query);	
	$stBusca->bindParam(':mid', $id_maquina, PDO::PARAM_INT);
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
		$maquina = $rsBusca[0];
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
<span style="font-size:16px;font-weight:bold;"><?php echo $maquina["nome"]; ?></span>
<br><br>

<div width="100%" style="padding:5px;">
	<span style="font-size:16px;font-weight:bold;color:#9f9f9f;">Arquivos INPUT</span>
	<br><br>
	<table width="100%" cellspacing="0" cellpadding="5" border="0" >
	<?php
		$query = "SELECT m.hasharqin, m.ordem, a.nome, a.codigo, a.descricao,
				mol.nome molecula_nome, max(r.codigo) output_codigo
			FROM maquina_qearquivoin m
				INNER JOIN qearquivoin a ON m.qearquivoin_codigo=a.codigo
				INNER JOIN molecula mol ON mol.codigo=a.molecula_codigo
				LEFT JOIN qeresumo r ON r.qearquivoin_codigo=a.codigo
			WHERE m.maquina_codigo = :mid
			GROUP BY m.hasharqin, m.ordem, a.nome, a.descricao, mol.nome, a.codigo
			ORDER BY m.ordem, a.codigo ";
		$stBusca = $con->prepare($query);	
		$stBusca->bindParam(':mid', $id_maquina, PDO::PARAM_INT);
		$stBusca->execute();
		$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
		if(sizeof($rsBusca)>0){
			foreach ($rsBusca as $obj){
				?>					
				<tr>
				<!-- <td style="border-bottom: solid 1px #cccccc;" width="30"></td> -->
				<td style="border-bottom: solid 1px #cccccc;"><span style="font-size:14px;"><?php echo $obj["descricao"]; ?></span><br>
					<span style="font-size:12px;color:#9f9f9f;"><?php echo $obj["nome"]; ?><br>
						hash md5: <?php echo $obj["hasharqin"]; ?></span></td>
				<td style="border-bottom: solid 1px #cccccc;" align="center"><?php 
					if($obj["output_codigo"]!=null){ ?>						
						<?php
							$query2 = "SELECT concluido,executando,erro,tamanhokb,
									TO_CHAR(datahora,'DD/MM HH24:MI') AS datahora,
									TO_CHAR(ultimalida,'DD/MM HH24:MI') AS ultimalida
								FROM qeresumo r
								WHERE r.codigo = :rid";
							$stBusca2 = $con->prepare($query2);	
							$stBusca2->bindParam(':rid', $obj["output_codigo"], PDO::PARAM_INT);
							$stBusca2->execute();
							$rsBusca2 = $stBusca2->fetchAll(PDO::FETCH_ASSOC);
							if(sizeof($rsBusca2)>0){
									$op = $rsBusca2[0];
									if($op["erro"]==null){
									?>
									<span class="<?php echo ($op["executando"]?"fblue":($op["concluido"]?"fgreen":"")); ?>" style="font-size:12px;font-weight:bold;" >EXECUTANDO</span>
									<br><span style="font-size:12px;color:#9f9f9f;">In&iacute;cio <?php echo $op["datahora"]; ?></span>
									<?php 
										if($op["concluido"]){
										?><br><span style="font-size:12px;color:#9f9f9f;">Fim <?php echo $op["datahora"]; ?></span>
										<?php 
										}									
									}else{
										?>
										<span class="fred" style="font-size:12px;font-weight:bold;" ><?php echo trim($obj["erro"]); ?></span>
										<?php
									}								
							}
							unset($rsBusca2);
							$rsBusca2 = null;
							$stBusca2->closeCursor();
							?>
					<?php }else{ ?><span style="font-size:12px;color:#9f9f9f;">SEM OUTPUT</span><?php } ?></td>	
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



