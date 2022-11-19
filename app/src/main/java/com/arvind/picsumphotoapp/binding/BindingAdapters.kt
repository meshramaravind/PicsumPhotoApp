package com.arvind.picsumphotoapp.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.arvind.picsumphotoapp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import javax.inject.Inject

class BindingAdapters @Inject constructor() {
    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun setImageURL(imageView: ImageView, url: String?) {
            if (!url.isNullOrEmpty()) {
                Glide.with(imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .downsample(DownsampleStrategy.AT_MOST)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView)
            }
        }

    }
}