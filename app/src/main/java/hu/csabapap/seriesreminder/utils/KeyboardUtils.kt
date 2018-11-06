package hu.csabapap.seriesreminder.utils

import android.content.Context
import android.os.ResultReceiver
import android.view.View
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // the public methods don't seem to work for me, so… reflection.
    try {
        val showSoftInputUnchecked = InputMethodManager::class.java.getMethod(
                "showSoftInputUnchecked", Int::class.javaPrimitiveType, ResultReceiver::class.java)
        showSoftInputUnchecked.isAccessible = true
        showSoftInputUnchecked.invoke(imm, 0, null)
    } catch (e: Exception) {
        // ho hum
    }

}

