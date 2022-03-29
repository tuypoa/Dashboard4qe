<?php if($id_maquina==null) { ?>
    <meta http-equiv="refresh" content="30">
<?php } ?>
<title>In&iacute;cio<?php if($id_maquina!=null) { 
    echo " / M&aacute;quina / ".$maquina["nome"];
}?></title>
<script>
    function selMaquina(id){
        window.location.href = 'maquina.php?mid='+id;
    }
</script>
<link rel="stylesheet" href="include/style.css"/>