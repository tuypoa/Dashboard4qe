<?php 
require_once("config/configuracao.php"); 

$ppe = $_GET["pe"]=="1"?1:0;
$ppv = $_GET["pv"]=="1"?1:0;
$ppd = $_GET["pd"]=="1"?1:0;
$pps = $_GET["ps"]=="1"?1:0;

$id_resumo = is_numeric($_GET["rid"])? $_GET["rid"] : NULL ;
$resumo = null;
$query = "SELECT tb.codigo, tb.qearquivoin_codigo, cputime, 
		mol.codigo molecula_codigo, mol.nome molecula_nome, a.descricao,
		minciclo, qmin.bfgssteps minbfgs, qmin.enthalpy minenthalpy, qmin.volume minvolume, 
			qmin.density mindensity, qmin.iterations miniterations,
		maxciclo, qmax.bfgssteps maxbfgs, qmax.enthalpy maxenthalpy, qmax.volume maxvolume,
			qmax.density maxdensity, qmax.iterations maxiterations
		FROM (SELECT r.codigo, r.qearquivoin_codigo, max(cputime) cputime, 
			max(qi.scfcycles) maxciclo, min(qi.scfcycles) minciclo
			FROM qeresumo r
			INNER JOIN qeinfoscf qi ON qi.qeresumo_codigo=r.codigo
			INNER JOIN qeinfoiteration qie ON r.codigo=qie.qeresumo_codigo AND qi.scfcycles=qie.scfcycles
			WHERE r.codigo = :rid
			GROUP BY r.codigo, r.qearquivoin_codigo) as tb
		INNER JOIN qearquivoin a ON a.codigo=tb.qearquivoin_codigo
		INNER JOIN molecula mol ON mol.codigo=a.molecula_codigo
		INNER JOIN qeinfoscf qmin ON qmin.qeresumo_codigo=tb.codigo AND qmin.scfcycles=tb.minciclo
		INNER JOIN qeinfoscf qmax ON qmax.qeresumo_codigo=tb.codigo AND qmax.scfcycles=tb.maxciclo";
	$stBusca = $con->prepare($query);	
	$stBusca->bindParam(':rid', $id_resumo, PDO::PARAM_INT);
	$stBusca->execute();
	$rsBusca = $stBusca->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca)>0){
		$resumo = $rsBusca[0];
	}
	unset($rsBusca);
	$rsBusca = null;
	$stBusca->closeCursor();

	$query2 = "SELECT concluido,executando,erro,tamanhokb,ciclos,horas,ultimalida,
			qi.enthalpy, qi.volume, qi.density
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
			$stBusca2->bindParam(':rid', $resumo["codigo"], PDO::PARAM_INT);
			$stBusca2->execute();
			$rsBusca2 = $stBusca2->fetchAll(PDO::FETCH_ASSOC);
	if(sizeof($rsBusca2)>0){
		$op = $rsBusca2[0];
	}	
	unset($rsBusca2);
	$rsBusca2 = null;
	$stBusca2->closeCursor();
?>
<html>
<head><?php include_once 'include/head.php'; ?></head>
<body>
<?php include_once 'include/menu.php'; ?>

<div width="100%" style="padding:20px;">
<span style="font-size:16px;font-weight:bold;"><?php echo $resumo["descricao"]; ?></span>
<br>

<div width="100%" style="padding:5px;">
	
	<table width="100%" cellspacing="0" cellpadding="5" border="0" >
	<tr>
		<td></td>
		<td></td>
		<td align="center">ENTALPIA<br>(Ry)</td>
		<td align="center">VOLUME<br>(Ang^3)</td>
		<td align="center">DENSIDADE<br>(g/cm^3)</td>
		<td align="center">ITERA&Ccedil;&Otilde;ES<br>#</td>
	</tr>
	<tr>
		<td style="border-bottom: solid 1px #cccccc;" height="35"><span style="font-size:14px;font-weight:bold;color:#9f9f9f;">IN&Iacute;CIO</span></td>
		<td style="border-bottom: solid 1px #cccccc;"><span style="font-size:14px;font-weight:bold;color:#9f9f9f;">SCF step <?php echo $resumo["minciclo"]; ?></span></td>
		<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($resumo["minenthalpy"],3))); ?></span></td>
		<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($resumo["minvolume"],1))); ?></span></td>
		<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($resumo["mindensity"],3))); ?></span></td>
		<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo $resumo["miniterations"]; ?></span></td>
	</tr>
	<?php 
	
		
		if($resumo["maxciclo"]>1 && !$op["concluido"]){ ?>
		<tr>
			<td style="border-bottom: solid 1px #cccccc;" height="35"><span style="font-size:12px;font-weight:bold;color:#9f9f9f;">&Uacute;LTIMA LEITURA <?php echo $op["ultimalida"]; ?></span></td>
			<td style="border-bottom: solid 1px #cccccc;"><span style="font-size:14px;font-weight:bold;color:#9f9f9f;">SCF step <?php echo $resumo["maxciclo"]; ?></span></td>
			<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;font-weight:bold;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($resumo["maxenthalpy"],3))); ?></span></td>
			<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($resumo["maxvolume"],1))); ?></span></td>
			<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo str_replace('.', ',', str_replace(',', '', number_format($resumo["maxdensity"],3))); ?></span></td>
			<td style="border-bottom: solid 1px #cccccc;" align="center"><span style="font-size:14px;"><?php echo $resumo["maxiterations"]; ?></span></td>
		</tr>
		<?php } ?>

		<?php
		if($op["erro"]==null){
			?>	
			<tr>
				<td style="border-bottom: solid 1px #cccccc;"><span class="<?php echo ($op["executando"]?"fblue":($op["concluido"]?"fgreen":"fred")); ?>" style="font-size:14px;font-weight:bold;" >C&Aacute;LCULO <?php echo ($op["executando"]?"EM ANDAMENTO":($op["concluido"]?"CONCL&Iacute;DO":"INTERROMPIDO")); ?></span></td>				
				<?php 
				if($op["concluido"]){
					?>
					<td style="border-bottom: solid 1px #cccccc;"></td>
					<td style="border-bottom: solid 1px #cccccc;"></td><?php 
				}else{
					?>
					<td style="border-bottom: solid 1px #cccccc;"><span style="font-size:14px;font-weight:bold;color:#9f9f9f;">SCF step <?php echo $resumo["maxciclo"]+1; ?></span></td>
					<td style="border-bottom: solid 1px #cccccc;" align="center"><img src="images/load1.gif" height="25"/></td><?php 
				} ?>
				<td style="border-bottom: solid 1px #cccccc;" colspan="3"></td>
			</tr>								
			<?php 
		}else{
			?>
			<tr>
				<td style="border-bottom: solid 1px #cccccc;" height="35"><span class="fred" style="font-size:14px;font-weight:bold;" >C&Aacute;LCULO N&#195;O CONVERGIU</span></td>
				<td style="border-bottom: solid 1px #cccccc;"><span style="font-size:14px;font-weight:bold;color:#9f9f9f;">SCF step <?php echo $resumo["maxciclo"]+1; ?></span></td>
				<td style="border-bottom: solid 1px #cccccc;" align="center" colspan="4">
					<span style="font-size:16px;" class="fred" ><?php echo $op["erro"]; ?></span>
				</td>
			</tr>			
			<?php
		}
	   ?>
	   <tr>
	   <td colspan="2"></td>
		<td colspan="4" align="center">
			<span style="font-size:12px;">TEMPO DE CPU TOTAL: <b><?php echo $op["horas"]>72?number_format($op["horas"]/24,0)." DIAS E ".number_format($op["horas"]%24,0)." HORAS":$op["horas"]." HORAS";?></b> (<?php echo number_format($op["tamanhokb"]/1024,2); ?> MB)&nbsp;&nbsp;</span>		
		</td>		
	</tr>	 
	</table>		
</div>

<img src="graph/graph_resumo.php?rid=<?php echo $resumo["codigo"]; ?>&pe=1" align="left"/>
<img src="graph/graph_resumo.php?rid=<?php echo $resumo["codigo"]; ?>&pv=1" align="left"/>
<img src="graph/graph_resumo.php?rid=<?php echo $resumo["codigo"]; ?>&pd=1" align="left"/>
<img src="graph/graph_resumo.php?rid=<?php echo $resumo["codigo"]; ?>&ps=1" align="left"/>


</div>

<?php include_once 'include/bottom.php'; ?>
</body>
</html>



