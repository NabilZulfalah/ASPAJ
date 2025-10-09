package com.example.androidphpmysql;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * VolleySingleton adalah kelas helper yang mengimplementasikan pola desain Singleton
 * untuk mengelola RequestQueue Volley secara global dalam aplikasi.
 *
 * Mengapa menggunakan Singleton?
 * - Memastikan hanya ada satu instance RequestQueue di seluruh aplikasi.
 * - Mencegah pemborosan resource dengan membuat RequestQueue baru setiap kali diperlukan.
 * - Memungkinkan pengelolaan request yang lebih efisien dan terpusat.
 *
 * Cara penggunaan:
 * 1. Dapatkan instance singleton: VolleySingleton.getInstance(context)
 * 2. Tambahkan request ke queue: getInstance(context).addToRequestQueue(request)
 */
public class VolleySingleton {
    // Instance tunggal dari VolleySingleton (lazy initialization)
    private static VolleySingleton instance;

    // RequestQueue untuk menangani semua request Volley
    private RequestQueue requestQueue;

    // Context aplikasi untuk membuat RequestQueue
    private static Context ctx;

    /**
     * Konstruktor private untuk mencegah instansiasi langsung dari luar kelas.
     * Ini adalah bagian dari pola Singleton.
     *
     * @param context Context aplikasi
     */
    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Method untuk mendapatkan instance tunggal VolleySingleton.
     * Jika instance belum ada, akan dibuat baru (thread-safe dengan synchronized).
     *
     * @param context Context aplikasi
     * @return Instance VolleySingleton
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    /**
     * Mendapatkan RequestQueue. Jika belum ada, buat baru menggunakan Volley.newRequestQueue().
     * Menggunakan getApplicationContext() untuk menghindari memory leak.
     *
     * @return RequestQueue instance
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() penting untuk mencegah leak Activity atau BroadcastReceiver
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Menambahkan request ke RequestQueue.
     * Method ini memungkinkan kita menambahkan berbagai jenis request (StringRequest, JsonObjectRequest, dll.)
     * ke dalam queue untuk dieksekusi.
     *
     * @param <T> Tipe response dari request
     * @param req Request yang akan ditambahkan ke queue
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
