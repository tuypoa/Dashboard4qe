	
</td>
</tr>
</table>
</div>

<img style='position: absolute; clip: rect(10px,50px,50px,10px);' src='images/load.gif' width='55'/>

<div width="100%" style="border-bottom: solid 1px #cccccc;">
<table cellspacing="0" cellpadding="18" >
<tr>
	<td>
	<span style="padding-left:30px;font-size:16px;font-weight:bold;color:#9f9f9f;">Executando</span>
	<pre><?php
	$query = "
		SELECT m.codigo, m.nome, p.conteudo,
			TO_CHAR(p.datahora,'DD/MM/YYYY HH24:MI:SS') AS datahora
		FROM psaux p INNER JOIN maquina m ON p.maquina_codigo=m.codigo
		WHERE comando_codigo = 2
		ORDER BY m.online, m.nome
	    ";

	$stBusca = $con->prepare($query);	
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
		foreach ($rsBusca as $obj){
			
			echo " ".$obj["datahora"]." <a href='maquina.php?mid=".$obj["codigo"]."' style='font-weight:bold;text-decoration:none;' ".($maquina!=null && $obj["codigo"]==$maquina["codigo"]?" class='fblue'":"").">".$obj["nome"]."</a>$ ".$obj["conteudo"]."\n";

		}
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();
	?></pre>
</td>
</tr>
</table>
</div>