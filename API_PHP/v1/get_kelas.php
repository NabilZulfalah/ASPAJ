<?php
$koneksi = new mysqli("localhost", "root", "", "db_asetkejuruan");

$sql = "SELECT name FROM school_classes";
$result = $koneksi->query($sql);

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row['name'];
}

header('Content-Type: application/json');
echo json_encode($data);
?>
