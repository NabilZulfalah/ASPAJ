package com.example.androidphpmysql;

/**
 * Constants class menyimpan semua URL endpoint API Laravel yang digunakan dalam aplikasi.
 * Setiap konstanta URL memiliki komentar yang menjelaskan fungsi endpoint tersebut.
 */
public class Constants {
    // Base URL API Laravel
    public static final String BASE_URL = "http://192.168.4.164:5555/api/";


    // Endpoint untuk mengambil, menambah, mengubah, dan menghapus data aset
    public static final String URL_GET_ASSETS = BASE_URL + "assets"; // GET: Mendapatkan daftar aset
    public static final String GET_ASSETS_URL = BASE_URL + "assets"; // GET: Mendapatkan daftar aset
    public static final String URL_ADD_ASSET = BASE_URL + "assets"; // POST: Menambah aset baru
    public static final String URL_UPDATE_ASSET = BASE_URL + "assets/{id}"; // PUT: Memperbarui aset
    public static final String URL_DELETE_ASSET = BASE_URL + "assets/{id}"; // DELETE: Menghapus aset

    // Endpoint untuk mengambil dan menambah data user
    public static final String URL_GET_USERS = BASE_URL + "users"; // GET: Mendapatkan daftar user
    public static final String GET_USERS_URL = BASE_URL + "users"; // GET: Mendapatkan daftar user
    public static final String URL_ADD_USER = BASE_URL + "users"; // POST: Menambah user baru
    public static final String URL_UPDATE_USER = BASE_URL + "users/{id}"; // PUT: Memperbarui user
    public static final String URL_DELETE_USER = BASE_URL + "users/{id}"; // DELETE: Menghapus user
    public static final String POST_USER_URL = BASE_URL + "users"; // POST: Menambah user baru

    // Endpoint untuk login user
    public static final String LOGIN_URL = BASE_URL + "login"; // POST: Login user dengan email dan password
    public static final String URL_REGISTER = BASE_URL + "register"; // POST: Register user baru

    // Endpoint untuk memanage peminjaman
    public static final String GET_BORROWINGS_URL = BASE_URL + "borrowings"; // GET: Mendapatkan daftar peminjaman
    public static final String STORE_BORROWING_URL = BASE_URL + "borrowings"; // POST: Membuat peminjaman baru
    public static final String GET_PENDING_BORROWINGS_URL = BASE_URL + "borrowings/pending"; // GET: Mendapatkan daftar peminjaman yang pending
    public static final String POST_UPDATE_BORROWING_STATUS_URL = BASE_URL + "borrowings/update-status"; // POST: Memperbarui status peminjaman
    public static final String POST_RETURN_BORROWING_URL = BASE_URL + "borrowings/{id}/return"; // POST: Mengembalikan item peminjaman

    // Endpoint untuk mengambil data kelas sekolah
    public static final String GET_SCHOOL_CLASSES_URL = BASE_URL + "school-classes"; // GET: Mendapatkan daftar kelas sekolah

    // Endpoint untuk menambah dan mengupdate kelas sekolah
    public static final String URL_ADD_KELAS = BASE_URL + "school-classes"; // POST: Menambah kelas baru
    public static final String URL_UPDATE_KELAS = BASE_URL + "school-classes/{id}"; // PUT: Memperbarui kelas
    public static final String URL_GET_KELAS = BASE_URL + "school-classes"; // GET: Mendapatkan daftar kelas sekolah
    public static final String URL_DELETE_KELAS = BASE_URL + "school-classes/{id}"; // DELETE: Menghapus kelas

    // Student-specific endpoints
    public static final String URL_GET_COMMODITIES = BASE_URL + "commodities"; // GET: Mendapatkan daftar komoditas untuk peminjaman
    public static final String STUDENT_DASHBOARD_STATS = BASE_URL + "student/dashboard-stats"; // GET: Stats untuk dashboard student
    public static final String STUDENT_ACTIVE_BORROWINGS = BASE_URL + "student/active-borrowings"; // GET: Peminjaman aktif student
    public static final String STUDENT_RECENT_REQUESTS = BASE_URL + "student/recent-requests"; // GET: Request terbaru
    public static final String STUDENT_BORROWING_HISTORY = BASE_URL + "student/borrowing-history"; // GET: Riwayat peminjaman
    public static final String STUDENT_BORROWINGS = BASE_URL + "student/borrowing-history"; // GET: Semua peminjaman student

    // Admin-specific endpoints
    public static final String ADMIN_DASHBOARD_STATS = BASE_URL + "admin/dashboard-stats"; // GET: Stats untuk dashboard admin

    // Profile endpoints
    public static final String URL_USER_PROFILE = BASE_URL + "user"; // GET: Get current user profile
    public static final String URL_UPDATE_PROFILE = BASE_URL + "user"; // PUT: Update user profile
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "change-password"; // POST: Change user password

    // Storage base for profile pictures
    public static final String STORAGE_BASE = "http://192.168.4.164:5555/storage/profile_pictures/";
        }
