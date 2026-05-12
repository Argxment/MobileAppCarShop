package com.example.pastuhovvprojectmobile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)

        val list = findViewById<RecyclerView>(R.id.cartList)
        list.layoutManager = LinearLayoutManager(this)
        val adapter = CartAdapter(CartManager.items, this)
        list.adapter = adapter

        val db = DBHelper(this, null)
        val user = SessionManager(this).getLogin() ?: ""

        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener { finish() }

        // ОБЫЧНАЯ ПОКУПКА
        findViewById<Button>(R.id.buttonBuy).setOnClickListener {
            val total = CartManager.items.sumOf { it.price }
            val balance = db.getBalance(user)

            if (CartManager.items.isEmpty()) return@setOnClickListener
            
            if (balance >= total) {
                CartManager.items.forEach { db.buy(user, it.title, it.price) }
                db.updateBalance(user, balance - total)
                CartManager.clearCart()
                Toast.makeText(this, "Успешно куплено!", Toast.LENGTH_SHORT).show()
                finish()
            } else Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show()
        }

        // КРЕДИТ
        findViewById<Button>(R.id.buttonCredit).setOnClickListener {
            if (CartManager.items.isEmpty()) return@setOnClickListener
            
            val total = CartManager.items.sumOf { it.price }
            
            // Логика лимита: например, кредит разрешен только если сумма долга не превышает 2 млн
            // Или если это просто демо, поставим проверку "лимита" на 10000 как "доп. взнос"
            if (total > 2000000) { 
                Toast.makeText(this, "Сумма превышает кредитный лимит", Toast.LENGTH_SHORT).show()
            } else {
                CartManager.items.forEach { db.startCredit(user, it.title, it.price) }
                CartManager.clearCart()
                Toast.makeText(this, "Кредит оформлен!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}