
package com.example.androidphpmysql;

    public class Constants {
    private static final String ROOT_URL = "http://192.168.0.116/";

        public static final String URL_REGISTER = ROOT_URL + "API_PHP/v1/registerUser.php";
        public static final String URL_LOGIN = ROOT_URL + "API_PHP/v1/login.php";

        // Asset CRUD URLs
        public static final String URL_GET_ASSETS = ROOT_URL + "API_PHP/v1/getCommodities.php";
        public static final String URL_ADD_ASSET = ROOT_URL + "API_PHP/v1/addAsset.php";
        public static final String URL_UPDATE_ASSET = ROOT_URL + "API_PHP/v1/updateAsset.php";
        public static final String URL_DELETE_ASSET = ROOT_URL + "API_PHP/v1/deleteAsset.php";

        // User CRUD URLs
        public static final String URL_GET_USERS = ROOT_URL + "API_PHP/v1/getUsers.php";
        public static final String URL_ADD_USER = ROOT_URL + "API_PHP/v1/addUser.php";
        public static final String URL_UPDATE_USER = ROOT_URL + "API_PHP/v1/updateUser.php";
        public static final String URL_DELETE_USER = ROOT_URL + "API_PHP/v1/deleteUser.php";

        // Kelas CRUD URLs
        public static final String URL_GET_KELAS = ROOT_URL + "API_PHP/v1/getSchoolClasses.php";
        public static final String URL_ADD_KELAS = ROOT_URL + "API_PHP/v1/addKelas.php";
        public static final String URL_UPDATE_KELAS = ROOT_URL + "API_PHP/v1/updateKelas.php";
        public static final String URL_DELETE_KELAS = ROOT_URL + "API_PHP/v1/deleteKelas.php";

        // 🔹 Borrowing
        public static final String URL_GET_PENDING_BORROWINGS = ROOT_URL + "get_pending_borrowings.php";
        public static final String URL_UPDATE_STATUS = ROOT_URL + "update_status.php";
    }

