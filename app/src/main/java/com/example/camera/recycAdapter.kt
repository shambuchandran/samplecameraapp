package com.example.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class recycAdapter(private var imagelist:MutableList<String> ):RecyclerView.Adapter<recycAdapter.eachimageholder>() {

    class eachimageholder(itemView:View):RecyclerView.ViewHolder(itemView){
        val imageview=itemView.findViewById<ImageView>(R.id.eachimg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eachimageholder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.eachimage,parent,false)
        return eachimageholder(view)
    }

    override fun getItemCount(): Int {
        return imagelist.size
    }

    override fun onBindViewHolder(holder: eachimageholder, position: Int) {
        val imagePath = imagelist[position]
        Glide.with(holder.imageview.context)
            .load(imagePath)
            .centerCrop()
            .into(holder.imageview)
    }

}