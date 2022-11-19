package com.arvind.picsumphotoapp.view.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.arvind.picsumphotoapp.utils.ext.observe
import com.arvind.picsumphotoapp.viewmodel.base.BaseViewModel

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    lateinit var viewModel: VM
    abstract fun getVM(): VM
    abstract fun bindVM(binding: VB, vm: VM)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        viewModel = getVM()
        bindVM(binding, viewModel)
        with(viewModel) {
            observe(errorMessage) { msg ->
                Toast.makeText(
                    requireActivity(),
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.root
    }


    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun applicationContext(): Context = requireActivity().applicationContext
    fun launchOnLifecycleScope(execute: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            execute()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}