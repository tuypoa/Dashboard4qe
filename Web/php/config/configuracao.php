<?php 
require_once("Ambiente.class.php"); 

try{
	$con = new PDO("pgsql:host=".Ambiente::$host.";port=5432;dbname=".Ambiente::$dbase.";", Ambiente::$user, Ambiente::$pswd);
}catch (PDOException $e) {
	echo 'Connection failed: ' . $e->getMessage();
	//echo json_encode($return);
	die();
}

//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
?>
