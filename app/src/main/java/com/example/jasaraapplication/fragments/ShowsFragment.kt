package com.example.jasaraapplication.fragments

import ShowsAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.adapter.ImageAdapter
import com.example.jasaraapplication.model.Category
import com.example.jasaraapplication.model.Shows
import com.example.jasaraapplication.model.SliderOffers
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_category_details.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_shows.*
import kotlinx.android.synthetic.main.fragment_shows.view.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class ShowsFragment : Fragment() {


    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_shows, container, false)





        getLastOffers()

        root.show_filter.setOnClickListener {v ->
            val popup = PopupMenu(this.requireContext(), v)
            requireActivity().menuInflater.inflate(R.menu.show_filter_menu,popup.menu)
            popup.setOnMenuItemClickListener { menu_item ->
                when (menu_item.itemId) {
                }
                true
            }
            popup.show()
        }



        return root
    }


    fun getLastOffers(){
        val url="https://jazara.applaab.com/api/getLastOffers"

        val cache= MySingleton.getInstance()!!.getRequestQueue()!!.cache
        val entry=   cache.get(url)
        if (entry != null){
            try {
                val da=String(entry!!.data, Charset.forName("UTF-8"))


            }catch (e: UnsupportedEncodingException){
                Log.e("no", e.message!!)
            }
        }else {

            val jsonObject = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->

                    val lastProduct=mutableListOf<Shows>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var offers = cases.getJSONObject(i)
                        lastProduct.add(
                            Shows(offers.getString("id"),offers.getString("name_ar"),offers.getString("url_image")
                         ,offers.getString("description_ar"),offers.getString("updated_at"), offers.getString("price")
                        )
                        )

                    }

                    rvShows.layoutManager = LinearLayoutManager(this.requireContext())
                    rvShows.setHasFixedSize(true)
                    val productAdapter = ShowsAdapter(this.requireActivity(),lastProduct,requireActivity().supportFragmentManager,"Show screen")
                    rvShows.adapter=productAdapter

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    headers["Authorization"] = NumberValidationActivity.token
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }



}