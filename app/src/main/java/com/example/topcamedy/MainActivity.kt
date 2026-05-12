package com.example.pastuhovvprojectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val log = findViewById<EditText>(R.id.userLogin); val mail = findViewById<EditText>(R.id.userMail)
        val pass = findViewById<EditText>(R.id.userPassword); val db = DBHelper(this, null)

        findViewById<TextView>(R.id.linktoReg).setOnClickListener { startActivity(Intent(this, AuthActivity::class.java)) }

        findViewById<Button>(R.id.button).setOnClickListener {
            val l = log.text.toString().trim(); val m = mail.text.toString().trim(); val p = pass.text.toString().trim()
            if (l.isEmpty() || m.isEmpty() || p.isEmpty()) return@setOnClickListener Toast.makeText(this, "Заполните всё", Toast.LENGTH_SHORT).show()

            val err = db.isTaken(l, p)
            if (err != null) Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
            else {
                db.addUser(User(l, p, m))
                SessionManager(this).saveUser(l)
                startActivity(Intent(this, itemsActivity::class.java))
                finish()
            }
        }
    }
}