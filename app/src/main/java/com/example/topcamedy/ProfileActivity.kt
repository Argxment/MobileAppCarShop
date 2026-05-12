package com.example.pastuhovvprojectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val session = SessionManager(this); val login = session.getLogin() ?: return finish()
        val db = DBHelper(this, null)

        findViewById<TextView>(R.id.profileLogin).text = login
        val balText = findViewById<TextView>(R.id.profileBalance)
        val historyContainer = findViewById<LinearLayout>(R.id.historyListLayout)
        val cardOrder = findViewById<androidx.cardview.widget.CardView>(R.id.cardOrder)
        val textPending = findViewById<TextView>(R.id.textPendingCar)

        fun refresh() {
            val balance = db.getBalance(login)
            balText.text = "Баланс: $balance¥"
            historyContainer.removeAllViews()

            val cart = CartManager.items
            if (cart.isNotEmpty()) {
                cardOrder.visibility = android.view.View.VISIBLE
                textPending.text = "${cart[0].title} - ${cart[0].price}¥"
            } else cardOrder.visibility = android.view.View.GONE

            db.getDebts(login).forEach { (id, title, rem) ->
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                val info = TextView(this).apply { text = "ДОЛГ: $title ($rem¥)"; layoutParams = LinearLayout.LayoutParams(0, -2, 1f); setTextColor(-0x10000) }
                val btnPay = Button(this).apply {
                    text = "Платить 10к"
                    setOnClickListener {
                        val curBal = db.getBalance(login)
                        if (curBal >= 10000) { db.payDebt(id, 10000); db.updateBalance(login, curBal - 10000); refresh() }
                        else Toast.makeText(context, "Мало денег", Toast.LENGTH_SHORT).show()
                    }
                }
                row.addView(info); row.addView(btnPay); historyContainer.addView(row)
            }

            db.getHistory(login).forEach { title ->
                historyContainer.addView(TextView(this).apply { text = "✓ Куплено: $title"; setPadding(0, 10, 0, 10) })
            }
        }

        findViewById<Button>(R.id.btnPayNow).setOnClickListener {
            val car = CartManager.items[0]; val curBal = db.getBalance(login)
            if (curBal >= car.price) {
                db.buy(login, car.title, car.price)
                db.updateBalance(login, curBal - car.price); CartManager.clearCart(); refresh()
            } else Toast.makeText(this, "Мало денег", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnPayCredit).setOnClickListener {
            val car = CartManager.items[0]
            db.startCredit(login, car.title, car.price)
            CartManager.clearCart(); refresh()
        }

        findViewById<Button>(R.id.buttonTopUp).setOnClickListener {
            val amt = findViewById<EditText>(R.id.editTopUp).text.toString().toIntOrNull() ?: 0
            db.updateBalance(login, db.getBalance(login) + amt); refresh()
        }

        findViewById<Button>(R.id.buttonLogout).setOnClickListener { session.logout(); finish() }
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener { finish() }
        refresh()
    }
}