package com.swenson.blechat.extention

import android.graphics.Typeface
import android.text.InputFilter
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun MaterialTextView.setTextWithUnderLine(text: String) {
    val content = SpannableString(text)
    content.setSpan(UnderlineSpan(), 0, content.length, 0)
    this.text = content
}

fun EditText.setAsTextField(maxLength :Int?){
    val filterArray = arrayOfNulls<InputFilter>(1)
    filterArray[0] = InputFilter.LengthFilter(maxLength ?: 100)
    this.filters = filterArray
}


fun EditText.setAsTextArea(maxLength :Int?){
    val filterArray = arrayOfNulls<InputFilter>(1)
    filterArray[0] = InputFilter.LengthFilter(maxLength ?: 200)
    this.filters = filterArray
}
fun SearchView.getQueryTextChangeStateFlow(): StateFlow<String> {
    val searchQuery = MutableStateFlow("")
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            searchQuery.value = newText!!
                    return true
        }
    })
    return searchQuery
}

fun TextView.setColor(color: Int) {
    this.setTextColor(color)
}

fun TextView.setFont(font: Typeface?) {
    this.typeface = font
}
fun TextView.setStyle(color: Int,font: Typeface?){
    this.setColor(color)
    this.setFont(font)
}

