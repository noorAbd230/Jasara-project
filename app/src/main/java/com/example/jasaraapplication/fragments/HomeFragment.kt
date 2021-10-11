package com.example.jasaraapplication.fragments

import ProductAdapter
import ProductDiscountAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.CheckNumberActivity
import com.example.jasaraapplication.activites.NumberValidationActivity.Companion.token
import com.example.jasaraapplication.adapter.ImageAdapter
import com.example.jasaraapplication.model.Products
import com.example.jasaraapplication.model.ProductsDiscount
import com.example.jasaraapplication.model.SliderOffers
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.product_item.*
import kotlinx.android.synthetic.main.product_item.view.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class HomeFragment : Fragment(){
    lateinit var data:MutableList<SliderOffers>
    lateinit var productId: String
    lateinit var amount: String
     var visible = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_home, container, false)


        getLastOffers()
        getMostOrderOffers()
        getMostOrderProducts()


        root.slider_login.setOnClickListener {
            var i= Intent(requireContext(), CheckNumberActivity::class.java)
            startActivity(i)
        }
        root.offers_login.setOnClickListener {
            var i= Intent(requireContext(), CheckNumberActivity::class.java)
            startActivity(i)
        }


        root.search_field.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.text.isEmpty()){
                    getMostOrderProducts()
                    getMostOrderOffers()
                }else
                    getProductsAndOffers(v.text.toString())
                  //  getOneProduct(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })


        return root
    }


    fun getProductsAndOffers(search : String){
        val url="https://jazara.applaab.com/api/getProductsAndOffers?filter=$search"

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

                    val discountProduct=mutableListOf<ProductsDiscount>()
                    val cases = response.getJSONObject("data")
                    val products = cases.getJSONArray("offers")
                    for (i in 0 until products.length()){
                        var offers = products.getJSONObject(i)
                        if (i % 2 == 0){
                            discountProduct.add(ProductsDiscount(offers.getString("id"),offers.getString("updated_at"),offers.getString("name_ar"),
                                offers.getString("url_image")
                                ,offers.getString("description_ar"), "2",R.drawable.border_pink_card_style,offers.getString("price")
                            ))
                        }else{
                            discountProduct.add(ProductsDiscount(offers.getString("id"),offers.getString("updated_at"),offers.getString("name_ar"),
                                offers.getString("url_image")
                                ,offers.getString("description_ar"), "2",R.drawable.border_card_style,offers.getString("price")
                            ))
                        }


                    }

                    rvDiscountMostRequested.layoutManager = LinearLayoutManager(this.requireContext(),
                        LinearLayoutManager.HORIZONTAL,false)
                    rvDiscountMostRequested.setHasFixedSize(false)
                    val discountProductAdapter = ProductDiscountAdapter(this.requireActivity(),discountProduct,requireActivity().supportFragmentManager)
                    rvDiscountMostRequested.adapter=discountProductAdapter


                    val product=mutableListOf<Products>()
                    val productSearch = cases.getJSONArray("products")
                    for (i in 0 until productSearch.length()){
                        var products = productSearch.getJSONObject(i)

                        product.add(Products(products.getString("id"),products.getString("name_ar"),
                            products.getString("url_image"),"1",products.getString("price")))



                    }

                    rvProductsMostRequested.layoutManager = LinearLayoutManager(this.context,
                        LinearLayoutManager.VERTICAL,false)
                    rvProductsMostRequested.setHasFixedSize(false)
                    val productAdapter = ProductAdapter(this.requireActivity(),product,requireActivity().supportFragmentManager)
                    rvProductsMostRequested.adapter=productAdapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    headers["Authorization"] = token
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }

    fun getOneProduct(search : String){
        val url="https://jazara.applaab.com/api/getOneProduct/$search"

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


                    val product=mutableListOf<Products>()
                    if (response!=null){

                        val products = response.getJSONObject("data")

                        product.add(
                            Products(
                                products.getString("id"), products.getString("name_ar"),
                                products.getString("url_image"), "1", products.getString("price")
                            )
                        )

                    }
                    rvProductsMostRequested.layoutManager = LinearLayoutManager(this.context,
                        LinearLayoutManager.VERTICAL,false)
                    rvProductsMostRequested.setHasFixedSize(false)
                    val productAdapter = ProductAdapter(this.requireActivity(),product,requireActivity().supportFragmentManager)
                    rvProductsMostRequested.adapter=productAdapter
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    headers["Authorization"] = token
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }

    fun getMostOrderOffers(){
        val url="https://jazara.applaab.com/api/getMostOrderOffers"

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

                    val discountProduct=mutableListOf<ProductsDiscount>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var offers = cases.getJSONObject(i)
                        if (i % 2 == 0){
                            discountProduct.add(ProductsDiscount(offers.getString("id"),offers.getString("updated_at"),offers.getString("name_ar"),
                                offers.getString("url_image")
                                ,offers.getString("description_ar"), "2",R.drawable.border_pink_card_style,offers.getString("price")
                            ))
                        }else{
                            discountProduct.add(ProductsDiscount(offers.getString("id"),offers.getString("updated_at"),offers.getString("name_ar"),
                                offers.getString("url_image")
                                ,offers.getString("description_ar"), "2",R.drawable.border_card_style,offers.getString("price")
                            ))
                        }


                    }

                    if (discountProduct.isEmpty()){
                        offers_login.visibility = View.VISIBLE
                        val editor: SharedPreferences.Editor =
                            requireActivity().getSharedPreferences("userLogin", Context.MODE_PRIVATE).edit()
                        editor.putBoolean("login",false)
                        editor.apply()
                    }else {
                        offers_login.visibility = View.GONE
                        rvDiscountMostRequested.layoutManager = LinearLayoutManager(
                            this.requireContext(),
                            LinearLayoutManager.HORIZONTAL, false
                        )
                        rvDiscountMostRequested.setHasFixedSize(true)
                        val discountProductAdapter = ProductDiscountAdapter(
                            this.requireActivity(),
                            discountProduct,
                            requireActivity().supportFragmentManager
                        )
                        rvDiscountMostRequested.adapter = discountProductAdapter

                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    headers["Authorization"] = token
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }

    fun getMostOrderProducts(){
        var prefs=requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language:String? = prefs.getString("My_Lang","ar")

        val url="https://jazara.applaab.com/api/getMostOrderProducts"

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

                    val product=mutableListOf<Products>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var products = cases.getJSONObject(i)

                        if (language=="en"){
                            product.add(
                                Products(
                                    products.getString("id"),
                                    products.getString("name_en"),
                                    products.getString("url_image"),
                                    products.getString("description_en"),
                                    products.getString("price")
                                )
                            )
                        }else {
                            product.add(
                                Products(
                                    products.getString("id"),
                                    products.getString("name_ar"),
                                    products.getString("url_image"),
                                    products.getString("description_ar"),
                                    products.getString("price")
                                )
                            )
                        }


                    }

                    rvProductsMostRequested.layoutManager = LinearLayoutManager(this.context,
                        LinearLayoutManager.VERTICAL,false)
                    rvProductsMostRequested.setHasFixedSize(true)
                    val productAdapter = ProductAdapter(this.requireActivity(),product,requireActivity().supportFragmentManager)
                    rvProductsMostRequested.adapter=productAdapter


                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }
    fun getLastOffers(){

        var prefs=requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language:String? = prefs.getString("My_Lang","ar")

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

                    val lastProduct=mutableListOf<SliderOffers>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var offers = cases.getJSONObject(i)

                        if (i % 2 == 0) {
                            lastProduct.add(
                                SliderOffers(
                                    offers.getString("id"),
                                    offers.getString("updated_at"),
                                    offers.getString("name_ar"),
                                    offers.getString("url_image"),
                                    offers.getString("description_ar"),
                                    offers.getString("price"),
                                    R.drawable.pink_slider_bk_style
                                )
                            )
                        }else{
                            lastProduct.add(
                                SliderOffers(
                                    offers.getString("id"),
                                    offers.getString("updated_at"),
                                    offers.getString("name_ar"),
                                    offers.getString("url_image"),
                                    offers.getString("description_ar"),
                                    offers.getString("price"),
                                    R.drawable.slider_bk_style
                                )
                            )
                        }


                    }

                    if (lastProduct.isEmpty()){
                        slider_login.visibility = View.VISIBLE
                        rvDiscountMostRequested.visibility = View.GONE
                        imageSlider.visibility = View.GONE
                        val editor: SharedPreferences.Editor =
                            requireActivity().getSharedPreferences("userLogin", Context.MODE_PRIVATE).edit()
                        editor.putBoolean("login",false)
                        editor.apply()
                    }else {
                        slider_login.visibility = View.GONE
                        rvDiscountMostRequested.visibility = View.VISIBLE
                        imageSlider.visibility = View.VISIBLE
                        var adapter = ImageAdapter(
                            this.requireActivity(),
                            lastProduct,
                            requireActivity().supportFragmentManager
                        )
                        imageSlider.setSliderAdapter(adapter)
                        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                        imageSlider.autoCycleDirection =
                            SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH


                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this.context, error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0"
                    headers["Authorization"] = token
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }






}