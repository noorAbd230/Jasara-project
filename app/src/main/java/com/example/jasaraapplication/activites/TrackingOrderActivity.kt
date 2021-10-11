package com.example.jasaraapplication.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jasaraapplication.R
import com.example.jasaraapplication.fragments.CartFragment
import com.example.jasaraapplication.fragments.ShowsFragment
import kotlinx.android.synthetic.main.activity_tracking_order.*

class TrackingOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_order)


        previous_order_back.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                CartFragment()
            ).commit()
        }
    }
}