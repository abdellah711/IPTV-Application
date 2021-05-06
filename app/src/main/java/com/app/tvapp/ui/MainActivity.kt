package com.app.tvapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tvapp.R
import com.app.tvapp.adapters.SuggestionAdapter
import com.app.tvapp.databinding.ActivityMainBinding
import com.app.tvapp.others.onRightDrawableClicked
import com.app.tvapp.ui.frags.MainFragment
import com.app.tvapp.viewmodels.MainViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding


    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        setSupportActionBar(binding.toolbar)
        setupSearch()
        setupSuggetions()
    }

    private fun setupSuggetions() {

        val sugAdapter = SuggestionAdapter(viewModel){
            binding.searchView.setText(it)
            search(it)
        }

        binding.sugRecycler.apply {
            adapter = sugAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity,VERTICAL))
        }
        viewModel.suggestions.observe(this){
            sugAdapter.suggestions = it
            binding.noSug.isVisible = it.isEmpty()
        }
    }


    private fun setupSearch() {
        binding.apply {
            searchView.setOnFocusChangeListener { _, b ->
                sugLayout.isVisible = b
                supportActionBar?.setDisplayShowHomeEnabled(b)
                supportActionBar?.setDisplayHomeAsUpEnabled(b)
            }
            searchView.onRightDrawableClicked {
                search(searchView.text.toString().trim())
            }

            searchView.setOnEditorActionListener { _, i, _ ->
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    search(searchView.text.toString().trim())
                    true
                }else
                    false
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
            hideKeyboard()
            binding.searchView.clearFocus()
            return
        }
        if(findNavController(R.id.nav_graph).currentDestination?.id==R.id.searchFragment){
            binding.searchView.setText("")
        }
        super.onBackPressed()
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.searchView.applicationWindowToken, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

}