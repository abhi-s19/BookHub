package com.abhisek.bookhub.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.abhisek.bookhub.*
import com.abhisek.bookhub.fragment.AppInfoFragment
import com.abhisek.bookhub.fragment.DashboardFragment
import com.abhisek.bookhub.fragment.FavouriteFragment
import com.abhisek.bookhub.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        openDashboard()
        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem != null)
            {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.favourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouriteFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favourite"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.aboutapp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            AppInfoFragment()
                        )
                        .commit()
                    supportActionBar?.title = "About App"
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout,
            DashboardFragment()
        )
            .commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        var frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }
}