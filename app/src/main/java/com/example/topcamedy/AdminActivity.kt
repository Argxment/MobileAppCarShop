package com.example.pastuhovvprojectmobile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_layout)

        val listLayout = findViewById<LinearLayout>(R.id.itemsListLayout)
        val db = DBHelper(this, null)

        fun refresh() {
            listLayout.removeAllViews()
            db.getAllCars().forEach { (item, qty) ->
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                val info = TextView(this).apply { text = "${item.title} ($qty шт)"; layoutParams = LinearLayout.LayoutParams(0, -2, 1f); setTextColor(-0xcccccd) }
                val btnDel = Button(this).apply { text = "X"; setOnClickListener { db.deleteCar(item.title); refresh() } }
                val btnPlus = Button(this).apply { text = "+"; setOnClickListener { db.updateQty(item.title, qty + 1); refresh() } }
                row.addView(info); row.addView(btnPlus); row.addView(btnDel); listLayout.addView(row)
            }
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val t = findViewById<EditText>(R.id.newTitle).text.toString()
            val p = findViewById<EditText>(R.id.newPrice).text.toString().toIntOrNull() ?: 0
            val i = findViewById<EditText>(R.id.newImage).text.toString().ifEmpty { "_1" }
            if (t.isNotEmpty()) { db.addOrUpdateCar(t, i, p, 1); refresh() }
        }

        findViewById<Button>(R.id.buttonAdminBack).setOnClickListener { finish() }
        refresh()
    }
}