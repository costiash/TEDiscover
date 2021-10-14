package com.example.tediscover

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tediscover.databinding.ActivityTedBinding
import com.example.tediscover.ui.models.TalkItem
import com.example.tediscover.viewmodels.FirebaseViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class TedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTedBinding
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var navController: NavController
    private val sharedViewModel: FirebaseViewModel by viewModels()
    var userName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)

        val navView: BottomNavigationView = binding.bottomNavView
        navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_feed, R.id.navigation_discoverFragment,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        sharedViewModel.firebaseUser.observe(this, Observer {
            userName = it.userName
        })

        // setting click listener to handle the case that logout button was pressed
        navView.setOnItemSelectedListener {
            if (it.itemId == R.id.action_logout) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Are you sure you want to leave?")
                    .setMessage("Don't worry, your data will wait for you until next time.")
                    .setNegativeButton("No") { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        Firebase.auth.signOut()
                        Toast.makeText(
                            baseContext, "User Signed out.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .show()
            } else {
                it.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
            }
            true
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }


    fun ensureBottomNavigation() {
        if(binding.bottomNavView.translationY == 0f) {
            val layoutParams = binding.bottomLayout.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as HideBottomViewOnScrollBehavior

            behavior.slideUp(binding.bottomLayout)
        }
    }


    fun ensureToolBar() {
        if (binding.toolBar.translationY == 0f) {
            val params = binding.toolBar.layoutParams as AppBarLayout.LayoutParams
            params.scrollFlags = SCROLL_FLAG_NO_SCROLL
            supportActionBar?.show()
        }
    }


    fun makeScroll() {
        val params = binding.toolBar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
        supportActionBar?.show()
    }


    fun showHistoryMenu(v: View, @MenuRes menuRes: Int, talkItem: TalkItem) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            if (menuItem.itemId == R.id.removeItem) {
                sharedViewModel.removeHistoryVideo(talkItem)
            }
            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }


    fun showLikeMenu(v: View, @MenuRes menuRes: Int, talkItem: TalkItem) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            if (menuItem.itemId == R.id.removeItem) {
                sharedViewModel.removeLikeVideo(talkItem)
            }
            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}