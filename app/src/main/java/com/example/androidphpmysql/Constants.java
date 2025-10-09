
package com.example.androidphpmysql;

/**
 * Constants class menyimpan semua URL endpoint API Laravel yang digunakan dalam aplikasi.
 * Setiap konstanta URL memiliki komentar yang menjelaskan fungsi endpoint tersebut.
 */
public class Constants {
    // Base URL API PHP
    public static final String BASE_URL = "http://192.168.0.117:8000/api/";

    // Endpoint untuk mengambil, menambah, mengubah, dan menghapus data aset
    public static final String URL_GET_ASSETS = BASE_URL + "assets"; // GET: Mendapatkan daftar aset
    public static final String GET_ASSETS_URL = BASE_URL + "assets"; // GET: Mendapatkan daftar aset
    public static final String URL_ADD_ASSET = BASE_URL + "assets"; // POST: Menambah aset baru
    public static final String URL_UPDATE_ASSET = BASE_URL + "assets"; // PUT: Memperbarui aset (biasanya dengan ID di URL)
    public static final String URL_DELETE_ASSET = BASE_URL + "assets"; // DELETE: Menghapus aset (biasanya dengan ID di URL)

    // Endpoint untuk mengambil dan menambah data user
    public static final String URL_GET_USERS = BASE_URL + "users"; // GET: Mendapatkan daftar user
    public static final String GET_USERS_URL = BASE_URL + "users"; // GET: Mendapatkan daftar user
    public static final String URL_ADD_USER = BASE_URL + "users"; // POST: Menambah user baru
    public static final String URL_UPDATE_USER = BASE_URL + "users"; // PUT: Memperbarui user (biasanya dengan ID di URL)
    public static final String URL_DELETE_USER = BASE_URL + "users"; // DELETE: Menghapus user (biasanya dengan ID di URL)
    public static final String POST_USER_URL = BASE_URL + "users"; // POST: Menambah user baru

    // Endpoint untuk login user
    public static final String LOGIN_URL = BASE_URL + "login"; // POST: Login user dengan email dan password
    public static final String URL_REGISTER = BASE_URL + "register"; // POST: Register user baru

    // Endpoint untuk memanage peminjaman
    public static final String GET_BORROWINGS_URL = BASE_URL + "borrowings"; // GET: Mendapatkan daftar peminjaman
    public static final String GET_PENDING_BORROWINGS_URL = BASE_URL + "borrowings/pending"; // GET: Mendapatkan daftar peminjaman yang pending
    public static final String POST_UPDATE_BORROWING_STATUS_URL = BASE_URL + "borrowings/update-status"; // POST: Memperbarui status peminjaman

    // Endpoint untuk mengambil data kelas sekolah
    public static final String GET_SCHOOL_CLASSES_URL = BASE_URL + "school-classes"; // GET: Mendapatkan daftar kelas sekolah

    // Endpoint untuk menambah dan mengupdate kelas sekolah
    public static final String URL_ADD_KELAS = BASE_URL + "school-classes"; // POST: Menambah kelas baru
    public static final String URL_UPDATE_KELAS = BASE_URL + "school-classes"; // PUT: Memperbarui kelas (biasanya dengan ID di URL)
    public static final String URL_GET_KELAS = BASE_URL + "school-classes"; // GET: Mendapatkan daftar kelas sekolah
    public static final String URL_DELETE_KELAS = BASE_URL + "school-classes"; // DELETE: Menghapus kelas (biasanya dengan ID di URL)
}

