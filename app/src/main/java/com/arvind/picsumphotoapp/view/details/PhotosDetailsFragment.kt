package com.arvind.picsumphotoapp.view.details

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.arvind.picsumphotoapp.BuildConfig
import com.arvind.picsumphotoapp.R
import com.arvind.picsumphotoapp.data.model.ImageListModel
import com.arvind.picsumphotoapp.databinding.FragmentPhotosDetailsBinding
import com.arvind.picsumphotoapp.utils.Constants.TAG
import com.arvind.picsumphotoapp.utils.FileUtils
import com.arvind.picsumphotoapp.utils.dialog.DialogListener
import com.arvind.picsumphotoapp.utils.ext.ShowAlert
import com.arvind.picsumphotoapp.view.base.BaseFragment
import com.arvind.picsumphotoapp.viewmodel.details.PhotosDetailsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_network_state.*
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class PhotosDetailsFragment : BaseFragment<FragmentPhotosDetailsBinding, PhotosDetailsViewModel>() {
    private val photosDetailsViewModel: PhotosDetailsViewModel by viewModels()
    private val args: PhotosDetailsFragmentArgs by navArgs()
    private lateinit var imageListModel: ImageListModel
    override fun getVM(): PhotosDetailsViewModel = photosDetailsViewModel
    private val REQUEST_CODE_ASK_PERMISSIONS = 100

    override fun bindVM(binding: FragmentPhotosDetailsBinding, vm: PhotosDetailsViewModel) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageListModel = args.imageListModel
        initView()
    }

    private fun initView() = with(binding) {
        val circularProgressDrawable = CircularProgressDrawable(requireActivity())
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 80f
        circularProgressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                R.color.purple_200,
                BlendModeCompat.SRC_ATOP
            )
        circularProgressDrawable.start()
        with(binding) {
            Glide.with(requireActivity())
                .load(imageListModel.download_url)
                .placeholder(circularProgressDrawable)
                .into(imageFullScreen)

            with(photosDetailsViewModel) {
                imageUri.observe(requireActivity()) { uri ->
                    if (!uri.path.isNullOrEmpty()) {
                        hideProgressBar()
                        downloadedFileUri = uri
                        if (!shareImage) {
                            val snackBar =
                                Snackbar.make(
                                    binding.rootLayout,
                                    getString(R.string.image_download_success),
                                    Snackbar.LENGTH_LONG
                                )
                            snackBar.show()
                            openImage()
                        } else {
                            shareImage()
                        }
                    }
                }

                showProgressBar.observe(requireActivity()) { show ->
                    if (show) showProgressBar()
                    else hideProgressBar()
                }

                errorToast.observe(requireActivity()) { value ->
                    if (value.isNotEmpty()) {
                        hideProgressBar()
                        toast(value)
                    }
                    hideErrorToast()
                }
            }
        }
    }

    private fun openImage() {
        viewModel.downloadedFileUri?.path?.let {
            var file = File(it)
            val finalUri: Uri? = FileUtils().copyFileToDownloads(requireContext(), file)
            finalUri?.path?.let { path ->
                file = File(path)
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    // JPG file
                    intent.setDataAndType(finalUri, "image/jpeg")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    this.startActivity(intent)
                } catch (e: Exception) {
                    Timber.tag(TAG).d(e.toString())
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_share_image -> {
                shareImage()
            }
            R.id.action_download_image -> {
                downloadImage()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun shareImage() {
        viewModel.shareImage = true
        if (viewModel.downloadedFileUri == null) {
            checkPermission()
        } else {
            handleShareImage()
        }
    }

    private fun handleShareImage() {
        var authority = "com.arvind.picsumphotoapp.fileprovider"
        viewModel.downloadedFileUri?.path?.let {
            val file = File(it)
            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
            viewModel.shareImage = false
        }
    }

    private fun downloadImage() {
        ShowAlert().alertDialog(requireContext(), "", getString(R.string.download_image),
            getString(R.string.yes), getString(R.string.no), object : DialogListener {
                override fun onYesClicked(obj: Any?) {
                    checkPermission()
                }

                override fun onNoClicked(error: String?) {}
            })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            viewModel.downloadImage(imageListModel)
        } else {
            val hasWriteStoragePermission =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    ShowAlert().alertDialog(
                        requireContext(),
                        "",
                        getString(R.string.rationale),
                        getString(R.string.yes),
                        getString(R.string.no),
                        object : DialogListener {
                            override fun onYesClicked(obj: Any?) {
                                requestPermission(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    REQUEST_CODE_ASK_PERMISSIONS
                                )
                            }

                            override fun onNoClicked(error: String?) {
                            }

                        }
                    )
                } else {
                    //Request permission
                    requestPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        REQUEST_CODE_ASK_PERMISSIONS
                    )
                }
            } else {
                //Permission granted
                viewModel.downloadImage(imageListModel)
            }
        }
    }

    private fun requestPermission(permissionName: String, permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permissionName),
            permissionRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.downloadImage(imageListModel)
            } else {
                toast(getString(R.string.permission_denied))

            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPhotosDetailsBinding.inflate(inflater, container, false)


}