package com.example.androidphpmysql;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private static Context ctx;
    private static final String SHARED_PREF_NAME = "mysharedpref12";

    // Key constants
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_NAME = "name";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_PROFILE_PICTURE = "profile_picture";
    private static final String KEY_NLS = "nls";
    private static final String KEY_ROLE = "role";
    private static final String KEY_LOGGED_IN = "loggedin";

    private SharedPrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public boolean userLogin(int id, String name, String email, String profilePicture, String nls, String role) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_PROFILE_PICTURE, profilePicture);
        editor.putString(KEY_NLS, nls);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_LOGGED_IN, true);

        editor.apply();
        return true;
    }

    // Overload method untuk backward compatibility
    public boolean userLogin(int id, String name, String email) {
        return userLogin(id, name, email, null, null, "user");
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    // Getter methods
    public int getUserId() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getProfilePicture() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PROFILE_PICTURE, null);
    }

    public String getNls() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NLS, null);
    }

    public String getRole() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ROLE, "user");
    }

    // Update methods
    public void updateProfilePicture(String profilePicture) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROFILE_PICTURE, profilePicture);
        editor.apply();
    }

    public void updateNls(String nls) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NLS, nls);
        editor.apply();
    }

    public void updateName(String name) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public void updateEmail(String email) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
}