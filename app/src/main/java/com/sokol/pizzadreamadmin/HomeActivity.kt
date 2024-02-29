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
import com.sokol.pizzadreamadmin.EventBus.*
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
                R.id.navigation_category,
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
            } else {
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
    fun onUpdateCategory(event: UpdateCategoryClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_update_category)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onMenu(event: MenuClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_category)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdatePizzeria(event: UpdatePizzeriaClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_update_pizzeria)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPizzerias(event: PizzeriasClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_pizzerias)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateNews(event: UpdateNewsClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_update_news)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onNews(event: NewsClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_news)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateVacancy(event: UpdateVacancyClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_update_vacancy)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onVacancies(event: VacanciesClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_vacancies)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onNewsDetail(event: NewsItemClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_news_detail)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onVacancyDetail(event: VacancyItemClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_vacancy_detail)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_food_list)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPizzeriaSelected(event: PizzeriaClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_reviews_pizzeria)
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onVacancySelected(event: VacancyClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_resumes)
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_food_detail)
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onComments(event: CommentsClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_comments)
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateFood(event: UpdateFoodClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_update_food)
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onOrderSelected(event: OrderDetailClick) {
        if (event.isSuccess) {
            navController.navigate(R.id.navigation_order_detail)
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLogOut(event: LogOutClick) {
        if (event.isSuccess) {
            signOut()
        }
    }
}