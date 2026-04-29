package com.muhammadZidane.aplikasir.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.database.*
import com.muhammadZidane.aplikasir.model.Produk
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProdukRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val produkRef: DatabaseReference = database.child("produk")

    fun getAllProduk(callback: (List<Produk>) -> Unit) {
        produkRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listProduk = mutableListOf<Produk>()
                for (dataSnapshot in snapshot.children) {
                    val produk = dataSnapshot.getValue(Produk::class.java)
                    produk?.let {
                        it.id = dataSnapshot.key ?: ""
                        listProduk.add(it)
                    }
                }
                listProduk.sortByDescending { it.timestamp }
                callback(listProduk)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // Workaround: Menggunakan Base64 untuk menyimpan gambar di Realtime Database
    // karena Firebase Storage sering meminta Blaze Plan untuk integrasi tertentu
    fun tambahProduk(context: Context, produk: Produk, imageUri: Uri?, callback: (Boolean, String) -> Unit) {
        val produkId = produkRef.push().key ?: return callback(false, "Gagal membuat ID")
        produk.id = produkId
        produk.timestamp = System.currentTimeMillis()

        if (imageUri != null) {
            val base64Image = uriToBase64(context, imageUri)
            if (base64Image != null) {
                produk.imageUrl = base64Image // Kita simpan string base64 di field imageUrl
            }
        }
        
        saveToDatabase(produk, callback)
    }

    fun updateProduk(context: Context, produk: Produk, imageUri: Uri?, callback: (Boolean, String) -> Unit) {
        if (imageUri != null) {
            val base64Image = uriToBase64(context, imageUri)
            if (base64Image != null) {
                produk.imageUrl = base64Image
            }
        }
        saveToDatabase(produk, callback)
    }

    private fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            
            // Kompres gambar agar tidak terlalu besar untuk Realtime Database (Limit 10MB per node)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveToDatabase(produk: Produk, callback: (Boolean, String) -> Unit) {
        produkRef.child(produk.id).setValue(produk)
            .addOnSuccessListener { callback(true, "Produk berhasil disimpan") }
            .addOnFailureListener { callback(false, "Gagal simpan: ${it.message}") }
    }

    fun hapusProduk(produkId: String, callback: (Boolean) -> Unit) {
        produkRef.child(produkId).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}
