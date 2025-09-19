<?php
$koneksi = new mysqli("localhost", "root", "", "db_asetkejuruan");

$sql = "SELECT id, name FROM commodities";
$result = $koneksi->query($sql);

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

header('Content-Type: application/json');
echo json_encode($data);
?>
