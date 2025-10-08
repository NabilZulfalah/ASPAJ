<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

require_once '../includes/dbConnect.php';

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

if ($conn->connect_error) {
    die(json_encode(['error' => true, 'message' => 'Database connection failed']));
}

$sql = "SELECT id, name, level, program_study, capacity, description FROM school_classes";
$result = $conn->query($sql);

$school_classes = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $school_classes[] = $row;
    }
    echo json_encode(['error' => false, 'school_classes' => $school_classes]);
} else {
    echo json_encode(['error' => true, 'message' => 'Failed to fetch school classes']);
}

$conn->close();
?>
