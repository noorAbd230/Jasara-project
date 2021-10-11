package com.example.jasaraapplication.fragments

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.jasaraapplication.R
import com.example.jasaraapplication.adapter.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_slider_image.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_orders.view.*
import kotlinx.android.synthetic.main.tab_content.*
import kotlinx.android.synthetic.main.tab_content.view.*


class OrdersFragment : Fragment()  ,View.OnClickListener{

    lateinit var tvColor:ColorStateList
    lateinit var fragments : ArrayList<Fragment>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_orders, container, false)

        root.order_waiting.setOnClickListener(this)
        root.order_done.setOnClickListener(this)
        root.order_canceled.setOnClickListener(this)

        tvColor = root.order_done.textColors

         fragments = ArrayList<Fragment>()

        fragments.add(WaitingOrdersFragment())
        fragments.add(DoneOrdersFragment())
        fragments.add(CanceledOrdersFragment())

        root.order_waiting.setTextColor(resources.getColor(R.color.teal_700))

        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), childFragmentManager ,fragments)
        root.orders_view_pager.adapter = sectionsPagerAdapter
        root.orders_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position){
                   0 -> {
                       root.order_waiting.setTextColor(resources.getColor(R.color.teal_700))
                       root.order_done.setTextColor(tvColor)
                       root.order_canceled.setTextColor(tvColor)
                   }
                   1 -> {
                       root.order_waiting.setTextColor(tvColor)
                       root.order_done.setTextColor(resources.getColor(R.color.teal_700))
                       root.order_canceled.setTextColor(tvColor)
                   }
                   2 -> {
                       root.order_waiting.setTextColor(tvColor)
                       root.order_done.setTextColor(tvColor)
                       root.order_canceled.setTextColor(resources.getColor(R.color.teal_700))
                   }
                }

            }

        })
        //root.tabs.setupWithViewPager(root.orders_view_pager)
        return root
    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.order_waiting -> {
                order_waiting.setTextColor(resources.getColor(R.color.teal_700))
                order_done.setTextColor(tvColor)
                order_canceled.setTextColor(tvColor)
                orders_view_pager.currentItem = 0
            }
            R.id.order_done -> {
                order_waiting.setTextColor(tvColor)
                order_done.setTextColor(resources.getColor(R.color.teal_700))
                order_canceled.setTextColor(tvColor)
                orders_view_pager.currentItem = 1
            }
            R.id.order_canceled -> {
                order_waiting.setTextColor(tvColor)
                order_done.setTextColor(tvColor)
                order_canceled.setTextColor(resources.getColor(R.color.teal_700))
                orders_view_pager.currentItem = 2
            }
        }

    }


}