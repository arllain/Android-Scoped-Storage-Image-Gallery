package com.arllain.scopedstorageimagegallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arllain.scopedstorageimagegallery.databinding.ItemImageBinding
import com.arllain.scopedstorageimagegallery.model.Image

class ImageAdapter(
    val clickAction: (Image) -> Unit
): ListAdapter<Image, ImageAdapter.MyViewHolder>(MyDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageToBind = getItem(position)
        holder.bind(imageToBind)

        holder.image.setOnClickListener {
            clickAction(imageToBind)
        }
    }

    inner class MyViewHolder(private val binding: ItemImageBinding ): RecyclerView.ViewHolder(binding.root){
        fun bind(image: Image) {
            binding.apply {
                ivImage.setImageURI(image.uri)
                imageName.text = image.name
            }
        }
        val image = binding.ivImage
    }

    private class MyDiff: DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Image, newItem: Image) = oldItem.name == newItem.name
    }


}