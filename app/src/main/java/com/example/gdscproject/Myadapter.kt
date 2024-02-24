package com.example.gdscproject

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Myadapter(private val context: Context, private val dataList: List<Dataclass>, private val dataKeys: List<String>, private val datauser: List<String>) : RecyclerView.Adapter<Myadapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycleritemview, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        Glide.with(context).load(currentItem.imgurl).into(holder.recImage)
        holder.recTitle.text = currentItem.dataname
        holder.recDesc.text = currentItem.datalocation

        holder.reccard.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java).apply {
                putExtra("image", currentItem.imgurl)
                putExtra("fullname", currentItem.dataname)
                putExtra("phonenumber", currentItem.dataphonenumber)
                putExtra("pincode", currentItem.datapincode)
                putExtra("state", currentItem.datastate)
                putExtra("city", currentItem.datacity)
                putExtra("address", currentItem.datalocation)

                // Pass the data key associated with this item
                if (position < dataKeys.size) {
                    putExtra("key", dataKeys[position])
                }

                // Pass the user associated with this item
                if (position < datauser.size) {
                    putExtra("user", datauser[position])
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recImage: ImageView = itemView.findViewById(R.id.recImage)
        var recTitle: TextView = itemView.findViewById(R.id.recTitle)
        var recDesc: TextView = itemView.findViewById(R.id.recDesc)
        var reccard: CardView = itemView.findViewById(R.id.recCard)
    }
}

