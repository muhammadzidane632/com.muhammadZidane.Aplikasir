package com.muhammadZidane.aplikasir

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muhammadZidane.aplikasir.model.Kategori
import com.muhammadZidane.aplikasir.repository.KategoriRepository

class Transaksi : AppCompatActivity(), OnKategoriListener {

    private lateinit var containerKategori: LinearLayout
    private lateinit var btnTambah: Button
    private val kategoriRepository = KategoriRepository()
    private val listKategori = mutableListOf<Kategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        containerKategori = findViewById(R.id.llKategoriContainer)
        btnTambah = findViewById(R.id.btnTambah)

        kategoriRepository.initializeDefaultKategori()
        loadKategoriFromFirebase()

        btnTambah.setOnClickListener {
            val dialog = TambahKategoriDialog(this, listKategori)
            dialog.show(supportFragmentManager, "TambahKategoriDialog")
        }
    }

    private fun loadKategoriFromFirebase() {
        kategoriRepository.getAllKategori { kategoriList ->
            containerKategori.removeAllViews()

            listKategori.clear()
            listKategori.addAll(kategoriList)

            for (kategori in kategoriList) {
                val btnKategori = createButtonKategori(kategori)
                containerKategori.addView(btnKategori)
            }

            containerKategori.addView(btnTambah)

            if (containerKategori.childCount > 1) {
                val firstButton = containerKategori.getChildAt(0)
                if (firstButton is Button) {
                    firstButton.setBackgroundResource(R.drawable.bg_cat_selected)
                }
            }
        }
    }

    private fun createButtonKategori(kategori: Kategori): Button {
        val btnKategori = AppCompatButton(this)

        btnKategori.text = kategori.nama
        btnKategori.isAllCaps = false
        btnKategori.setTextColor(android.graphics.Color.BLACK)
        btnKategori.setBackgroundResource(R.drawable.bg_cat_unselected)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            40.dpToPx()
        )
        params.marginEnd = 8.dpToPx()
        btnKategori.layoutParams = params
        btnKategori.setPadding(24.dpToPx(), 0, 24.dpToPx(), 0)

        btnKategori.setOnClickListener {
            resetSemuaTombol()
            btnKategori.setBackgroundResource(R.drawable.bg_cat_selected)
            Toast.makeText(this, "Filter: ${kategori.nama}", Toast.LENGTH_SHORT).show()
        }

        return btnKategori
    }

    override fun onKategoriSimpan(kategoriBaru: String) {
        kategoriRepository.tambahKategori(kategoriBaru) { success, message ->
            if (success) {
                Toast.makeText(this, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetSemuaTombol() {
        for (i in 0 until containerKategori.childCount) {
            val view = containerKategori.getChildAt(i)
            if (view is Button && view.id != R.id.btnTambah) {
                view.setBackgroundResource(R.drawable.bg_cat_unselected)
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}