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

// Ambil data borrowings yang masih pending
$sql = "SELECT
            b.id,
            s.name AS student_name,
            s.id AS student_id,
            b.status,
            b.tujuan,
            b.class,
            b.borrow_date,
            b.return_date
        FROM borrowings b
        JOIN students s ON b.student_id = s.id
        WHERE b.status = 'pending'
        ORDER BY b.id DESC";

$result = $conn->query($sql);

$pending = [];
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $pending[] = $row;
    }
}

// Output JSON
header('Content-Type: application/json');
echo json_encode([
    "status" => "success",
    "count"  => count($pending),
    "data"   => $pending
], JSON_PRETTY_PRINT);

$conn->close();
?>
