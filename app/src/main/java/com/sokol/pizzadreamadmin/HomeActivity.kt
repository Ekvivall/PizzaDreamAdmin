package com.sokol.pizzadreamadmin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.AddAdminClick
import com.sokol.pizzadreamadmin.EventBus.EditProfileClick
import com.sokol.pizzadreamadmin.EventBus.LogOutClick
import com.sokol.pizzadreamadmin.EventBus.ProfileClick
import com.sokol.pizzadreamadmin.databinding.ActivityHomeBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_home)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_pizzerias,
                R.id.navigation_orders,
                R.id.navigation_news,
                R.id.navigation_vacancies
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.profile) {
            if (navController.currentDestination?.id != R.id.navigation_profile) {
                navController.navigate(R.id.navigation_profile)
            }
            else{
                onBackPressed()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun signOut() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog)
        builder.setTitle("Вихід").setMessage("Ви дійсно хочете вийти?")
            .setNegativeButton("Відміна") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Так") { dialogInterface, _ ->
                Common.currentUser = null
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        val dialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.red))
        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onAddAdmin(event: AddAdminClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_add_admin)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEditProfile(event: EditProfileClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_edit_profile)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onProfile(event: ProfileClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_profile)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLogOut(event: LogOutClick) {
        if (event.isSuccess) {
            signOut()
        }
    }
}