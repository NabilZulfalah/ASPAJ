package com.example.androidphpmysql

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BarangAdapter(
    private val barangList: List<Barang>,
    private val onEditClick: (Barang) -> Unit,
    private val onDeleteClick: (Barang) -> Unit
) : RecyclerView.Adapter<BarangAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textNama: TextView = view.findViewById(R.id.textNamaBarang)
        val textKode: TextView = view.findViewById(R.id.textKodeBarang)
        val textStok: TextView = view.findViewById(R.id.textStok)
        val buttonEdit: Button = view.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val barang = barangList[position]
        holder.textNama.text = barang.nama
        holder.textKode.text = barang.kode
        holder.textStok.text = "Stok: ${barang.stok}"
        holder.textStok.setTextColor(if (barang.stok<5) Color.RED else Color.GRAY)

        holder.buttonEdit.setOnClickListener { onEditClick(barang) }
        holder.buttonDelete.setOnClickListener { onDeleteClick(barang) }
    }

    override fun getItemCount(): Int = barangList.size
}
