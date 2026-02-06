package com.muhammadZidane.aplikasir.model

data class Kategori(
    var id: String = "",
    var nama: String = "",
    var timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", 0L)
}
