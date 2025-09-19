<?php
$koneksi = new mysqli("localhost", "root", "", "db_asetkejuruan");

$nama       = $_POST['nama'] ?? '';
$kelas      = $_POST['kelas'] ?? '';
$tgl_pinjam = $_POST['tgl_pinjam'] ?? '';
$tgl_kembali= $_POST['tgl_kembali'] ?? '';
$tujuan     = $_POST['tujuan'] ?? '';
$barang     = $_POST['barang'] ?? '';
$jumlah     = $_POST['jumlah'] ?? '';

// cari student_id berdasarkan nama
$student_id = null;
$q1 = $koneksi->prepare("SELECT id FROM students WHERE name=? LIMIT 1");
$q1->bind_param("s", $nama);
$q1->execute();
$res = $q1->get_result();
if ($row = $res->fetch_assoc()) {
    $student_id = $row['id'];
}
$q1->close();

// cari commodity_id berdasarkan nama barang
$commodity_id = null;
$q2 = $koneksi->prepare("SELECT id FROM commodities WHERE name=? LIMIT 1");
$q2->bind_param("s", $barang);
$q2->execute();
$res2 = $q2->get_result();
if ($row2 = $res2->fetch_assoc()) {
    $commodity_id = $row2['id'];
}
$q2->close();

if ($student_id && $commodity_id) {
    // Default values
    $status = "pending"; // bisa "pending" / "approved" / "returned"
    $returned_by = null;
    $return_condition = null;
    $return_photo = null;
    $created_at = date("Y-m-d H:i:s");
    $updated_at = date("Y-m-d H:i:s");

    $stmt = $koneksi->prepare("INSERT INTO borrowings 
        (student_id, commodity_id, borrow_date, return_date, status, returned_by, return_condition, return_photo, created_at, updated_at, quantity, tujuan, name, class) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    $stmt->bind_param(
        "iissssssssisss",
        $student_id,
        $commodity_id,
        $tgl_pinjam,
        $tgl_kembali,
        $status,
        $returned_by,
        $return_condition,
        $return_photo,
        $created_at,
        $updated_at,
        $jumlah,
        $tujuan,
        $nama,
        $kelas
    );

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Peminjaman berhasil disimpan"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Gagal menyimpan data: " . $stmt->error]);
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Siswa atau barang tidak ditemukan"]);
}
?>
