package com.example.jasaraapplication.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.jasaraapplication.R
import com.example.jasaraapplication.fragments.ProfileFragment
import com.example.jasaraapplication.fragments.SettingsFragment
import com.example.jasaraapplication.fragments.ShowsFragment


private val TAB_TITLES = arrayOf(
    R.id.order_waiting,
    R.id.order_done,
    R.id.order_canceled
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager,var fragment : ArrayList<Fragment>) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragment[position]
    }



    override fun getCount(): Int {
        return fragment.size
    }
}