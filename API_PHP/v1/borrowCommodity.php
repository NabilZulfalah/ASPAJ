<?php
require_once '../includes/dbOperation.php';
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['student_id'], $_POST['commodity_id'], $_POST['quantity'], $_POST['tujuan'])) {
        $db = new DbOperation();
        if ($db->borrowCommodity($_POST['student_id'], $_POST['commodity_id'], $_POST['quantity'], $_POST['tujuan'])) {
            $response['error'] = false;
            $response['message'] = "Peminjaman berhasil (menunggu persetujuan).";
        } else {
            $response['error'] = true;
            $response['message'] = "Gagal meminjam.";
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
