package com.example.jasaraapplication.activites

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.jasaraapplication.R

class MainActivity : AppCompatActivity() {
    var firstTime = "false"
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firstTime= sharedPreferences.getString("firstTime","true")!!

        when (firstTime) {
            "true" -> {

                Handler().postDelayed({
                    var editor= sharedPreferences.edit()
                    firstTime="false"
                    editor.putString("firstTime",firstTime)
                    editor.apply()
                    var i = Intent(this,
                        SliderImageActivity::class.java)
                    startActivity(i)
                    finish()

                },4000)

            }

            "logout" -> {
                var i = Intent(this,
                    CheckNumberActivity::class.java)
                startActivity(i)
                finish()
            }
            else -> {
                var i = Intent(this,
                    HomeActivity::class.java)
                startActivity(i)
                finish()
                val editor: SharedPreferences.Editor =
                    getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE).edit()
                editor.putString("from","home")
                editor.apply()
            }
        }

    }
}