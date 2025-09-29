package com.example.androidphpmysql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class BarangAdapter(
    private val assetList: MutableList<Asset>,
    private val onEditClick: (Asset) -> Unit,
    private val onDeleteClick: (Asset) -> Unit
) : RecyclerView.Adapter<BarangAdapter.AssetViewHolder>() {

    // Fungsi untuk update data
    fun updateData(newList: List<Asset>) {
        assetList.clear()
        assetList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_barang, parent, false)
        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assetList[position]
        holder.bind(asset)
    }

    override fun getItemCount(): Int = assetList.size

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ✅ PERBAIKAN: Sesuaikan ID dengan layout item_barang.xml
        private val textNamaBarang: TextView = itemView.findViewById(R.id.textNamaBarang)
        private val textKodeBarang: TextView = itemView.findViewById(R.id.textKodeBarang)
        private val textStok: TextView = itemView.findViewById(R.id.textStok)
        private val textLokasi: TextView = itemView.findViewById(R.id.textLokasi)
        private val textJurusan: TextView = itemView.findViewById(R.id.textJurusan)
        private val textMerk: TextView = itemView.findViewById(R.id.textMerk)
        private val textSumberDana: TextView = itemView.findViewById(R.id.textSumberDana)
        private val textTahun: TextView = itemView.findViewById(R.id.textTahun)
        private val textDeskripsi: TextView = itemView.findViewById(R.id.textDeskripsi)
        private val textHarga: TextView = itemView.findViewById(R.id.textHarga)
        private val buttonEdit: MaterialButton = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: MaterialButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(asset: Asset) {
            // Set data ke view dengan null safety
            textNamaBarang.text = asset.namaBarang ?: "Nama tidak tersedia"
            textKodeBarang.text = asset.kodeBarang ?: "Kode tidak tersedia"
            textStok.text = "Stok: ${asset.jumlahStok}"
            textLokasi.text = asset.lokasiBarang ?: "Lokasi tidak tersedia"
            textJurusan.text = asset.jurusanBarang ?: "Jurusan tidak tersedia"
            textMerk.text = asset.merk ?: "Merk tidak tersedia"
            textSumberDana.text = asset.sumber ?: "Sumber tidak tersedia"
            textTahun.text = "Tahun: ${asset.tahun ?: "N/A"}"
            textDeskripsi.text = asset.deskripsi ?: "Deskripsi tidak tersedia"

            // Format harga dengan separator ribuan
            val hargaFormatted = if (asset.hargaSatuan > 0) {
                "Rp ${String.format("%,d", asset.hargaSatuan.toInt()).replace(',', '.')}"
            } else {
                "Rp 0"
            }
            textHarga.text = hargaFormatted

            // Set click listeners
            buttonEdit.setOnClickListener { onEditClick(asset) }
            buttonDelete.setOnClickListener { onDeleteClick(asset) }
        }
    }
}