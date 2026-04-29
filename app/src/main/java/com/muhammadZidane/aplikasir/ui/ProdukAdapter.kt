package com.muhammadZidane.aplikasir.ui

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muhammadZidane.aplikasir.R
import com.muhammadZidane.aplikasir.model.Produk
import java.text.NumberFormat
import java.util.Locale

class ProdukAdapter(
    private var listProduk: List<Produk>,
    private val onClick: (Produk) -> Unit
) : RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduk: ImageView = view.findViewById(R.id.ivProduk)
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvKategori: TextView = view.findViewById(R.id.tvKategoriProduk)
        val tvStok: TextView = view.findViewById(R.id.tvStokProduk)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaProduk)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.tvNama.text = produk.nama
        holder.tvKategori.text = produk.kategoriNama
        holder.tvStok.text = "Stok: ${produk.stok}"
        
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvHarga.text = format.format(produk.harga).replace(",00", "")

        if (produk.imageUrl.isNotEmpty()) {
            // Handle Base64 or URL
            if (produk.imageUrl.length > 500) {
                try {
                    val imageByteArray = Base64.decode(produk.imageUrl, Base64.DEFAULT)
                    Glide.with(holder.itemView.context)
                        .load(imageByteArray)
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivProduk)
                    holder.ivProduk.setPadding(0, 0, 0, 0)
                } catch (e: Exception) {
                    setDefaultImage(holder.ivProduk)
                }
            } else {
                Glide.with(holder.itemView.context)
                    .load(produk.imageUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivProduk)
                holder.ivProduk.setPadding(0, 0, 0, 0)
            }
        } else {
            setDefaultImage(holder.ivProduk)
        }

        holder.itemView.setOnClickListener { onClick(produk) }
    }

    private fun setDefaultImage(imageView: ImageView) {
        imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        imageView.setBackgroundColor(0xFFEEEEEE.toInt())
        val padding = (48 * imageView.resources.displayMetrics.density).toInt()
        imageView.setPadding(padding, padding, padding, padding)
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<Produk>) {
        listProduk = newList
        notifyDataSetChanged()
    }
}
