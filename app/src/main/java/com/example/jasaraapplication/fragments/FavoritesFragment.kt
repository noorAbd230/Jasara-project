package com.example.jasaraapplication.fragments

import ShowsAdapter
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.model.Shows
import kotlinx.android.synthetic.main.fragment_favorites.*
import java.io.UnsupportedEncodingException


class FavoritesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        getFavorite()
        return root
    }

    fun getFavorite(){
        val prefs=requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language:String? = prefs.getString("My_Lang","ar")

        val url="https://jazara.applaab.com/api/getFavoriate"

        val cache= MySingleton.getInstance()!!.getRequestQueue()!!.cache
        val entry=   cache.get(url)
        if (entry != null){
            try {
                //val da=String(entry!!.data, Charset.forName("UTF-8"))


            }catch (e: UnsupportedEncodingException){
                Log.e("no", e.message!!)
            }
        }else {

            val jsonObject = object : JsonObjectRequest(
                Method.GET, url, null,
                Response.Listener { response ->

                    val lastProduct=mutableListOf<Shows>()
                    val cases = response.getJSONObject("data")
                    val offers = cases.getJSONArray("offers")
                    val products = cases.getJSONArray("products")
                    for (i in 0 until offers.length()){
                        val offerObj = offers.getJSONObject(i)
                        val offer = offerObj.getJSONObject("offer")
                        lastProduct.add(
                            Shows(offer.getString("id"),offer.getString("name_ar"),offer.getString("url_image")
                                ,offer.getString("description_ar"),offer.getString("updated_at"), offer.getString("price")
                            )
                        )
                    }

                    rvFavoritesOffers.layoutManager = LinearLayoutManager(this.requireContext())
                    rvFavoritesOffers.setHasFixedSize(true)
                    val offerAdapter = ShowsAdapter(this.requireActivity(),lastProduct,requireActivity().supportFragmentManager,"offers")
                    rvFavoritesOffers.adapter=offerAdapter

                    for (i in 0 until products.length()){
                        val offerObj = products.getJSONObject(i)
                        val offer = offerObj.getJSONObject("product")
                        if (language=="en"){
                            lastProduct.add(
                                Shows(
                                    offer.getString("id"),
                                    offer.getString("name_en"),
                                    offer.getString("url_image"),
                                    offer.getString("description_en"),
                                    offer.getString("updated_at"),
                                    offer.getString("price")
                                )
                            )
                        }else {
                            lastProduct.add(
                                Shows(
                                    offer.getString("id"),
                                    offer.getString("name_ar"),
                                    offer.getString("url_image"),
                                    offer.getString("description_ar"),
                                    offer.getString("updated_at"),
                                    offer.getString("price")
                                )
                            )
                        }
                    }

                    rvFavoritesProducts.layoutManager = LinearLayoutManager(this.requireContext())
                    rvFavoritesProducts.setHasFixedSize(true)
                    val productAdapter = ShowsAdapter(this.requireActivity(),lastProduct,requireActivity().supportFragmentManager,"favorite screen")
                    rvFavoritesProducts.adapter=productAdapter

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                    headers["Accept"] = "application/json"
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }
}