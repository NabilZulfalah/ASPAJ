<?php
require_once '../includes/dbConnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['identifier']) && isset($_POST['password'])) {
        $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
        if ($conn->connect_error) {
            $response['error'] = true;
            $response['message'] = "Connection failed: " . $conn->connect_error;
            echo json_encode($response);
            exit();
        }

        $identifier = $_POST['identifier'];
        $password = $_POST['password'];

        $stmt = $conn->prepare("SELECT id, name, email, password FROM users WHERE email=? OR name=?");
        $stmt->bind_param("ss", $identifier, $identifier);
        $stmt->execute();
        $result = $stmt->get_result()->fetch_assoc();

        if ($result && password_verify($password, $result['password'])) {
            unset($result['password']);
            $response['error'] = false;
            $response['user'] = $result;
        } else {
            $response['error'] = true;
            $response['message'] = "Email/Username atau password salah.";
        }
        $stmt->close();
        $conn->close();
    } else {
        $response['error'] = true;
        $response['message'] = "Parameter tidak lengkap.";
    }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request.";
}

echo json_encode($response);
?>
