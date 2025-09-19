<?php
require_once '../includes/dbOperation.php';
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['borrowing_id'], $_POST['condition'], $_POST['photo'])) {
        $db = new DbOperation();
        if ($db->returnCommodity($_POST['borrowing_id'], $_POST['condition'], $_POST['photo'])) {
            $response['error'] = false;
            $response['message'] = "Pengembalian berhasil.";
        } else {
            $response['error'] = true;
            $response['message'] = "Gagal mengembalikan.";
        }
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
