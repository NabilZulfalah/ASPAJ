<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../includes/dbConnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['id']) && isset($_POST['name']) && isset($_POST['code']) && isset($_POST['stock']) && isset($_POST['lokasi']) &&
        isset($_POST['jurusan']) && isset($_POST['merk']) && isset($_POST['harga_satuan']) &&
        isset($_POST['sumber']) && isset($_POST['tahun']) && isset($_POST['deskripsi']) && isset($_POST['condition'])) {

        $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

        if ($conn->connect_error) {
            $response['success'] = false;
            $response['message'] = 'Database connection failed';
            echo json_encode($response);
            exit();
        }

        $id = $_POST['id'];
        $name = $_POST['name'];
        $code = $_POST['code'];
        $stock = $_POST['stock'];
        $lokasi = $_POST['lokasi'];
        $jurusan = $_POST['jurusan'];
        $merk = $_POST['merk'];
        $harga_satuan = $_POST['harga_satuan'];
        $sumber = $_POST['sumber'];
        $tahun = $_POST['tahun'];
        $deskripsi = $_POST['deskripsi'];
        $condition = $_POST['condition'];

        $sql = "UPDATE commodities SET name=?, code=?, stock=?, lokasi=?, jurusan=?, merk=?, harga_satuan=?, sumber=?, tahun=?, deskripsi=?, `condition`=? WHERE id=?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssssssssssi", $name, $code, $stock, $lokasi, $jurusan, $merk, $harga_satuan, $sumber, $tahun, $deskripsi, $condition, $id);

        if ($stmt->execute()) {
            $response['success'] = true;
            $response['message'] = 'Asset updated successfully';
        } else {
            $response['success'] = false;
            $response['message'] = 'Failed to update asset';
        }

        $stmt->close();
        $conn->close();
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
