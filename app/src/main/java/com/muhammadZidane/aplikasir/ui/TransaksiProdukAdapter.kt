package com.muhammadZidane.aplikasir.ui

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muhammadZidane.aplikasir.R
import com.muhammadZidane.aplikasir.model.Produk
import java.text.NumberFormat
import java.util.Locale

class TransaksiProdukAdapter(
    private var listProduk: List<Produk>,
    private val onQuantityChanged: (Produk, Int) -> Unit
) : RecyclerView.Adapter<TransaksiProdukAdapter.ViewHolder>() {

    private val cart = mutableMapOf<String, Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduk: ImageView = view.findViewById(R.id.ivProduk)
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaProduk)
        val tvQty: TextView = view.findViewById(R.id.tvQuantity)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_transaksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.tvNama.text = produk.nama
        
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvHarga.text = format.format(produk.harga).replace(",00", "")

        val qty = cart[produk.id] ?: 0
        holder.tvQty.text = qty.toString()

        if (produk.imageUrl.isNotEmpty()) {
            if (produk.imageUrl.length > 500) { // Base64 check
                try {
                    val imageByteArray = Base64.decode(produk.imageUrl, Base64.DEFAULT)
                    Glide.with(holder.itemView.context)
                        .load(imageByteArray)
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_input_add)
                        .into(holder.ivProduk)
                    holder.ivProduk.setPadding(0, 0, 0, 0)
                } catch (e: Exception) {
                    setDefaultImage(holder.ivProduk)
                }
            } else {
                Glide.with(holder.itemView.context)
                    .load(produk.imageUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_input_add)
                    .into(holder.ivProduk)
                holder.ivProduk.setPadding(0, 0, 0, 0)
            }
        } else {
            setDefaultImage(holder.ivProduk)
        }

        holder.btnPlus.setOnClickListener {
            val currentQty = cart[produk.id] ?: 0
            val newQty = currentQty + 1
            cart[produk.id] = newQty
            holder.tvQty.text = newQty.toString()
            onQuantityChanged(produk, newQty)
        }

        holder.btnMinus.setOnClickListener {
            val currentQty = cart[produk.id] ?: 0
            if (currentQty > 0) {
                val newQty = currentQty - 1
                if (newQty == 0) cart.remove(produk.id) else cart[produk.id] = newQty
                holder.tvQty.text = newQty.toString()
                onQuantityChanged(produk, newQty)
            }
        }
    }

    private fun setDefaultImage(imageView: ImageView) {
        imageView.setImageResource(android.R.drawable.ic_input_add)
        imageView.setBackgroundColor(0xFFEEEEEE.toInt())
        val padding = (36 * imageView.resources.displayMetrics.density).toInt()
        imageView.setPadding(padding, padding, padding, padding)
    }

    override fun getItemCount(): Int = listProduk.size

    fun updateData(newList: List<Produk>) {
        listProduk = newList
        notifyDataSetChanged()
    }
    
    fun clearCart() {
        cart.clear()
        notifyDataSetChanged()
    }
}
