	
</td>
</tr>
</table>
</div>

<img style='position: absolute;' src='images/load.gif' width='60'/>

<div width="100%" style="border-bottom: solid 1px #cccccc;">
<table cellspacing="0" cellpadding="18" border="0" >
<tr>
	<td style="padding-top: 22px;">
	<span style="padding-left:30px;font-size:16px;font-weight:bold;color:#9f9f9f;">Executando</span>
	<pre><?php
	$query = "
		SELECT m.codigo, m.nome, p.conteudo,
			TO_CHAR(p.datahora,'DD/MM/YYYY HH24:MI:SS') AS datahora,
			MAX(qi.scfcycles) as ciclos,
			(MAX(qie.cputime)/3600) as horas
		FROM psaux p 
			INNER JOIN maquina m ON p.maquina_codigo=m.codigo
			LEFT JOIN qeresumo r ON p.qeresumo_codigo=r.codigo
			LEFT JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
			LEFT JOIN qeinfoiteration qie ON qie.qeresumo_codigo=r.codigo AND qi.scfcycles=qie.scfcycles
		WHERE comando_codigo = 2
		GROUP BY m.codigo, m.nome, p.conteudo, p.datahora
		ORDER BY m.online, m.nome
	    ";

	$stBusca = $con->prepare($query);	
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
		foreach ($rsBusca as $obj){
			
			echo " ".$obj["datahora"]." <a href='maquina.php?mid=".$obj["codigo"]."' style='font-weight:bold;text-decoration:none;' ".($maquina!=null && $obj["codigo"]==$maquina["codigo"]?" class='fblue'":"").">".$obj["nome"]."</a>$ ".$obj["conteudo"];
			if($obj["ciclos"]!=null) { 
				echo " <span class='fblue'>(".$obj["ciclos"]." steps, ".($obj["horas"]>72?number_format($obj["horas"]/24,0)."d".number_format($obj["horas"]%24,0)."h":$obj["horas"]."h").")</span>"; 
			}
			echo "\n";

		}
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();
	?></pre></td>
</tr>
<?php
	$query = "
	SELECT m.codigo, m.nome, a.nome as conteudo, r.concluido, r.erro,
	TO_CHAR(r.ultimalida,'DD/MM/YYYY HH24:MI:SS') AS datahora,
	MAX(qi.scfcycles) as ciclos,
	(MAX(qie.cputime)/3600) as horas
	FROM qeresumo r 		
		INNER JOIN qearquivoin a ON a.codigo=r.qearquivoin_codigo
		INNER JOIN maquina_qearquivoin qa ON a.codigo=qa.qearquivoin_codigo
		INNER JOIN maquina m ON m.codigo=qa.maquina_codigo
		INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
		INNER JOIN qeinfoiteration qie ON qie.qeresumo_codigo=r.codigo AND qi.scfcycles=qie.scfcycles
	WHERE NOT executando
	GROUP BY m.codigo, m.nome, a.nome, r.ultimalida, r.concluido, r.erro
	ORDER BY r.ultimalida DESC
	LIMIT 10
	";

	$stBusca = $con->prepare($query);	
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
	?>
	<tr>
		<td style="padding-top: 0px;">
		<span style="font-size:16px;font-weight:bold;color:#9f9f9f;">Hist&oacute;rico</span>
		<pre><?php
			foreach ($rsBusca as $obj){
				
				echo " ".$obj["datahora"]." <a href='maquina.php?mid=".$obj["codigo"]."' style='font-weight:bold;text-decoration:none;' ".($maquina!=null && $obj["codigo"]==$maquina["codigo"]?" class='fblue'":"").">".$obj["nome"]."</a>$ ".$obj["conteudo"];
				if($obj["ciclos"]!=null) { 
					echo " <span class='".($op["concluido"]?"fgreen":"fred")."'>".($obj["erro"]!=null?"Error ":(!$op["concluido"]?"Caiu ":""))."(".$obj["ciclos"]." steps, ".($obj["horas"]>72?number_format($obj["horas"]/24,0)."d".number_format($obj["horas"]%24,0)."h":$obj["horas"]."h").")</span>"; 
				}
				echo "\n";

			}	
		?></pre>
	</td>
	</tr>
	<?php 
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();
?>
</table>
</div>

<br>
<table align="center" cellspacing="0" cellpadding="0" border="0" >
<tr>		
	<td align="center"><img src="images/logo-lab.png" width="60" height="44"/></td>
</tr>
<tr>	
	<td align="center" style="font-size:10px;color:#9f9f9f;">
		LABORAT&#211;RIO DE PLANEJAMENTO FARMAC&#202;UTICO E SIMULA&#199;&#195;O COMPUTACIONAL
		<br>
		UFRJ - CENTRO DE CI&#202;NCIAS DA SA&#218;DE
	</td>
</tr>	
</table>
<br>

</td>
</tr>		
</table>
