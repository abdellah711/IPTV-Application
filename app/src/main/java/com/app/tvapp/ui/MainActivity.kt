package com.app.tvapp.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.app.tvapp.R
import com.app.tvapp.adapters.ChannelsAdapter
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
        setupFrags()
    }

    private fun setupFrags() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,MainFragment())
            .commit()
    }

    private fun setupSearch() {
        binding.apply {
            searchView.setOnFocusChangeListener { _, b ->
                sugLayout.isVisible = b
                supportActionBar?.setDisplayShowHomeEnabled(b)
                supportActionBar?.setDisplayHomeAsUpEnabled(b)
                if (b) {
                    searchView.compoundDrawables[2].setVisible(true,false)
                    return@setOnFocusChangeListener
                }
                searchView.compoundDrawables[2].setVisible(false,false)
            }
            searchView.onRightDrawableClicked {
                Log.e("Test","click")
            }
//            searchView.setOnEditorActionListener { _, i, keyEvent ->
//                if(keyEvent.action == KeyEvent.ACTION_DOWN)
//            }
        }
    }

    override fun onNavigateUp(): Boolean {
        Log.e("test","nav up")
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        if(binding.searchView.isFocused){
            binding.searchView.clearFocus()
            return
        }
        super.onBackPressed()
    }

}