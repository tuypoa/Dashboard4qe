<meta http-equiv="refresh" content="30">
<title>Monitoramento PWScf<?php 
if($id_maquina!=null) { 
    echo " / ".$maquina["nome"];
}
if($id_molecula!=null) { 
    echo " / ".$molecula["nome"];
}
?></title>
<script>
    function selMaquina(id){
        window.location.href = 'maquina.php?mid='+id;
    }
</script>
<link rel="stylesheet" href="include/style.css"/>