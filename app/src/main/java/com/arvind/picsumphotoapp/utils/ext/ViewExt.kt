package com.arvind.picsumphotoapp.utils.ext

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.arvind.picsumphotoapp.R
import com.arvind.picsumphotoapp.utils.dialog.DialogListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

class ShowAlert {
    fun alertDialog(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        negativeBtnText: String? = "",
        listener: DialogListener
    ) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnText) { dialog, _ ->
                dialog.dismiss()
                listener.onYesClicked("yes")
            }
            .setNegativeButton(negativeBtnText) { dialog, _ ->
                dialog.dismiss()
                listener.onNoClicked(error = null)
            }

        builder.create().show()
    }

    fun okAlertDialog(
        context: Context,
        title: String,
        message: String,
        neutralBtnText: String
    ) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(title)
            .setMessage(message)
            .setNeutralButton(neutralBtnText) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}


fun Context.showAlertDialog(
    title: String,
    body: String,
    callback: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(body)

        .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
            // Respond to negative button press
        }
        .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
            callback()
        }
        .show()
}
