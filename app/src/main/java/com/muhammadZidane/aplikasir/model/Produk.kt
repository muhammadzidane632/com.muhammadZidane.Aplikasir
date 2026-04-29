package com.muhammadZidane.aplikasir.model

data class Produk(
    var id: String = "",
    var nama: String = "",
    var harga: Long = 0,
    var stok: Int = 0,
    var kategoriId: String = "",
    var kategoriNama: String = "",
    var imageUrl: String = "",
    var timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", 0, 0, "", "", "", 0L)
}
