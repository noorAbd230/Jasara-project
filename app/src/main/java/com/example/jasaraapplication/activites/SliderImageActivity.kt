package com.example.jasaraapplication.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.jasaraapplication.R
import com.example.jasaraapplication.adapter.WalkThroughAdapter
import com.example.jasaraapplication.model.WalkThrough
import kotlinx.android.synthetic.main.activity_slider_image.*

class SliderImageActivity : AppCompatActivity() {
    lateinit var dots:Array<ImageView>
    lateinit var data:MutableList<WalkThrough>
    var currentPage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider_image)

        data= mutableListOf()
        data.add(WalkThrough("لا داعي لتضييع وقتك بالمتاجر",
            R.drawable.man,resources.getString(R.string.sub_title)))
        data.add(WalkThrough("لا داعي لتضييع وقتك بالمتاجر",
            R.drawable.man,resources.getString(R.string.sub_title)))
        data.add(WalkThrough("لا داعي لتضييع وقتك بالمتاجر",
            R.drawable.man,resources.getString(R.string.sub_title)))

        var adapter = WalkThroughAdapter(this, data)
        view_pager.adapter = adapter
        createDots(0)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                createDots(position)
                currentPage=position
                if (position==2){
                    Handler().postDelayed({
                        var i = Intent(this@SliderImageActivity,
                            HomeActivity::class.java)
                        startActivity(i)
                        finish()

                    },4000)
                }

            }

        })

    }
    fun createDots(position: Int){
        if (dots_layout!=null){
            dots_layout.removeAllViews()
        }
        dots=Array(data.size, {i -> ImageView(this)  })
        for (i in 0 until data.size){
            dots[i]=ImageView(this)
            if (i==position){
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.tab_indicator_selected
                ))
            }else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.tab_indicator_default
                ))

            }
            var params= LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(10,0,10,0)
            dots_layout.addView(dots[i],params)
        }

    }

}