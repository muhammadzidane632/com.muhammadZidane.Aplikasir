package com.muhammadZidane.aplikasir.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.muhammadZidane.aplikasir.R
import com.muhammadZidane.aplikasir.databinding.ActivityTambahProdukBinding
import com.muhammadZidane.aplikasir.model.Kategori
import com.muhammadZidane.aplikasir.model.Produk
import com.muhammadZidane.aplikasir.repository.KategoriRepository
import com.muhammadZidane.aplikasir.repository.ProdukRepository
import java.text.NumberFormat
import java.util.*

class TambahProdukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahProdukBinding
    private val produkRepo = ProdukRepository()
    private val kategoriRepo = KategoriRepository()
    private var listKategori = listOf<Kategori>()
    private var produkId: String? = null
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivProduk.let { imageView ->
                Glide.with(this).load(selectedImageUri).centerCrop().into(imageView)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTambahProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPriceFormatter()

        produkId = intent.getStringExtra("PRODUK_ID")
        if (produkId != null) {
            binding.tvTitle.text = "Edit Produk"
            binding.btnSimpan.text = "Update Produk"
            binding.btnHapus.visibility = View.VISIBLE
            
            binding.etNamaProduk.setText(intent.getStringExtra("NAMA"))
            val harga = intent.getLongExtra("HARGA", 0)
            binding.etHargaProduk.setText(formatRupiah(harga.toString()))
            binding.etStokProduk.setText(intent.getIntExtra("STOK", 0).toString())
            
            val imageSource = intent.getStringExtra("IMAGE_URL") ?: ""
            if (imageSource.isNotEmpty()) {
                binding.ivProduk.let { imageView ->
                    if (imageSource.length > 500) { // Likely Base64
                        val imageByteArray = Base64.decode(imageSource, Base64.DEFAULT)
                        Glide.with(this).load(imageByteArray).centerCrop().into(imageView)
                    } else {
                        Glide.with(this).load(imageSource).centerCrop().into(imageView)
                    }
                }
            }
        }

        loadKategori()

        binding.btnBack.setOnClickListener { finish() }
        binding.cardImage.setOnClickListener { pickImage() }
        binding.btnSimpan.setOnClickListener { simpanProduk() }
        binding.btnHapus.setOnClickListener { hapusProduk() }
    }

    private fun setupPriceFormatter() {
        binding.etHargaProduk.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    binding.etHargaProduk.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(parsed)
                        
                        current = formatted.replace("Rp", "").replace(",00", "").trim()
                        binding.etHargaProduk.setText(current)
                        binding.etHargaProduk.setSelection(current.length)
                    } else {
                        current = ""
                        binding.etHargaProduk.setText("")
                    }

                    binding.etHargaProduk.addTextChangedListener(this)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun formatRupiah(number: String): String {
        val cleanString = number.replace("[Rp,.\\s]".toRegex(), "")
        return if (cleanString.isNotEmpty()) {
            val parsed = cleanString.toDouble()
            val formatted = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(parsed)
            formatted.replace("Rp", "").replace(",00", "").trim()
        } else {
            ""
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun loadKategori() {
        kategoriRepo.getAllKategori { kategoriList ->
            listKategori = kategoriList
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listKategori.map { it.nama }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spKategori.adapter = adapter

            val currentKategoriId = intent.getStringExtra("KATEGORI_ID")
            if (currentKategoriId != null) {
                val index = listKategori.indexOfFirst { it.id == currentKategoriId }
                if (index != -1) {
                    binding.spKategori.setSelection(index)
                }
            }
        }
    }

    private fun simpanProduk() {
        val nama = binding.etNamaProduk.text.toString()
        val hargaStr = binding.etHargaProduk.text.toString().replace(".", "")
        val stokStr = binding.etStokProduk.text.toString()
        val selectedPos = binding.spKategori.selectedItemPosition

        if (nama.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty() || selectedPos == -1) {
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val harga = hargaStr.toLong()
        val stok = stokStr.toInt()
        val kategori = listKategori[selectedPos]

        val produk = Produk(
            id = produkId ?: "",
            nama = nama,
            harga = harga,
            stok = stok,
            kategoriId = kategori.id,
            kategoriNama = kategori.nama,
            imageUrl = intent.getStringExtra("IMAGE_URL") ?: ""
        )

        binding.btnSimpan.isEnabled = false
        binding.btnSimpan.text = "Sedang menyimpan..."

        if (produkId == null) {
            produkRepo.tambahProduk(this, produk, selectedImageUri) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) finish() else {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = "Simpan Produk"
                }
            }
        } else {
            produkRepo.updateProduk(this, produk, selectedImageUri) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) finish() else {
                    binding.btnSimpan.isEnabled = true
                    binding.btnSimpan.text = "Update Produk"
                }
            }
        }
    }

    private fun hapusProduk() {
        produkId?.let { id ->
            produkRepo.hapusProduk(id) { success ->
                if (success) {
                    Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
