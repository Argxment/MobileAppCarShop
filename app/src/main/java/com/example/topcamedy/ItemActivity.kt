package com.example.pastuhovvprojectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item)

        val title = intent.getStringExtra("itemTitle") ?: ""
        val price = intent.getStringExtra("itemPrice")?.replace("¥", "")?.toIntOrNull() ?: 0
        val imgRes = intent.getIntExtra("itemImage", -1)

        findViewById<TextView>(R.id.itemTitle).text = title
        findViewById<TextView>(R.id.itemPrice).text = "$price¥"
        findViewById<ImageView>(R.id.imageView).setImageResource(imgRes)
        findViewById<TextView>(R.id.itemText).text = intent.getStringExtra("itemText")

        val db = DBHelper(this, null)
        val login = SessionManager(this).getLogin() ?: ""

        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.buttonAddToCart).setOnClickListener {
            if (login.isEmpty()) return@setOnClickListener Toast.makeText(this, "Войдите", Toast.LENGTH_SHORT).show()
            
            if (db.hasDebt(login)) {
                Toast.makeText(this, "Сначала оплатите долг!", Toast.LENGTH_LONG).show()
            } else {
                CartManager.addItem(Item(0, "", title, "", "", price))
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
        }
        findViewById<Button>(R.id.button3).visibility = android.view.View.GONE
    }
}