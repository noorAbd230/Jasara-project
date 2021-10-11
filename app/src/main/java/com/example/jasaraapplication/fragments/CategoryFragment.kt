package com.example.jasaraapplication.fragments

import CategoryAdapter
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.model.Category
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class CategoryFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_category, container, false)


        getCategory()


        return root
    }

    fun getCategory(){
        var prefs=requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language:String? = prefs.getString("My_Lang","ar")

        val url="https://jazara.applaab.com/api/getCategories"

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

                   val product=mutableListOf<Category>()
                   val cases = response.getJSONArray("data")
                   for (i in 0 until cases.length()){
                           var category = cases.getJSONObject(i)
                       if (language=="en"){
                           product.add(
                               Category(
                                   category.getString("name_en"),
                                   category.getString("image")
                               )
                           )
                       }else {
                           product.add(
                               Category(
                                   category.getString("name_ar"),
                                   category.getString("image")
                               )
                           )
                       }
                   }

                   rvCategory.layoutManager = GridLayoutManager(this.context,2)
                   rvCategory.setHasFixedSize(true)
                   val productAdapter = CategoryAdapter(this.requireActivity(),product,requireActivity().supportFragmentManager)
                   rvCategory.adapter=productAdapter

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