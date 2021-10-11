package com.example.jasaraapplication.fragments

import CategoryDetailsAdapter
import ProductAdapter
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.model.CategoryDetails
import com.example.jasaraapplication.model.Products
import kotlinx.android.synthetic.main.fragment_category.view.*
import kotlinx.android.synthetic.main.fragment_category_details.*
import kotlinx.android.synthetic.main.fragment_category_details.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.rvProductsMostRequested
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class CategoryDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_category_details, container, false)


        val prefs: SharedPreferences = requireActivity().getSharedPreferences("sendName", MODE_PRIVATE)
        val name = prefs.getString("name", null)

        root.categoryDetailsName.text = name

        getMostOrderProducts(name!!)
        root.category_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                CategoryFragment()
            ).addToBackStack(null).commit()
        }

        root.search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.text.isEmpty()) {
                    getMostOrderProducts(name)
                } else
                getOneProduct(v.text.toString())
                return@OnEditorActionListener true
            }

            false
        })

        return root

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
                    val products = response.getJSONObject("data")

                    product.add(Products(products.getString("id"),products.getString("name_ar"),
                        products.getString("url_image"),"1",products.getString("price")))


                    rvCategoryDetails.layoutManager = LinearLayoutManager(this.context,
                        LinearLayoutManager.VERTICAL,false)
                    rvCategoryDetails.setHasFixedSize(false)
                    val productAdapter = ProductAdapter(this.requireActivity(),product,requireActivity().supportFragmentManager)
                    rvCategoryDetails.adapter=productAdapter
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
    fun getMostOrderProducts(categoryName : String){
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

                    val product=mutableListOf<CategoryDetails>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var products = cases.getJSONObject(i)
                         var category =  products.getJSONObject("category")
                         if (category.getString("name_ar") == categoryName) {

                             if (i%2 == 0){
                                 if (language=="en"){
                                     product.add(
                                         CategoryDetails(
                                             products.getString("id"),
                                             products.getString("name_en"),
                                             products.getString("url_image"),
                                             products.getString("price"),
                                             "#4DF7A775",
                                             products.getString("description_en")
                                         )
                                     )
                                 }else {
                                     product.add(
                                         CategoryDetails(
                                             products.getString("id"),
                                             products.getString("name_ar"),
                                             products.getString("url_image"),
                                             products.getString("price"),
                                             "#4DF7A775",
                                             products.getString("description")
                                         )
                                     )
                                 }
                             }else
                                 if (language=="en"){
                                     product.add(
                                         CategoryDetails(
                                             products.getString("id"),
                                             products.getString("name_en"),
                                             products.getString("url_image"),
                                             products.getString("price"),
                                             "#4D4F8B8D",
                                             products.getString("description_en")
                                         )
                                     )
                                 }else {
                                     product.add(
                                         CategoryDetails(
                                             products.getString("id"),
                                             products.getString("name_ar"),
                                             products.getString("url_image"),
                                             products.getString("price"),
                                             "#4D4F8B8D",
                                             products.getString("description")
                                         )
                                     )
                                 }
                         }
                    }

                    rvCategoryDetails.layoutManager = GridLayoutManager(this.requireContext(),2)
                    rvCategoryDetails.setHasFixedSize(true)
                    val productAdapter = CategoryDetailsAdapter(this.requireActivity(),product,requireActivity().supportFragmentManager)
                    rvCategoryDetails.adapter=productAdapter

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



}