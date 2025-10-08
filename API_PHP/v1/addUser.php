<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../includes/dbConnect.php';
require_once '../includes/dbOperation.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['role']) && isset($_POST['approval_status'])) {
        $db = new dbOperation();

        $result = $db->addUser(
            $_POST['name'],
            $_POST['email'],
            $_POST['role'],
            $_POST['approval_status']
        );

        if ($result == 1) {
            $response['success'] = true;
            $response['message'] = 'User added successfully';
        } elseif ($result == 2) {
            $response['success'] = false;
            $response['message'] = 'Some error occurred';
        } elseif ($result == 0) {
            $response['success'] = false;
            $response['message'] = 'User already exists';
        }
    } else {
        $response['success'] = false;
        $response['message'] = 'Required fields are missing';
    }
} else {
    $response['success'] = false;
    $response['message'] = 'Invalid request method';
}

echo json_encode($response);
?>
