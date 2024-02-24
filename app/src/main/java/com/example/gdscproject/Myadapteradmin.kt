package com.example.gdscproject

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Myadapteradmin(private val context: Context, private val dataList: List<Dataclass>, private val dataKeys: List<String>, private val datauser: List<String>) : RecyclerView.Adapter<Myadapteradmin.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adminrecyclerlist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: Myadapteradmin.MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        Glide.with(context).load(currentItem.imgurl).into(holder.adminrecImage)
        holder.adminrecTitle.text = currentItem.dataname
        holder.adminrecDesc.text = currentItem.datalocation

        holder.adminreccard.setOnClickListener {
            val intent = Intent(context, Detailedactivityadmin::class.java).apply {
                putExtra("image", currentItem.imgurl)
                putExtra("fullname", currentItem.dataname)
                putExtra("phonenumber", currentItem.dataphonenumber)
                putExtra("pincode", currentItem.datapincode)
                putExtra("state", currentItem.datastate)
                putExtra("city", currentItem.datacity)
                putExtra("address", currentItem.datalocation)

                // Pass the data key associated with this item
                if (position < datauser.size) {
                    putExtra("key", dataKeys[position])
                }

                // Pass the user associated with this item
                if (position < datauser.size){
                    putExtra("user", datauser[position])
                    Log.d("child", "data user: ${datauser[position]}")

                }


            }
            context.startActivity(intent)
        }
    }



    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var adminrecImage: ImageView = itemView.findViewById(R.id.adminrecImage)
        var adminrecTitle: TextView = itemView.findViewById(R.id.adminrecTitle)
        var adminrecDesc: TextView = itemView.findViewById(R.id.adminrecDesc)
        var adminreccard: CardView = itemView.findViewById(R.id.adminrecCard)
    }
}

