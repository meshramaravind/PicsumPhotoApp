package com.arvind.picsumphotoapp.view.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arvind.picsumphotoapp.adapter.PagingLoadStateAdapter
import com.arvind.picsumphotoapp.adapter.PhotosImageAdapter
import com.arvind.picsumphotoapp.databinding.FragmentPhotosBinding
import com.arvind.picsumphotoapp.view.base.BaseFragment
import com.arvind.picsumphotoapp.viewmodel.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PhotosFragment : BaseFragment<FragmentPhotosBinding, MainViewModel>() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var photosImageAdapter: PhotosImageAdapter

    override fun getVM(): MainViewModel = mainViewModel

    override fun bindVM(binding: FragmentPhotosBinding, vm: MainViewModel) {
        with(binding) {
            val rLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rLayoutManager.gapStrategy =
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

            with(photosImageAdapter) {
                rvImages.apply {
                    postponeEnterTransition()
                    viewTreeObserver.addOnPreDrawListener {
                        startPostponedEnterTransition()
                        true
                    }
                    layoutManager = rLayoutManager
                }

                rvImages.adapter = withLoadStateHeaderAndFooter(
                    header = PagingLoadStateAdapter(this),
                    footer = PagingLoadStateAdapter(this)
                )

                swipeRefreshLayout.setOnRefreshListener { refresh() }

                with(viewModel) {
                    launchOnLifecycleScope {
                        imageResponse.collectLatest { submitData(it) }
                    }
                }
                launchOnLifecycleScope {
                    loadStateFlow.collectLatest {
                        swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                    }
                }

                launchOnLifecycleScope {
                    photosImageAdapter.loadStateFlow.collect { loadState ->
                        // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                        val errorState = loadState.source.append as? LoadState.Error
                            ?: loadState.source.prepend as? LoadState.Error
                            ?: loadState.append as? LoadState.Error
                            ?: loadState.prepend as? LoadState.Error
                            ?: loadState.source.refresh as? LoadState.Error
                        errorState?.let {
                            toast("\uD83D\uDE28 ooops ${it.error}")

                        }
                    }
                }

                launchOnLifecycleScope {
                    photosImageAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && photosImageAdapter.itemCount < 1) {
                            rvImages.isVisible = false
                            layoutEmpty.emptyStateView.isVisible = true
                        } else {
                            rvImages.isVisible = true
                            layoutEmpty.emptyStateView.isVisible = false
                        }
                    }
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPhotosBinding.inflate(inflater, container, false)


}