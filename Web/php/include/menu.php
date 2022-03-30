<table class="body_border" cellspacing="0" cellpadding="0" border="0" >
<tr>		
	<td valign="top">

<div width="100%" height="100%" style="border-bottom: solid 1px #cccccc;">
<table width="100%" cellspacing="0" cellpadding="5" >
<tr>
<td valign="top" style="width: 200px; padding:10px; background-color: #f7f7f7;">

	<br>
	
	<div style="padding:10px;">
		<span style="font-size:16px;font-weight:bold;color:#8c8c8c;">Monitoramento PWScf<br>Quantum Espresso</span>
		<br><br>
		<a href="index.php" style="color:#9f9f9f;">IN&Iacute;CIO</a><?php
        if($id_maquina!=null) { ?>
        / <span style="color:#9f9f9f;font-weight:bold;">M&aacute;quina</span>
        <?php }
		if($id_molecula!=null) { ?>
			/ <span style="color:#9f9f9f;font-weight:bold;"><?php echo $molecula["nome"]; ?></span>
			<?php }
		?>
        
        <br><br>
	</div>
	<table width="100%" cellspacing="0" cellpadding="0" border="0">
		<!--<tr>
		<td width="30"></td>
		<td><span style="font-size:14px;font-weight:bold;">Mol&eacute;culas</span></td>
		</tr> -->
		<?php
		$query = "SELECT m.codigo, m.nome FROM molecula m ORDER BY m.nome";
		$stBusca = $con->prepare($query);	
		$stBusca->execute();
		$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
		if(sizeof($rsBusca)>0){
			foreach ($rsBusca as $obj){
				?>					
				<tr>
				<td width="30" height="35" <?php echo ($molecula!=null && $obj["codigo"]==$molecula["codigo"]?"bgcolor='#dedede'":""); ?>></td>
				<td <?php echo ($molecula!=null && $obj["codigo"]==$molecula["codigo"]?"bgcolor='#dedede'":""); ?>><a href="molecula.php?molid=<?php echo $obj["codigo"]; ?>" 
                style="font-size:14px;font-weight:bold;color:#666666;text-decoration:none;"><?php echo $obj["nome"]; ?></a></td>
				</tr>
                <tr><td colspan="2" height="1"></td></tr>
				<?php
			}
		}
		unset($rsBusca);
		$rsBusca = null;
		$stBusca->closeCursor();
		?>
	</table>
	<br>
</td>
<td valign="top">