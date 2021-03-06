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
			$fechouDiv = false;
			if($i++ % 3 == 0){ 
				echo "<div class='row'>";
			}
			?><div class="card <?php echo $obj["online"]?"green":"red";?>" onclick="selMaquina('<?php echo $obj["codigo"]; ?>');">
			<table cellspacing="0" cellpadding="3" border="0" align="left" bgcolor="#ffffff" >
				<tr>
				<td><?php
					echo "<span style='font-size:14px;font-weight:bold;'>".$obj["nome"]."</span><br>".($obj["online"]?($obj["iniciarjob"]?"Autorizada a iniciar processo...":"(Online)"):"<span style='color:red;'>(Offline)</span>");
				?>
				</td>
				</tr>
				<tr>
				<td ><?php
					echo "<span style='color:".($obj["online"]?(!$obj["ignorar"] && ($obj["cpuused"] <50 || $obj["ociosa"])?"red":"blue"):"#666666").";font-size:14px;font-weight:bold;'>".$obj["cpuused"]."% cpu</span><br>";
					echo "<span style='color:".($obj["online"]?"green":"#666666").";font-size:13px;'>".$obj["memused"]."% Mem</span><br>";
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
				$fechouDiv = true;
				echo "</div>";
			}
		}
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();

	if($i % 3 == 0){ 
		$fechouDiv = false;
		echo "<div class='row'>";			
	}


	
	?><div class="card blue">
	<table cellspacing="0" cellpadding="3" border="0" align="left" bgcolor="#ffffff" >
		<tr>
		<td><?php
			echo "<span style='font-size:14px;font-weight:bold;'>Database</span><br>(Online)";
		?>
		</td>
		</tr>
		<tr>
		<td ><?php
			$query = "SELECT 
				pg_size_pretty(pg_database_size('dashboard4qe')) as tam,
				table_name, 
				pg_size_pretty( pg_total_relation_size(quote_ident(table_name))) as tamtb
			FROM 
				information_schema.tables
			WHERE 
				table_schema = 'public'
			ORDER BY 
				pg_total_relation_size(quote_ident(table_name)) DESC limit 6";
			$stBusca = $con->prepare($query);	
			$stBusca->execute();
			$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
			if(sizeof($rsBusca)>0){
				$dbsize = $rsBusca[0];
				echo "<span style='font-size:13px;'>Total: ".$dbsize["tam"]."</span><br>";
				foreach ($rsBusca as $obj){	
					echo "<span style='color:#666666;font-size:12px;'>&nbsp; ".$obj["table_name"].": ".$obj["tamtb"]."</span><br>";
				}
			}
			unset($rsBusca);
			$rsBusca = null;
			$stBusca->closeCursor();
		?></td>
		</tr>
	</table>			
	<?php

	if(!$fechouDiv){
		echo "</div>";
	}
	?>
<?php include_once 'include/bottom.php'; ?>
</body>
</html>



