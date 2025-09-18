<?php
require_once '../includes/dbOperation.php';
$db = new DbOperation();
$response = array();

$response['commodities'] = $db->getCommodities();
$response['error'] = false;

echo json_encode($response);
?>
