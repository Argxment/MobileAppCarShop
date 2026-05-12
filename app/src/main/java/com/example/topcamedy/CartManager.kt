package com.example.pastuhovvprojectmobile

object CartManager {
    val items = mutableListOf<Item>()
    fun addItem(item: Item) { items.clear(); items.add(item) }
    fun clearCart() = items.clear()
}