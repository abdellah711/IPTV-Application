package com.app.tvapp.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.navigation.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tvapp.R
import com.app.tvapp.adapters.SuggestionAdapter
import com.app.tvapp.databinding.ActivityMainBinding
import com.app.tvapp.others.onRightDrawableClicked
import com.app.tvapp.viewmodels.MainViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    private val viewModel: MainViewModel by viewModels()

    private val navController: NavController by lazy {
        findNavController(R.id.nav_graph)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_TVApp)
        setContentView(binding.root)

        setupToolbar()
        setupSearch()
        setupSuggetions()
        setupNetworkErrorDialog()
    }

    private fun setupNetworkErrorDialog() {
        viewModel.networkError.observe(this){
            if(it){
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setPositiveButton("OK"){ _,_ ->
                        finish()
                    }
                    .setMessage(R.string.check_internet_msg)
                    .setTitle(R.string.network_error)
                    .show()
            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            setSupportActionBar(toolbar)
            toggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout, toolbar, 0, 0
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener(this@MainActivity)
        }
    }

    private fun setupSuggetions() {

        val sugAdapter = SuggestionAdapter(viewModel) {
            binding.searchView.setText(it)
            search(it)
        }

        binding.sugRecycler.apply {
            adapter = sugAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, VERTICAL))
        }
        viewModel.suggestions.observe(this) {
            sugAdapter.suggestions = it
            binding.noSug.isVisible = it.isEmpty()
        }
    }


    private fun setupSearch() {
        binding.apply {
            searchView.setOnFocusChangeListener { _, b ->
                sugLayout.isVisible = b
                toggle.isDrawerIndicatorEnabled = !b
                supportActionBar?.apply {
                    setDisplayShowTitleEnabled(!b)
                    setDisplayShowHomeEnabled(b)
                    setDisplayHomeAsUpEnabled(b)
                }
                toggle.setToolbarNavigationClickListener {
                    if (b) {
                        onBackPressed()
                    }
                }
                toggle.syncState()
            }
            searchView.onRightDrawableClicked {
                search(searchView.text.toString().trim())
            }

            searchView.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    search(searchView.text.toString().trim())
                    true
                } else
                    false
            }
        }
    }

    private fun search(text: String) {
        if (text.isEmpty()) return

        hideKeyboard()

        if (navController.currentDestination?.id != R.id.searchFragment) {
            navController.navigate(R.id.action_mainFragment_to_searchFragment)
        }
        binding.searchView.clearFocus()

        viewModel.search(text)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.searchView.isFocused) {
            hideKeyboard()
            binding.searchView.clearFocus()
            return
        }
        if(binding.drawerLayout.isOpen){
            binding.drawerLayout.close()
            return
        }
        if (navController.currentDestination?.id == R.id.searchFragment) {
            binding.searchView.setText("")
        }
        super.onBackPressed()
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.searchView.applicationWindowToken, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        navController.navigate(
                when (item.itemId) {
                    R.id.nav_settings -> R.id.action_to_settingsFragment
                    R.id.nav_contact -> R.id.action_to_contactFragment
                    else -> R.id.action_to_mainFragment
                }
            )

        binding.drawerLayout.closeDrawer(GravityCompat.START,true)
        return true
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id
            binding.apply {
                toolbar.isVisible = id != R.id.splashFragment
                val isMainOrSearch =  id in listOf(R.id.searchFragment, R.id.mainFragment)
                searchView.isVisible = isMainOrSearch
                titleTv.isVisible = !isMainOrSearch
                titleTv.text = getString(when(id){
                    R.id.settingsFragment ->R.string.settings
                    R.id.contactFragment ->R.string.contact_us
                    else -> R.string.app_name
                })
                navView.menu.findItem(
                    when (id) {
                        R.id.settingsFragment -> R.id.nav_settings
                        R.id.contactFragment -> R.id.nav_contact
                        else -> R.id.nav_home
                    }
                ).isChecked = true
            }

        }
    }

    override fun onStop() {
        super.onStop()
        binding.drawerLayout.close()
    }
}