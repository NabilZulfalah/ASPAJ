package com.example.androidphpmysql

class Asset(
    var id: Int = 0,
    var namaBarang: String? = null,
    var kodeBarang: String? = null,
    var jumlahStok: Int = 0,  // Tetap Int
    var lokasiBarang: String? = null,
    var jurusanBarang: String? = null,
    var merk: String? = null,
    var hargaSatuan: Double = 0.0,
    var sumber: String? = null,
    var tahun: String? = null,
    var deskripsi: String? = null
)
