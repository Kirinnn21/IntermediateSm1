package com.dutaram.intermediatesm1.Cosview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8 && s.isNotEmpty()) showError() else removeError()
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun showError() {
        this.error = "Requires 8 characters or more!"
    }


    private fun removeError() {
        this.error = null
    }
}