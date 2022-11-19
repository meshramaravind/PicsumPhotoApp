package com.arvind.picsumphotoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arvind.picsumphotoapp.comparator.PhotosItemComparator
import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.databinding.ItemsPhotosBinding
import com.arvind.picsumphotoapp.view.photos.PhotosFragmentDirections
import javax.inject.Inject

class PhotosImageAdapter @Inject constructor() :
    PagingDataAdapter<ImageListModel, PhotosImageAdapter.ImageAdapterViewHolder>(
        PhotosItemComparator
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageAdapterViewHolder(
            ItemsPhotosBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ImageAdapterViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ImageAdapterViewHolder(private val binding: ItemsPhotosBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageListModel: ImageListModel) = with(binding) {
            photos = imageListModel

            itemView.setOnClickListener {
                val direction =
                    PhotosFragmentDirections.actionPhotosFragmentToPhotosDetailsFragment(
                        imageListModel,imageListModel.author
                    )
                it.findNavController().navigate(direction)
            }

        }


    }
}