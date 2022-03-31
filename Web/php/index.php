<?php 
require_once("config/configuracao.php"); 

?>
<html>
<head><?php include_once 'include/head.php'; ?></head>
<body>
<?php include_once 'include/menu.php'; ?>
<?php
	$query = "
		SELECT m.codigo, m.nome, m.cpuused, m.memused, m.online, m.ignorar, m.iniciarjob,
			TO_CHAR(m.ultimoacesso,'DD/MM/YY HH24:MI:SS') AS ultimoacesso,
			m.mincpu, m.maxcpu, SUM(r.qtdecpu) AS cpu_emuso,
			(COALESCE(SUM(r.qtdecpu),0) < m.maxcpu) as ociosa
		FROM maquina m
			LEFT JOIN maquina_qearquivoin ma ON m.codigo=ma.maquina_codigo
			LEFT JOIN qeresumo r ON ma.qearquivoin_codigo=r.qearquivoin_codigo AND r.executando
		GROUP BY m.codigo, m.nome, m.cpuused, m.memused, m.online, m.ignorar, m.iniciarjob, m.ultimoacesso,m.mincpu, m.maxcpu
		ORDER BY m.online, m.nome
	    ";

	$stBusca = $con->prepare($query);	
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
		$i = 0;
		foreach ($rsBusca as $obj){	
		
			if($i++ % 3 == 0){ 
				echo "<div class='row'>";
			}
		?><div class="card <?php echo $obj["online"]?"green":"red";?>" onclick="selMaquina('<?php echo $obj["codigo"]; ?>');">
		<table cellspacing="0" cellpadding="3" border="0" align="left" bgcolor="#ffffff" >
		    <tr>
			<td><?php
				echo "<span style='font-size:14px;font-weight:bold;'>".$obj["nome"]."</span><br>".($obj["online"]?($obj["iniciarjob"]?"Autorizada a iniciar processo...":"(Online)"):"(Offline)");
			?>
			</td>
			</tr>
			<tr>
			<td ><?php
				echo "<span style='color:".(!$obj["ignorar"] && $obj["ociosa"]?"red":"blue").";font-size:14px;font-weight:bold;'>".$obj["cpuused"]."% cpu</span><br>";
				echo "<span style='color:green;font-size:13px;'>".$obj["memused"]."% Mem</span><br>";
				echo "<span style='font-size:12px;'>".$obj["ultimoacesso"]."</span><br>";
			?></td>
			</tr>
		</table>		
		<?php if(!$obj["ignorar"]){ ?>
				<img class="image" src="graph/graph_maquina_mini.php?mid=<?php echo $obj["codigo"]; ?>&o=<?php echo $obj["ociosa"]?"1":"0"; ?>" width="145" height="60"/>
			<?php } ?>
		</div>
			<?php
			if($i % 3 == 0){ 
				echo "</div>";
			}
		}
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();
	?>
<?php include_once 'include/bottom.php'; ?>
</body>
</html>



