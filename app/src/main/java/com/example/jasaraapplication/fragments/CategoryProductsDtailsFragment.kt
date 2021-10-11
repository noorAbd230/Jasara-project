package com.example.jasaraapplication.fragments

import CategorySimilarAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.CheckNumberActivity
import com.example.jasaraapplication.activites.FormActivity
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.activites.NumberValidationActivity.Companion.deviceToken
import com.example.jasaraapplication.model.CategorySimilar
import com.example.jasaraapplication.model.Products
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_category_products_dtails.*
import kotlinx.android.synthetic.main.fragment_category_products_dtails.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.rvProductsMostRequested
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.HashMap


class CategoryProductsDtailsFragment : Fragment() {

     var amount = 0
     var favoriteEdit = 0
     var productId : String? = null
    lateinit var cartId : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_category_products_dtails, container, false)


        root.category_details_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                CategoryDetailsFragment()
            ).addToBackStack(null).commit()
        }


        val prefs: SharedPreferences = requireActivity().getSharedPreferences("sendCategoryDetails",
            Context.MODE_PRIVATE
        )
        val from = prefs.getString("from", null)


        if (from=="category"){
            val name = prefs.getString("name", null)

            productId = prefs.getString("id", null)
            val img = prefs.getString("img", null)
            val desc = prefs.getString("desc", null)
            val price = prefs.getString("price", null)
            val favorite = prefs.getInt("favorite", 0)

            Picasso.get().load(img).into(root.category_product_img)
            root.categoryProductDetailsName.text = name
            root.product_details_subtitle.text = desc
            root.priceForKilo.text = price

            if (favorite==0){
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }else{
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        }else{
            val name = prefs.getString("hName", null)

            productId = prefs.getString("hId", null)
            val img = prefs.getString("hImg", null)
            val desc = prefs.getString("hDesc", null)
            val price = prefs.getString("hPrice", null)

            Picasso.get().load(img).into(root.category_product_img)
            root.categoryProductDetailsName.text = name
            root.product_details_subtitle.text = desc
            root.priceForKilo.text = price
        }


        root.pDetails_favorite.setOnClickListener {
            if (favoriteEdit==0){
                favoriteEdit = 1
                addFavorite(productId!!)
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                favoriteEdit = 0
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }

        }


        root.product_details_add.setOnClickListener {
            amount+=1
            root.product_details_amount.text = amount.toString()
        }

        root.product_details_minus.setOnClickListener {
            amount-=1
            root.product_details_amount.text = amount.toString()
        }



        root.btnAddToCart.setOnClickListener {
            requireActivity().getSharedPreferences("userLogin", Context.MODE_PRIVATE)
            var login = prefs.getBoolean("login",false)
            if (login) {
                addToCart()
            }else{
                var i= Intent(activity, CheckNumberActivity::class.java)
                requireActivity().startActivity(i)
            }
        }
        getRecommendationsProducts()



        return root
    }

    fun getRecommendationsProducts(){
        val url="https://jazara.applaab.com/api/getRecommendationsProducts/$productId"

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

                    val product=mutableListOf<CategorySimilar>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var products = cases.getJSONObject(i)

                        product.add(CategorySimilar(products.getString("url_image")))
                    }

                   rvSimilarCategory.layoutManager = LinearLayoutManager(this.context,
                        LinearLayoutManager.HORIZONTAL,false)
                    rvSimilarCategory.setHasFixedSize(true)
                    val productAdapter = CategorySimilarAdapter(this.requireActivity(),product,"CategorySimilar")
                    rvSimilarCategory.adapter=productAdapter

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

    private fun addToCart(){
        val url="https://jazara.applaab.com/api/addCart"
        val queue= Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("addCart","$data")



            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["device_token"] = deviceToken
                params["product_id"] = productId!!
                params["amount"] = amount.toString()
                params["offer_id"] = ""

                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()

                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }


        }



        queue.add(request)

    }

    private fun addFavorite(id:String){

        val url="https://jazara.applaab.com/api/addFigure"


        val queue= Volley.newRequestQueue(activity)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                Log.e("add Favorite", response)
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity,"${error.message}", Toast.LENGTH_SHORT).show()
            }){

            override fun getParams(): MutableMap<String, String> {

                var params = HashMap<String, String>()

                params["offer_id"] = ""
                params["product_id"] = id

                return params
            }



            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Authorization"] = "Bearer "+ NumberValidationActivity.token
                headers["Accept"] = "application/json"

                return headers
            }


        }
        queue.add(request)

    }

}