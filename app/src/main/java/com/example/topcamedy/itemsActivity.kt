package com.example.pastuhovvprojectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class itemsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_list)

        val db = DBHelper(this, null)
        val session = SessionManager(this)
        
        val cars = db.getAllCars().map { it.first }

        val itemList: RecyclerView = findViewById(R.id.itemList)
        itemList.layoutManager = LinearLayoutManager(this)
        itemList.adapter = itemAdaptew(cars, this)

        findViewById<Button>(R.id.buttonProfile).setOnClickListener {
            startActivity(Intent(this, if (session.getLogin() != null) ProfileActivity::class.java else AuthActivity::class.java))
        }
    }
}