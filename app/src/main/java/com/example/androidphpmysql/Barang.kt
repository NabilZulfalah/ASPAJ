package com.example.androidphpmysql

data class Barang(
    val id: Int = 0,
    val nama: String = "",
    val kode: String = "",
    val stok: Int = 0,
    val jurusan: String = "",
    val lokasi: String = "",
    val merk: String = "",
    val sumberDana: String = "",
    val tahun: String = "",
    val deskripsi: String = "",
    val harga: Double = 0.0
)