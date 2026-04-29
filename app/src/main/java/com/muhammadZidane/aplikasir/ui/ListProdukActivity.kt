package com.muhammadZidane.aplikasir.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.muhammadZidane.aplikasir.R
import com.muhammadZidane.aplikasir.databinding.ActivityListProdukBinding
import com.muhammadZidane.aplikasir.model.Kategori
import com.muhammadZidane.aplikasir.model.Produk
import com.muhammadZidane.aplikasir.repository.KategoriRepository
import com.muhammadZidane.aplikasir.repository.ProdukRepository

class ListProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListProdukBinding
    private lateinit var adapter: ProdukAdapter
    private val produkRepo = ProdukRepository()
    private val kategoriRepo = KategoriRepository()
    private var fullList = listOf<Produk>()
    private var selectedKategori: String = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearch()
        loadKategori()
        loadData()

        binding.btnBack.setOnClickListener { finish() }
        binding.fabTambahProduk.setOnClickListener {
            startActivity(Intent(this, TambahProdukActivity::class.java))
        }
    }

    private fun loadKategori() {
        kategoriRepo.getAllKategori { kategoriList ->
            binding.llKategoriContainer?.removeAllViews()
            
            val allKategori = mutableListOf<Kategori>()
            if (kategoriList.none { it.nama == "Semua" }) {
                allKategori.add(Kategori("", "Semua"))
            }
            allKategori.addAll(kategoriList)

            for (kategori in allKategori) {
                val btn = createKategoriButton(kategori)
                binding.llKategoriContainer?.addView(btn)
            }
        }
    }

    private fun createKategoriButton(kategori: Kategori): Button {
        val btn = AppCompatButton(this)
        btn.text = kategori.nama
        btn.isAllCaps = false
        btn.maxLines = 1
        btn.ellipsize = android.text.TextUtils.TruncateAt.END
        btn.setTextColor(Color.BLACK)
        
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            (40 * resources.displayMetrics.density).toInt()
        )
        params.marginEnd = (8 * resources.displayMetrics.density).toInt()
        btn.layoutParams = params
        btn.setPadding((20 * resources.displayMetrics.density).toInt(), 0, (20 * resources.displayMetrics.density).toInt(), 0)

        updateButtonUI(btn, kategori.nama == selectedKategori)

        btn.setOnClickListener {
            selectedKategori = kategori.nama
            refreshKategoriButtons()
            filter(binding.etSearch.text.toString())
        }
        return btn
    }

    private fun updateButtonUI(btn: Button, isSelected: Boolean) {
        if (isSelected) {
            btn.setBackgroundResource(R.drawable.bg_cat_selected)
        } else {
            btn.setBackgroundResource(R.drawable.bg_cat_unselected)
        }
    }

    private fun refreshKategoriButtons() {
        val container = binding.llKategoriContainer ?: return
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is Button) {
                updateButtonUI(view, view.text.toString() == selectedKategori)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ProdukAdapter(emptyList()) { produk ->
            val intent = Intent(this, TambahProdukActivity::class.java)
            intent.putExtra("PRODUK_ID", produk.id)
            intent.putExtra("NAMA", produk.nama)
            intent.putExtra("HARGA", produk.harga)
            intent.putExtra("STOK", produk.stok)
            intent.putExtra("KATEGORI_ID", produk.kategoriId)
            intent.putExtra("KATEGORI_NAMA", produk.kategoriNama)
            intent.putExtra("IMAGE_URL", produk.imageUrl)
            startActivity(intent)
        }
        binding.rvProduk.layoutManager = GridLayoutManager(this, 2)
        binding.rvProduk.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadData() {
        produkRepo.getAllProduk { produkList ->
            fullList = produkList
            filter(binding.etSearch.text.toString())
        }
    }

    private fun filter(query: String) {
        var filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.nama.contains(query, ignoreCase = true) }
        }

        if (selectedKategori != "Semua") {
            filteredList = filteredList.filter { it.kategoriNama == selectedKategori }
        }

        adapter.updateData(filteredList)
    }
}
