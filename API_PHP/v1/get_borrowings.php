<?php
header('Content-Type: application/json');

// Database connection
$koneksi = new mysqli("localhost", "root", "", "db_asetkejuruan");
if ($koneksi->connect_error) {
    echo json_encode(["error" => "Database connection failed: " . $koneksi->connect_error]);
    exit;
}

// Get parameters
$statusFilter = $_GET['status'] ?? '';
$jurusanFilter = $_GET['jurusan'] ?? '';
$kelasFilter = $_GET['kelas'] ?? '';
$nameFilter = $_GET['name'] ?? '';

// Build query
$query = "SELECT
    @row_number:=@row_number+1 AS no,
    u.profile_picture AS foto_profile,
    b.name AS nama_murid,
    b.class AS kelas,
    CONCAT(c.name, ' ', b.quantity) AS barang_jumlah,
    b.tujuan,
    DATE_FORMAT(b.borrow_date, '%Y-%m-%d %H:%i') AS tanggal_jam,
    b.status,
    b.return_condition AS kondisi_pengembalian,
    o.name AS dikembalikan_oleh,
    b.return_photo AS photo,
    'Lihat' AS aksi
FROM borrowings b
LEFT JOIN students s ON b.student_id = s.id
LEFT JOIN users u ON s.user_id = u.id
LEFT JOIN commodities c ON b.commodity_id = c.id
LEFT JOIN officers o ON b.returned_by = o.id
CROSS JOIN (SELECT @row_number:=0) AS rn
WHERE 1=1";

// Add filters
$params = [];
$types = '';

if (!empty($statusFilter) && $statusFilter !== 'Semua Status') {
    $query .= " AND b.status = ?";
    $params[] = $statusFilter;
    $types .= 's';
}

if (!empty($jurusanFilter) && $jurusanFilter !== 'Semua Jurusan') {
    $query .= " AND c.jurusan = ?";
    $params[] = $jurusanFilter;
    $types .= 's';
}

if (!empty($kelasFilter) && $kelasFilter !== 'Semua Kelas') {
    $query .= " AND b.class LIKE ?";
    $params[] = '%' . $kelasFilter . '%';
    $types .= 's';
}

if (!empty($nameFilter)) {
    $query .= " AND b.name LIKE ?";
    $params[] = '%' . $nameFilter . '%';
    $types .= 's';
}

$query .= " ORDER BY b.id DESC";

// Prepare and execute
$stmt = $koneksi->prepare($query);
if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}

if ($stmt->execute()) {
    $result = $stmt->get_result();
    $data = [];
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
    echo json_encode($data);
} else {
    echo json_encode(["error" => "Query failed: " . $stmt->error]);
}

$stmt->close();
$koneksi->close();
?>
