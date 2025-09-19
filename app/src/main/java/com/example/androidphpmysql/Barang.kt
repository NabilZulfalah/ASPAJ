package com.example.androidphpmysql

data class Barang(
    val id: String,
    val nama: String,
    val kode: String,
    val stok: Int,
    val lokasi: String,
    val jurusan: String,
    val merk: String,
    val sumberDana: String,
    val tahun: String,
    val deskripsi: String,
    val harga: Double
)