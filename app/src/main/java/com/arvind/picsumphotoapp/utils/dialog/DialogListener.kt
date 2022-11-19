package com.arvind.picsumphotoapp.utils.dialog

interface DialogListener {
    fun onYesClicked(obj: Any?)
    fun onNoClicked(error: String?)
}