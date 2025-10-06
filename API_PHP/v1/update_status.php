<?php
// Konfigurasi database
$host = "localhost";
$user = "root";      // sesuaikan
$pass = "";          // sesuaikan
$db   = "db_asetkejuruan";

// Koneksi
$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    http_response_code(500);
    die(json_encode([
        "status" => "error",
        "message" => "Koneksi gagal: " . $conn->connect_error
    ]));
}

// Set header untuk JSON
header('Content-Type: application/json');

// Ambil data dari POST
$id = isset($_POST['id']) ? (int)$_POST['id'] : 0;
$status = isset($_POST['status']) ? $_POST['status'] : '';

if (empty($id) || empty($status)) {
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => "ID dan status diperlukan"
    ]);
    exit;
}

// Validasi status yang diizinkan
$allowed_statuses = ['approved', 'rejected'];
if (!in_array($status, $allowed_statuses)) {
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => "Status tidak valid. Gunakan 'approved' atau 'rejected'"
    ]);
    exit;
}

// Update status borrowing
$sql = "UPDATE borrowings SET status = ?, updated_at = NOW() WHERE id = ?";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("si", $status, $id);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode([
                "status" => "success",
                "message" => "Status berhasil diupdate",
                "updated_id" => $id,
                "new_status" => $status
            ]);
        } else {
            http_response_code(404);
            echo json_encode([
                "status" => "error",
                "message" => "Borrowing dengan ID tersebut tidak ditemukan"
            ]);
        }
    } else {
        http_response_code(500);
        echo json_encode([
            "status" => "error",
            "message" => "Gagal mengupdate status: " . $stmt->error
        ]);
    }

    $stmt->close();
} else {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Gagal mempersiapkan statement: " . $conn->error
    ]);
}

$conn->close();
?>
