<?php
$koneksi = new mysqli("localhost", "root", "", "db_asetkejuruan");

$sql = "SELECT students.id, students.name, school_classes.name AS kelas 
        FROM students 
        JOIN school_classes ON students.school_class_id = school_classes.id";
$result = $koneksi->query($sql);

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

header('Content-Type: application/json');
echo json_encode($data);
?>
