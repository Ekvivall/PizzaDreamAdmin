package com.sokol.pizzadreamadmin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Model.UserModel
import io.reactivex.disposables.CompositeDisposable

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var database: FirebaseDatabase
    private lateinit var userInfoRef: DatabaseReference
    private var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(listener)
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuth.removeAuthStateListener(listener)
        compositeDisposable.clear()
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
        userInfoRef = database.getReference(Common.USER_REFERENCE)
        firebaseAuth = FirebaseAuth.getInstance()
        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            // Користувач вже увійшов, перенаправити на HomeActivity
            if (user != null) {
                userInfoRef.child(user.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                this@SplashScreenActivity, "" + p0.message, Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val model = p0.getValue(UserModel::class.java)
                            Common.currentUser = model

                            if (model!!.role == "admin") {
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        HomeActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                    })
            } else {
                // Користувач ще не увійшов, перенаправляємо на MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}