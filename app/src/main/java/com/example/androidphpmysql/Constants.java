package com.example.androidphpmysql;

    public class Constants {
        private static final String ROOT_URL = "http://192.168.1.121/android/v1/";

        public static final String URL_REGISTER = ROOT_URL + "registerUser.php";
        public static final String URL_LOGIN = ROOT_URL + "loginUser.php";

        // Asset CRUD URLs
        public static final String URL_GET_ASSETS = ROOT_URL + "getAssetList.php";
        public static final String URL_ADD_ASSET = ROOT_URL + "addAsset.php";
        public static final String URL_UPDATE_ASSET = ROOT_URL + "updateAsset.php";
        public static final String URL_DELETE_ASSET = ROOT_URL + "deleteAsset.php";

        // User CRUD URLs
        public static final String URL_GET_USERS = ROOT_URL + "getUsers.php";
        public static final String URL_ADD_USER = ROOT_URL + "addUser.php";
        public static final String URL_UPDATE_USER = ROOT_URL + "updateUser.php";
        public static final String URL_DELETE_USER = ROOT_URL + "deleteUser.php";

        // Kelas CRUD URLs
        public static final String URL_GET_KELAS = ROOT_URL + "getKelas.php";
        public static final String URL_ADD_KELAS = ROOT_URL + "addKelas.php";
        public static final String URL_UPDATE_KELAS = ROOT_URL + "updateKelas.php";
        public static final String URL_DELETE_KELAS = ROOT_URL + "deleteKelas.php";

        // 🔹 Borrowing
        public static final String URL_GET_PENDING_BORROWINGS = ROOT_URL + "get_pending_borrowings.php";
        public static final String URL_UPDATE_STATUS = ROOT_URL + "update_status.php";
    }

