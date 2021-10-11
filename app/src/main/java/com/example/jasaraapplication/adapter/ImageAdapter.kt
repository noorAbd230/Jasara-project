package com.example.jasaraapplication.adapter

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.jasaraapplication.fragments.ShowsDetailsFragment
import com.example.jasaraapplication.model.SliderOffers
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_viewpager_item.view.*


class ImageAdapter(var activity: Activity, var data: MutableList<SliderOffers>,var fragment : FragmentManager) :
    SliderViewAdapter<ImageAdapter.MyViewHolder>() {
    lateinit var inflator:LayoutInflater
   inner class MyViewHolder(itemView: View) :
        ViewHolder(itemView) {

       val image= itemView.image
       val sliderBk= itemView.sliderBk
       val sliderPrice= itemView.sliderPrice
       val sliderDesc= itemView.sliderDesc

    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        inflator=activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflator.inflate(com.example.jasaraapplication.R.layout.home_viewpager_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(viewHolder: ImageAdapter.MyViewHolder?, position: Int) {

        Picasso.get().load(data[position].img).error(com.example.jasaraapplication.R.drawable.indianflour).into(viewHolder!!.image)
            viewHolder.sliderDesc.text = data[position].title
            viewHolder.sliderPrice.text = "$"+data[position].price
            viewHolder.sliderBk.setBackgroundResource(data[position].bk)

           viewHolder.sliderBk.setOnClickListener {
               fragment.beginTransaction().replace(
                   com.example.jasaraapplication.R.id.mainContainer,
                   ShowsDetailsFragment()
               ).addToBackStack(null).commit()

               val editor: SharedPreferences.Editor =
                   activity.getSharedPreferences("Shows Details", Context.MODE_PRIVATE).edit()
               editor.putString("sName",data[position].title)
               editor.putString("sId",data[position].id)
               editor.putString("sDate",data[position].date)
               editor.putString("sDesc",data[position].subTitle)
               editor.putString("sImg",data[position].img)
               editor.putString("sPrice",data[position].price)
               editor.putString("fav","slider")
               editor.apply()
           }
    }


}