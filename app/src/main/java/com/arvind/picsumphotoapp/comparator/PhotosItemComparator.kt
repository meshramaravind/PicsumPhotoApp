package com.arvind.picsumphotoapp.comparator

import androidx.recyclerview.widget.DiffUtil
import com.arvind.picsumphotoapp.data.model.ImageListModel


object PhotosItemComparator : DiffUtil.ItemCallback<ImageListModel>() {
    override fun areItemsTheSame(oldItem: ImageListModel, newItem: ImageListModel) =
        oldItem.url == newItem.url

    override fun areContentsTheSame(oldItem: ImageListModel, newItem: ImageListModel) =
        oldItem == newItem
}
