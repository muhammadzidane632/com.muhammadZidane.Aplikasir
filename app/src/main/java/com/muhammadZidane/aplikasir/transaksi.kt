package com.muhammadZidane.aplikasir

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muhammadZidane.aplikasir.model.Kategori
import com.muhammadZidane.aplikasir.model.Produk
import com.muhammadZidane.aplikasir.repository.KategoriRepository
import com.muhammadZidane.aplikasir.repository.ProdukRepository
import com.muhammadZidane.aplikasir.ui.CheckoutActivity
import com.muhammadZidane.aplikasir.ui.TransaksiProdukAdapter
import java.text.NumberFormat
import java.util.*

class Transaksi : AppCompatActivity(), OnKategoriListener {

    private lateinit var containerKategori: LinearLayout
    private lateinit var btnTambahKategori: Button
    private lateinit var btnBack: ImageButton
    private lateinit var rvProduk: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var cardCheckout: View
    private lateinit var tvTotalItem: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnCheckout: Button

    private val kategoriRepository = KategoriRepository()
    private val produkRepository = ProdukRepository()
    private val listKategori = mutableListOf<Kategori>()
    private var fullProdukList = listOf<Produk>()
    private lateinit var adapter: TransaksiProdukAdapter
    
    private var selectedKategori: String = "Semua"
    private val cart = mutableMapOf<String, Pair<Produk, Int>>()

    private val checkoutLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            cart.clear()
            updateCheckoutUI()
            adapter.clearCart()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupSearch()
        
        kategoriRepository.initializeDefaultKategori()
        loadKategori()
        loadProduk()
    }

    private fun initViews() {
        containerKategori = findViewById(R.id.llKategoriContainer)
        btnTambahKategori = findViewById(R.id.btnTambah)
        btnBack = findViewById(R.id.backButton)
        rvProduk = findViewById(R.id.rvProdukTransaksi)
        etSearch = findViewById(R.id.etSearch)
        cardCheckout = findViewById(R.id.cardCheckout)
        tvTotalItem = findViewById(R.id.tvTotalItem)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        btnCheckout = findViewById(R.id.btnCheckout)

        btnBack.setOnClickListener { finish() }
        btnTambahKategori.setOnClickListener {
            val dialog = TambahKategoriDialog(this, listKategori)
            dialog.show(supportFragmentManager, "TambahKategoriDialog")
        }
        
        btnCheckout.setOnClickListener { goToCheckout() }
    }

    private fun setupRecyclerView() {
        adapter = TransaksiProdukAdapter(emptyList()) { produk, qty ->
            if (qty > 0) {
                cart[produk.id] = Pair(produk, qty)
            } else {
                cart.remove(produk.id)
            }
            updateCheckoutUI()
        }
        rvProduk.layoutManager = GridLayoutManager(this, 2)
        rvProduk.adapter = adapter
    }

    private fun updateCheckoutUI() {
        val totalItem = cart.values.sumOf { it.second }
        val totalPrice = cart.values.sumOf { it.first.harga * it.second }

        if (totalItem > 0) {
            cardCheckout.visibility = View.VISIBLE
            tvTotalItem.text = String.format(Locale.getDefault(), "%d Item", totalItem)
            val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
            tvTotalPrice.text = format.format(totalPrice).replace(",00", "")
        } else {
            cardCheckout.visibility = View.GONE
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProduk()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadKategori() {
        kategoriRepository.getAllKategori { categories ->
            listKategori.clear()
            listKategori.addAll(categories)
            
            val displayList = mutableListOf<Kategori>()
            if (categories.none { it.nama == "Semua" }) {
                displayList.add(Kategori("", "Semua"))
            }
            displayList.addAll(categories)

            containerKategori.removeAllViews()
            for (kat in displayList) {
                containerKategori.addView(createKategoriButton(kat))
            }
            containerKategori.addView(btnTambahKategori)
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
        btn.setPadding((24 * resources.displayMetrics.density).toInt(), 0, (24 * resources.displayMetrics.density).toInt(), 0)

        updateButtonUI(btn, kategori.nama == selectedKategori)

        btn.setOnClickListener {
            selectedKategori = kategori.nama
            refreshKategoriButtons()
            filterProduk()
        }
        return btn
    }

    private fun updateButtonUI(btn: Button, isSelected: Boolean) {
        btn.setBackgroundResource(if (isSelected) R.drawable.bg_cat_selected else R.drawable.bg_cat_unselected)
    }

    private fun refreshKategoriButtons() {
        for (i in 0 until containerKategori.childCount) {
            val view = containerKategori.getChildAt(i)
            if (view is Button && view != btnTambahKategori) {
                updateButtonUI(view, view.text.toString() == selectedKategori)
            }
        }
    }

    private fun loadProduk() {
        produkRepository.getAllProduk { products ->
            fullProdukList = products
            filterProduk()
        }
    }

    private fun filterProduk() {
        val query = etSearch.text.toString()
        var filtered = if (query.isEmpty()) fullProdukList else fullProdukList.filter { it.nama.contains(query, ignoreCase = true) }
        
        if (selectedKategori != "Semua") {
            filtered = filtered.filter { it.kategoriNama == selectedKategori }
        }
        adapter.updateData(filtered)
    }

    private fun goToCheckout() {
        val intent = Intent(this, CheckoutActivity::class.java)
        
        val names = cart.values.map { it.first.nama }.toTypedArray()
        val prices = cart.values.map { it.first.harga }.toLongArray()
        val qtys = cart.values.map { it.second }.toIntArray()
        
        intent.putExtra("NAMES", names)
        intent.putExtra("PRICES", prices)
        intent.putExtra("QTYS", qtys)
        
        checkoutLauncher.launch(intent)
    }

    override fun onKategoriSimpan(kategoriBaru: String) {
        kategoriRepository.tambahKategori(kategoriBaru) { _, _ -> }
    }
}
