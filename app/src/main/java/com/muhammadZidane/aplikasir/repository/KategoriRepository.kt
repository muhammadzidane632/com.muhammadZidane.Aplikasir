package com.muhammadZidane.aplikasir.repository

import com.google.firebase.database.*
import com.muhammadZidane.aplikasir.model.Kategori

class KategoriRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val kategoriRef: DatabaseReference = database.child("kategori")

    fun getAllKategori(callback: (List<Kategori>) -> Unit) {
        kategoriRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKategori = mutableListOf<Kategori>()

                for (dataSnapshot in snapshot.children) {
                    val kategori = dataSnapshot.getValue(Kategori::class.java)
                    kategori?.let {
                        it.id = dataSnapshot.key ?: ""
                        listKategori.add(it)
                    }
                }

                listKategori.sortBy { it.timestamp }
                callback(listKategori)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun tambahKategori(namaKategori: String, callback: (Boolean, String) -> Unit) {
        val kategoriId = kategoriRef.push().key

        if (kategoriId == null) {
            callback(false, "Gagal membuat ID kategori")
            return
        }

        val kategori = Kategori(
            id = kategoriId,
            nama = namaKategori,
            timestamp = System.currentTimeMillis()
        )

        kategoriRef.child(kategoriId).setValue(kategori)
            .addOnSuccessListener {
                callback(true, "Kategori berhasil ditambahkan")
            }
            .addOnFailureListener { exception ->
                callback(false, "Gagal menambahkan kategori: ${exception.message}")
            }
    }

    fun hapusKategori(kategoriId: String, callback: (Boolean) -> Unit) {
        kategoriRef.child(kategoriId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun initializeDefaultKategori() {
        kategoriRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                val defaultKategori = listOf("Semua", "Penjualan")

                defaultKategori.forEach { namaKategori ->
                    val kategoriId = kategoriRef.push().key
                    if (kategoriId != null) {
                        val kategori = Kategori(
                            id = kategoriId,
                            nama = namaKategori,
                            timestamp = System.currentTimeMillis()
                        )
                        kategoriRef.child(kategoriId).setValue(kategori)
                    }
                }
            }
        }
    }
}
