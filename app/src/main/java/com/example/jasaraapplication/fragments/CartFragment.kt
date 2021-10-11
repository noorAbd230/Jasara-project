package com.example.jasaraapplication.fragments

import CartAdapter
import CartAdapter.Companion.cartId
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.CheckNumberActivity
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.activites.NumberValidationActivity.Companion.deviceToken
import com.example.jasaraapplication.activites.TrackingOrderActivity
import com.example.jasaraapplication.fragments.MapFragment.Companion.addressId
import com.example.jasaraapplication.model.Cart
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_check_number.*
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_number_validation.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.bottom_sheet_one_dialog.view.*
import kotlinx.android.synthetic.main.bottom_sheet_two_dialog.view.*
import kotlinx.android.synthetic.main.cart_item.*
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_shows.view.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.HashMap


class CartFragment : Fragment() {
    lateinit var prefs: SharedPreferences
    lateinit var delivery: String
    companion object{
        lateinit var orderId:String
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_cart, container, false)

        getCart()

        root.trackingOrder.setOnClickListener {
            var i = Intent(requireActivity(),TrackingOrderActivity::class.java)
            startActivity(i)
        }

        root.deleteCartItems.setOnClickListener {
            if (checkBox.visibility == View.VISIBLE)
               deleteCart()
        }



        root.btnSend.setOnClickListener {
            getBottomSheet()
        }

        return  root
    }

    fun getBottomSheet(){


       var prefs = requireActivity().getSharedPreferences("UserAddress",
            Context.MODE_PRIVATE
        )
        var cPrefs = requireActivity().getSharedPreferences("couponPref",
            Context.MODE_PRIVATE
        )


        val coupon = cPrefs.getString("coupon", null)
        val lat = prefs.getString("lat", null)
        val lang = prefs.getString("lang", null)
        val fromUserAddress = prefs.getBoolean("FromUserAddress", false)

        if (fromUserAddress){
            var view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            var bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.setCanceledOnTouchOutside(true)
            orderPrice(coupon!!,lat!!,lang!!,view)
            view.btnNext.setOnClickListener {
                confirmCart(bottomSheetDialog)
            }
            bottomSheetDialog.show()

        }
        else {
            var view = layoutInflater.inflate(R.layout.bottom_sheet_one_dialog, null)
            var bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.setCanceledOnTouchOutside(true)
            val coupon = view.etCoupon.text.toString()
            val editor: SharedPreferences.Editor =
                requireActivity().getSharedPreferences("couponPref", Context.MODE_PRIVATE).edit()
            editor.putString("coupon",coupon)
            editor.apply()
            view.btnCouponNext.setOnClickListener {

                    checkCoupon(coupon,bottomSheetDialog)
            }
            bottomSheetDialog.show()
        }




    }
    fun getCart(){

        var prefs=requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language:String? = prefs.getString("My_Lang","ar")
        val url="https://jazara.applaab.com/api/getCart?device_token=${NumberValidationActivity.deviceToken}"

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
                    val product=mutableListOf<Cart>()
                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){
                        var cart = cases.getJSONObject(i)

                       if (cart.isNull("product")){
                          Log.e("pro","null")
                       }else{
                           var products = cart.getJSONObject("product")
                           var category = products.getJSONObject("category")
                           if (language=="en"){
                               product.add(
                                   Cart(
                                       cart.getString("id"),
                                       products.getString("name_en"),
                                       category.getString("name_en"),
                                       products.getString("url_image"),
                                       cart.getString("amount"),
                                       products.getString("price")
                                   )
                               )
                           }else {
                               product.add(
                                   Cart(
                                       cart.getString("id"),
                                       products.getString("name_ar"),
                                       category.getString("name_ar"),
                                       products.getString("url_image"),
                                       cart.getString("amount"),
                                       products.getString("price")
                                   )
                               )
                           }
                       }
                        if (cart.isNull("offer")){
                            Log.e("off","null")
                        }else{
                            var offer = cart.getJSONObject("offer")
                            var details = offer.getJSONArray("details")
                            for (i in 0 until details.length()) {
                                var cart = details.getJSONObject(i)
                                var products = cart.getJSONObject("product")
                                product.add(Cart(offer.getString("id"),products.getString("name_ar"),""
                                    ,products.getString("url_image"),cart.getString("amount"),cart.getString("offer_price")
                                ))
                            }
                        }

                    }

                    rvcart.layoutManager = LinearLayoutManager(this.requireContext())
                    rvcart.setHasFixedSize(true)
                    val productAdapter = CartAdapter(this.requireActivity(),product)
                    rvcart.adapter=productAdapter
                    itemsNum.text = "(${productAdapter.data.size})"
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

    fun deleteCart(){
        val url="https://jazara.applaab.com/api/delCart/${cartId}"
        val queue= Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Request.Method.DELETE,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("deleteAddress","$data")

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){

            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }


        }



        queue.add(request)

    }


    private fun checkCoupon(coupon:String,bottomSheetDialog:BottomSheetDialog){
        val url="https://jazara.applaab.com/api/checkCoupon"
        val queue= Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("Check Coupon","$data")
                val couponData = data.getJSONObject("data")
               // if (couponData.getBoolean("coupon_avilable")) {
                 bottomSheetDialog.dismiss()
                    requireActivity().supportFragmentManager.beginTransaction().replace(
                        R.id.mainContainer,
                        UserAddressesFragment()
                    ).addToBackStack(null).commit()

              //  }else{
                 //   Snackbar.make(contextView, "كوبون الخصم غير موجود, يرجى المحاولة مرة أخرى", Snackbar.LENGTH_LONG).show()
               // }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["code"] = coupon
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()

                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                return headers
            }


        }

        queue.cache.clear()
        queue.add(request)

    }

    private fun orderPrice(coupon:String,lat:String,lang:String,view:View){
        val url="https://jazara.applaab.com/api/orderPrice?client_location=$lat,$lang&device_token=$deviceToken&code=$coupon"
        val queue= Volley.newRequestQueue(context)
        val request = @SuppressLint("SetTextI18n")
        object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("order Price","$data")
                val priceData = data.getJSONObject("data")
               var sell_sum= priceData.getString("sell_sum")
               var discount_offer = priceData.getString("discount_offer")
               var delivery_price = priceData.getString("delivery_price")
              var discount_value =  priceData.getString("discount_value")
               var vat_value = priceData.getString("vat_value")
              var final_total =  priceData.getString("finel_totel")
              var total_after_discount =  priceData.getString("total_after_discount")


                view.sellSum.text = "$$sell_sum"
                view.deliveryCost.text = "$$delivery_price"
                view.discount_value.text = "$$discount_value"
                view.discount_offer.text = "$$discount_offer"
                view.total_after_discount.text = "$$total_after_discount"
                view.vat_value.text = "$$vat_value"
                view.final_total.text = "$$final_total"

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["client_location"] = "$lat,$lang"
                params["device_token"] = deviceToken
                params["code"] = coupon
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()

                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                return headers
            }


        }



        queue.add(request)

    }

    private fun confirmCart(bottomSheetDialog:BottomSheetDialog){
        val url="https://jazara.applaab.com/api/confirmCart"
        val queue= Volley.newRequestQueue(context)
        val request =
        object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("confirm Cart","$data")
                 var orderData =  data.getJSONObject("data")
                 var order = orderData.getJSONObject("order")
                  orderId =  order.getString("id")

                var viewOne = layoutInflater.inflate(R.layout.bottom_sheet_two_dialog,null)
                bottomSheetDialog.setContentView(viewOne)
                bottomSheetDialog.setCanceledOnTouchOutside(true)
                val items = listOf("manual", "external","delivery_worker")
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                viewOne.deliverySpinner.adapter = adapter

                viewOne.deliverySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        delivery =  items[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
                viewOne.btnFinish.setOnClickListener {
                    completeOrder(bottomSheetDialog)
                    bottomSheetDialog.dismiss()
                }


                bottomSheetDialog.show()

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["device_token"] = deviceToken
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()

                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                return headers
            }


        }



        queue.add(request)

    }
    private fun completeOrder(bottomSheetDialog: BottomSheetDialog){
        var prefs = requireActivity().getSharedPreferences("UserAddress",
            Context.MODE_PRIVATE
        )


        val id = prefs.getString("id", null)
        val url="https://jazara.applaab.com/api/completeOrder"
        val queue= Volley.newRequestQueue(context)
        val request =
            object : StringRequest(
                Request.Method.POST,url,
                Response.Listener { response ->
                    val data=  JSONObject(response)
                    Log.e("complete Order","$data")


                },
                Response.ErrorListener { error ->
                    //Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
                }){
                override fun getParams(): MutableMap<String, String> {
                    var params = HashMap<String, String>()
                    params["order_id"] = orderId
                    params["address_id"] = id!!
                    params["delivery_type"] = delivery
                    params["device_token"] = deviceToken
                    return params
                }
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()

                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                    return headers
                }


            }



        queue.add(request)

    }
}