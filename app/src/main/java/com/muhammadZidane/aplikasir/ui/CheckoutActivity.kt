package com.muhammadZidane.aplikasir.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.muhammadZidane.aplikasir.R
import com.muhammadZidane.aplikasir.databinding.ActivityCheckoutBinding
import java.text.NumberFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var grandTotal: Long = 0
    private val cartItems = mutableListOf<Triple<String, Long, Int>>() // Name, Price, Qty
    private var selectedPaymentMethod: String? = null
    private var lastSelectedCard: MaterialCardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val names = intent.getStringArrayExtra("NAMES") ?: emptyArray()
        val prices = intent.getLongArrayExtra("PRICES") ?: longArrayOf()
        val qtys = intent.getIntArrayExtra("QTYS") ?: intArrayOf()

        for (i in names.indices) {
            cartItems.add(Triple(names[i], prices[i], qtys[i]))
            grandTotal += prices[i] * qtys[i]
        }

        setupUI()
        setupPaymentSelection()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        binding.tvTotalAmount.text = format.format(grandTotal).replace(",00", "")

        binding.llOrderItems.removeAllViews()
        for (item in cartItems) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_checkout, binding.llOrderItems, false)
            itemView.findViewById<TextView>(R.id.tvItemName).text = item.first
            itemView.findViewById<TextView>(R.id.tvItemQty).text = String.format(Locale.getDefault(), "%d x %s", item.third, format.format(item.second).replace(",00", ""))
            itemView.findViewById<TextView>(R.id.tvItemSubtotal).text = format.format(item.second * item.third).replace(",00", "")
            binding.llOrderItems.addView(itemView)
        }

        binding.btnConfirmPayment.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showReceipt(selectedPaymentMethod!!)
        }
    }

    private fun setupPaymentSelection() {
        val paymentOptions = mapOf(
            binding.cvCash to "Tunai (Cash)",
            binding.cvQris to "QRIS",
            binding.cvOvo to "OVO",
            binding.cvShopee to "ShopeePay",
            binding.cvBca to "BCA Transfer"
        )

        paymentOptions.forEach { (card, methodName) ->
            card.setOnClickListener {
                selectCard(card, methodName)
            }
        }
    }

    private fun selectCard(card: MaterialCardView, methodName: String) {
        // Reset last selected card
        lastSelectedCard?.let {
            it.setCardBackgroundColor(Color.WHITE)
            it.strokeColor = Color.parseColor("#DDDDDD")
            it.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }

        // Highlight new selected card
        card.setCardBackgroundColor(Color.parseColor("#F0FFEB")) // Light green tint
        card.strokeColor = Color.parseColor("#BEFF6C") // Your theme green
        card.strokeWidth = (3 * resources.displayMetrics.density).toInt()

        selectedPaymentMethod = methodName
        lastSelectedCard = card
    }

    private fun showReceipt(paymentMethod: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_struk, null)
        val llItems = view.findViewById<LinearLayout>(R.id.llStrukItems)
        val tvTotal = view.findViewById<TextView>(R.id.tvStrukTotal)
        val btnSelesai = view.findViewById<Button>(R.id.btnSelesaiStruk)
        
        val tvMethod = TextView(this)
        tvMethod.text = String.format(Locale.getDefault(), "Metode: %s", paymentMethod)
        tvMethod.setPadding(0, 0, 0, 16)
        tvMethod.textSize = 12f
        tvMethod.setTextColor(Color.GRAY)
        llItems.addView(tvMethod)

        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))

        for (item in cartItems) {
            val itemPrice = item.second * item.third
            val itemRow = TextView(this)
            itemRow.text = String.format(Locale.getDefault(), "%s x%d \n%s", item.first, item.third, format.format(itemPrice).replace(",00", ""))
            itemRow.setPadding(0, 8, 0, 8)
            itemRow.setTextColor(Color.BLACK)
            llItems.addView(itemRow)
        }

        tvTotal.text = format.format(grandTotal).replace(",00", "")

        val receiptDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        receiptDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSelesai.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        receiptDialog.show()
    }
}
