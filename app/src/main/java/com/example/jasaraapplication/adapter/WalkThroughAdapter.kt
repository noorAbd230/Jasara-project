package com.example.jasaraapplication.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.jasaraapplication.R
import com.example.jasaraapplication.model.WalkThrough
import kotlinx.android.synthetic.main.viewpager_item.view.*


class WalkThroughAdapter(var activity: Activity, var data: MutableList<WalkThrough>) :
    PagerAdapter(){

  lateinit var inflator:LayoutInflater
    override fun isViewFromObject(view: View, `object`: Any): Boolean {

       return view==`object` as RelativeLayout
    }

    override fun getCount(): Int {
      return data.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
         inflator=activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflator.inflate(R.layout.viewpager_item, container, false)
        itemView.title.text = data[position].title
        itemView.sub_title.text = data[position].subTitle.toString()
        itemView.linear.setBackgroundResource(data[position].img)
         container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }


}