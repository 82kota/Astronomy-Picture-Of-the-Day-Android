package com.example.apiproject_assignment_2

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apiproject_assignment_2.databinding.ApodCardBinding

class RecyclerAdapter(private val apodList: List<APIRequest>) :
    RecyclerView.Adapter<ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ApodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apodItem = apodList[position]

        holder.apodDate.text = apodItem.date
        holder.apodName.text = apodItem.title
        //holder.apodExplanation.text = apodItem.explanation

        Glide.with(holder.binding.root).load(apodItem.url).into(holder.apodImage)
    //might not work, not sure if the correct binding is passed
    }

    override fun getItemCount(): Int {
        return apodList.size
    }

    }

class ViewHolder(val binding: ApodCardBinding) : RecyclerView.ViewHolder(binding.root){
    val apodDate : TextView = binding.apodDate
    //val apodExplanation : TextView = binding.apodExplanation
    val apodName : TextView = binding.apodName
    val apodImage : ImageView = binding.apodImage
}