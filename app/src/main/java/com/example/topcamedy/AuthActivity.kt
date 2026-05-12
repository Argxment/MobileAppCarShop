package com.example.pastuhovvprojectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.avtorisate)

        val userLog = findViewById<EditText>(R.id.userLogin)
        val userPass = findViewById<EditText>(R.id.userPassword)
        val db = DBHelper(this, null)

        findViewById<TextView>(R.id.linktoReg).setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        findViewById<Button>(R.id.button).setOnClickListener {
            val l = userLog.text.toString().trim()
            val p = userPass.text.toString().trim()

            if (l == "admin" && p == "admin123") {
                SessionManager(this).saveUser(l)
                startActivity(Intent(this, AdminActivity::class.java))
            } else if (db.checkUser(l, p)) {
                SessionManager(this).saveUser(l)
                startActivity(Intent(this, itemsActivity::class.java))
                finish()
            } else Toast.makeText(this, "Ошибка входа", Toast.LENGTH_SHORT).show()
        }
    }
}