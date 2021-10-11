package com.example.jasaraapplication.activites

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.jasaraapplication.R
import com.example.jasaraapplication.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.mainContainer,
                        HomeFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_category -> {
                    replaceFragment(CategoryFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_shows -> {
                   replaceFragment(ShowsFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_cart -> {
                    var prefs = getSharedPreferences("UserAddress",
                        Context.MODE_PRIVATE
                    )
                    val editor = prefs.edit()
                    editor.putBoolean("FromUserAddress",false)
                    editor.apply()
                    replaceFragment(CartFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_more -> {
                   replaceFragment(MoreFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val prefs=
             getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE)
        var from = prefs.getString("from","home")
        when(from){
            "home" -> {
                nav_view.selectedItemId =
                R.id.navigation_home}
            "cart" -> {
                nav_view.selectedItemId =
                R.id.navigation_cart}
            "settings" -> {
                nav_view.selectedItemId =
                R.id.navigation_more}
        }

    }


    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(
            R.id.mainContainer,
            fragment).commit()

    }
}