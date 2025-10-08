9<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

require_once '../includes/dbConnect.php';

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

if ($conn->connect_error) {
    die(json_encode(['error' => true, 'message' => 'Database connection failed']));
}

$sql = "SELECT id, name, email, role, approval_status FROM users";
$result = $conn->query($sql);

$users = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
    echo json_encode(['error' => false, 'users' => $users]);
} else {
    echo json_encode(['error' => true, 'message' => 'Failed to fetch users']);
}

$conn->close();
?>
