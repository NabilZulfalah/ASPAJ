package com.example.androidphpmysql;

public class Constants {

    // ✅ Ganti IP ini dengan IP laptop kamu kalau berubah
    private static final String ROOT_URL = "http://10.0.2.2/android/v1/";



    // 🔹 Auth
    public static final String URL_REGISTER = ROOT_URL + "registerUser.php";
    public static final String URL_LOGIN = ROOT_URL + "loginUser.php";

    // 🔹 Asset CRUD
    public static final String URL_GET_ASSETS = ROOT_URL + "getAssetList.php";
    public static final String URL_ADD_ASSET = ROOT_URL + "addAsset.php";
    public static final String URL_UPDATE_ASSET = ROOT_URL + "updateAsset.php";
    public static final String URL_DELETE_ASSET = ROOT_URL + "deleteAsset.php";

    // 🔹 Borrowing
    public static final String URL_GET_PENDING_BORROWINGS = ROOT_URL + "get_pending_borrowings.php";
    public static final String URL_UPDATE_STATUS = ROOT_URL + "update_status.php";
}
