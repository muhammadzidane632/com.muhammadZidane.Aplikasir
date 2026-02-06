package com.muhammadZidane.aplikasir

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.muhammadZidane.aplikasir.model.Kategori

class TambahKategoriDialog(
    private val listener: OnKategoriListener,
    private val listKategoriSaatIni: List<Kategori>
) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_tambah_kategori, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val containerInput = view.findViewById<LinearLayout>(R.id.containerInput)
        val etInput = view.findViewById<EditText>(R.id.etInputKategori)
        val gridKategori = view.findViewById<GridLayout>(R.id.gridKategori)

        for (kategori in listKategoriSaatIni) {
            val btnKategori = createButtonVisual(kategori.nama, false)
            gridKategori.addView(btnKategori)
        }

        val btnTambahInternal = createButtonVisual("+ Tambah", true)

        btnTambahInternal.setOnClickListener {
            gridKategori.removeView(btnTambahInternal)
            containerInput.visibility = View.VISIBLE
            etInput.requestFocus()
        }
        gridKategori.addView(btnTambahInternal)


        btnClose.setOnClickListener { dismiss() }

        btnSimpan.setOnClickListener {
            if (containerInput.visibility == View.VISIBLE) {
                val kategoriBaru = etInput.text.toString().trim()
                if (kategoriBaru.isNotEmpty()) {
                    val sudahAda = listKategoriSaatIni.any { it.nama.equals(kategoriBaru, ignoreCase = true) }
                    if (sudahAda) {
                        Toast.makeText(context, "Kategori sudah ada", Toast.LENGTH_SHORT).show()
                    } else {
                        listener.onKategoriSimpan(kategoriBaru)
                        dismiss()
                    }
                } else {
                    Toast.makeText(context, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            } else {
                dismiss()
            }
        }
    }

    private fun createButtonVisual(teks: String, isTambah: Boolean): TextView {
        val textView = TextView(context)
        textView.text = teks
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER
        textView.setBackgroundResource(R.drawable.bg_cat_unselected)

        val params = GridLayout.LayoutParams()
        params.width = GridLayout.LayoutParams.WRAP_CONTENT
        params.height = (40 * resources.displayMetrics.density).toInt()
        params.setMargins(0, 0, (8 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt())


        textView.layoutParams = params
        textView.setPadding(
            (24 * resources.displayMetrics.density).toInt(), 0,
            (24 * resources.displayMetrics.density).toInt(), 0
        )

        return textView
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout((resources.displayMetrics.widthPixels * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}