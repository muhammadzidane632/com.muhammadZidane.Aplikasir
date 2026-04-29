package com.muhammadZidane.aplikasir

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muhammadZidane.aplikasir.ui.ListProdukActivity

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.landing_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupMenu()
        setupNavbar()
    }

    private fun setupMenu() {
        val btnTransaksi = findViewById<View>(R.id.btn_transaksi_container)
        val btnProduk = findViewById<View>(R.id.btn_produk_container)
        val btnLaporan = findViewById<View>(R.id.btn_laporan_container)

        btnTransaksi?.setOnClickListener {
            val intent = Intent(this, Transaksi::class.java)
            startActivity(intent)
        }

        btnProduk?.setOnClickListener {
            val intent = Intent(this, ListProdukActivity::class.java)
            startActivity(intent)
        }

        btnLaporan?.setOnClickListener {
            Toast.makeText(this, "Fitur Laporan segera hadir", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupNavbar() {
        val navHome = findViewById<View>(R.id.nav_home)
        val navAnalytics = findViewById<View>(R.id.nav_analytics)
        val navSettings = findViewById<View>(R.id.nav_settings)

        navHome?.setOnClickListener {
            // Sudah di Home
        }
        
        navAnalytics?.setOnClickListener {
            Toast.makeText(this, "Analitik", Toast.LENGTH_SHORT).show()
        }
        
        navSettings?.setOnClickListener {
            Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show()
        }
    }
}
