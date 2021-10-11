package com.example.jasaraapplication.fragments

import CategorySimilarAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.model.CategorySimilar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_category_products_dtails.*
import kotlinx.android.synthetic.main.fragment_category_products_dtails.rvSimilarCategory
import kotlinx.android.synthetic.main.fragment_category_products_dtails.view.*
import kotlinx.android.synthetic.main.fragment_shows_details.*
import kotlinx.android.synthetic.main.fragment_shows_details.view.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.HashMap


class ShowsDetailsFragment : Fragment() {
    var amount = 0
    var favorite = 0
    lateinit var offerId :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_shows_details, container,false)

        val prefs: SharedPreferences = requireActivity().getSharedPreferences("Shows Details",
            Context.MODE_PRIVATE
        )
        val fav = prefs.getString("fav", null)


        if (fav == "true"){
            val name = prefs.getString("fName", null)
            val img = prefs.getString("fImg", null)
            offerId = prefs.getString("fId", null).toString()
            val price = prefs.getString("fPrice", null)
            val desc = prefs.getString("fDesc", null)
            val date = prefs.getString("fDate", null)
            val favoriteEdit = prefs.getInt("favorite", 0)

            val lstValues = ArrayList<String>()

            root.showDetailsNameTitle.text = name
            root.showDetailsName.text = name
            root.showDetailsDesc.text = desc
            root.showDetailsPrice.text = price
            var offerDate = date!!.substring(11,19)
            var strSep = offerDate.split(":")
            for (element in strSep){
                lstValues.add(element)
            }
            root.showsHour.text = lstValues[0]
            root.showsMinute.text = lstValues[1]
            root.showsSeconds.text = lstValues[2]
            Picasso.get().load(img).error(R.drawable.category1).into(root.showDetails_img)

            if (favoriteEdit==0){
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }

        }else if (fav == "home"){

            val name = prefs.getString("hName", null)
            val img = prefs.getString("hImg", null)
            offerId = prefs.getString("hId", null).toString()
            val price = prefs.getString("hPrice", null)
            val desc = prefs.getString("hDesc", null)
            val date = prefs.getString("hDate", null)

            val lstValues = ArrayList<String>()

            root.showDetailsNameTitle.text = name
            root.showDetailsName.text = name
            root.showDetailsDesc.text = desc
            root.showDetailsPrice.text = price
            var offerDate = date!!.substring(11,19)
            var strSep = offerDate.split(":")
            for (element in strSep){
                lstValues.add(element)
            }
            root.showsHour.text = lstValues[0]
            root.showsMinute.text = lstValues[1]
            root.showsSeconds.text = lstValues[2]
            Picasso.get().load(img).error(R.drawable.category1).into(root.showDetails_img)

        }else if (fav == "slider"){

            val name = prefs.getString("sName", null)
            val img = prefs.getString("sImg", null)
            offerId = prefs.getString("sId", null).toString()
            val price = prefs.getString("sPrice", null)
            val desc = prefs.getString("sDesc", null)
            val date = prefs.getString("sDate", null)

            val lstValues = ArrayList<String>()

            root.showDetailsNameTitle.text = name
            root.showDetailsName.text = name
            root.showDetailsDesc.text = desc
            root.showDetailsPrice.text = price
            var offerDate = date!!.substring(11,19)
            var strSep = offerDate.split(":")
            for (element in strSep){
                lstValues.add(element)
            }
            root.showsHour.text = lstValues[0]
            root.showsMinute.text = lstValues[1]
            root.showsSeconds.text = lstValues[2]
            Picasso.get().load(img).error(R.drawable.category1).into(root.showDetails_img)

        }
        else{
            val name = prefs.getString("name", null)
            val img = prefs.getString("img", null)
            offerId = prefs.getString("id", null).toString()
            val price = prefs.getString("price", null)
            val desc = prefs.getString("desc", null)
            val date = prefs.getString("date", null)

            val lstValues = ArrayList<String>()

            root.showDetailsNameTitle.text = name
            root.showDetailsName.text = name
            root.showDetailsDesc.text = desc
            root.showDetailsPrice.text = price
            var offerDate = date!!.substring(11,19)
            var strSep = offerDate.split(":")
            for (element in strSep){
                lstValues.add(element)
            }
            root.showsHour.text = lstValues[0]
            root.showsMinute.text = lstValues[1]
            root.showsSeconds.text = lstValues[2]
            Picasso.get().load(img).error(R.drawable.category1).into(root.showDetails_img)


        }
        root.shows_details_add.setOnClickListener {
            amount+=1
            root.shows_details_amount.text = amount.toString()
        }

        root.shows_details_minus.setOnClickListener {
            amount-=1
            root.shows_details_amount.text = amount.toString()
        }

        root.offer_favorite.setOnClickListener {
            if (favorite==0){
                favorite = 1
                addFavorite(offerId!!)
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                favorite = 0
                root.pDetails_favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }



        root.show_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                ShowsFragment()
            ).commit()
        }

        root.orderNow.setOnClickListener {
            addToCart()
        }

     //   getRecommendationsOffers()

        return root
    }


    fun getDialog(){
        var view= layoutInflater.inflate(R.layout.dialog_item,null)
        val deleteDialog = Dialog(this.requireContext())
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.setContentView(view)
        deleteDialog.setCancelable(true)
        deleteDialog.show()




    }
    fun getRecommendationsOffers(){
        val url="https://jazara.applaab.com/api/getOneOffer/$offerId"

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

                    val product = mutableListOf<CategorySimilar>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()) {
                        var products = cases.getJSONObject(i)

                        product.add(CategorySimilar(products.getString("url_image")))
                    }

                    rvRecommended.layoutManager = LinearLayoutManager(
                        this.context,
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    rvRecommended.setHasFixedSize(true)
                    val productAdapter = CategorySimilarAdapter(
                        this.requireActivity(),
                        product,
                        "showsRecommended"
                    )
                    rvRecommended.adapter = productAdapter
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

                params["offer_id"] = id
                params["product_id"] = ""

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
    private fun addToCart(){
        val url="https://jazara.applaab.com/api/addCart"
        val queue= Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("addCart","$data")
                getDialog()


            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["device_token"] = NumberValidationActivity.deviceToken
                params["product_id"] = ""
                params["amount"] = amount.toString()
                params["offer_id"] = offerId

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

}