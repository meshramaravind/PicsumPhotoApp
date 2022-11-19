package com.arvind.picsumphotoapp.viewmodel.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.data.repository.ImageListRepositoryImpl
import com.arvind.picsumphotoapp.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ImageListRepositoryImpl
) : BaseViewModel() {

    private val TAG = "MainViewModel"
    private lateinit var _imageResponse: Flow<PagingData<ImageListModel>>
    val imageResponse: Flow<PagingData<ImageListModel>>
        get() = _imageResponse

    init {
        fetchImages()
    }

    private fun fetchImages() {
        launchPagingAsync({
            repository.getImages().cachedIn(viewModelScope)
        }, {
            _imageResponse = it
        })
    }
}