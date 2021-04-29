package com.app.tvapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.app.tvapp.R
import com.app.tvapp.databinding.ActivityMainBinding
import com.app.tvapp.others.onRightDrawableClicked
import com.app.tvapp.ui.frags.MainFragment
import com.app.tvapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        setSupportActionBar(binding.toolbar)
        setupSearch()

    }


    private fun setupSearch() {
        binding.apply {
            searchView.setOnFocusChangeListener { _, b ->
                sugLayout.isVisible = b
                supportActionBar?.setDisplayShowHomeEnabled(b)
                supportActionBar?.setDisplayHomeAsUpEnabled(b)
            }
            searchView.onRightDrawableClicked {
                search(searchView.text.toString())
            }
            searchView.setOnKeyListener { _, i, event ->
                if(event.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    searchView.clearFocus()
                    findNavController(R.id.nav_graph).navigate(R.id.action_mainFragment_to_searchFragment)
                    true
                }else {
                    false
                }
            }
        }
    }
    private fun search(text: String){
        if(text.isEmpty()) return

        hideKeyboard()
        
        val controller = findNavController(R.id.nav_graph)
        if(controller.currentDestination?.id != R.id.searchFragment){
            controller.navigate(R.id.action_mainFragment_to_searchFragment)
        }
        binding.searchView.clearFocus()

        viewModel.search(text)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(binding.searchView.isFocused){
            binding.searchView.clearFocus()
            hideKeyboard()
            return
        }
        super.onBackPressed()
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.searchView.applicationWindowToken, 0)
    }

}