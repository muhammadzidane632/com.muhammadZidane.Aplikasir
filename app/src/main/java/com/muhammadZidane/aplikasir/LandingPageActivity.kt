package com.muhammadZidane.aplikasir

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        
        setupNavbar()
    }
    
    private fun setupNavbar() {
        val navHome = findViewById<View>(R.id.nav_home)
        val navAnalytics = findViewById<View>(R.id.nav_analytics)
        val navSettings = findViewById<View>(R.id.nav_settings)

        navHome?.setOnClickListener {
            Toast.makeText(this, "Beranda", Toast.LENGTH_SHORT).show()
        }
        
        navAnalytics?.setOnClickListener {
            Toast.makeText(this, "Analitik", Toast.LENGTH_SHORT).show()
        }
        
        navSettings?.setOnClickListener {
            Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show()
        }
    }
}