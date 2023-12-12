package com.example.playlist_maker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val EDIT_TEXT_VALUE_KEY = "editTextValue"
    }

    private var editTextValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.image)
        backButton.setOnClickListener {
            finish()
        }

        val searchBar = findViewById<EditText>(R.id.search_bar)
        val resetButton = findViewById<ImageView>(R.id.reset_button)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editTextValue = s.toString()
                resetButton.isVisible = s.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        resetButton.setOnClickListener {
            searchBar.text.clear()
            resetButton.isVisible = false
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
        }

        editTextValue = savedInstanceState?.getString(EDIT_TEXT_VALUE_KEY, "") ?: ""
        searchBar.setText(editTextValue)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE_KEY, editTextValue)
    }
}

