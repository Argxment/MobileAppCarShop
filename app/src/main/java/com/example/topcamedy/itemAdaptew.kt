package com.example.pastuhovvprojectmobile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class itemAdaptew(var items: List<Item>, var context: Context) : RecyclerView.Adapter<itemAdaptew.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageView)
        val title = view.findViewById<TextView>(R.id.item_title)
        val desc = view.findViewById<TextView>(R.id.item_desc)
        val price = view.findViewById<TextView>(R.id.item_price)
        val btn = view.findViewById<Button>(R.id.button2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.desc.text = item.desc
        holder.price.text = "${item.price}¥"
        holder.image.setImageResource(context.resources.getIdentifier(item.image, "drawable", context.packageName))

        holder.btn.setOnClickListener {
            val intent = Intent(context, ItemActivity::class.java).apply {
                putExtra("itemTitle", item.title)
                putExtra("itemText", item.text)
                putExtra("itemPrice", item.price.toString())
                putExtra("itemImage", context.resources.getIdentifier(item.image, "drawable", context.packageName))
            }
            context.startActivity(intent)
        }
    }
}