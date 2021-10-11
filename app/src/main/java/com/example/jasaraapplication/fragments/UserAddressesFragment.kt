package com.example.jasaraapplication.fragments

import AddressesAdapter
import android.content.Context
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
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.model.Addresses
import com.example.jasaraapplication.model.Cart
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.rvcart
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_user_addresses.*
import kotlinx.android.synthetic.main.fragment_user_addresses.view.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.HashMap


class UserAddressesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_user_addresses, container, false)


        getMyAddress()
        root.addLocation.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                MapFragment()
            ).commit()
            val editor: SharedPreferences.Editor =
                requireActivity().getSharedPreferences("editAddress", Context.MODE_PRIVATE).edit()
            editor.putString("edit","No")
            editor.apply()
        }

        root.fromUserAddress_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                MapFragment()
            ).commit()
        }
        return root
    }

    fun getMyAddress(){
        val url="https://jazara.applaab.com/api/addresses"

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
                    val addresses=mutableListOf<Addresses>()
                    val cases = response.getJSONObject("data")
                    val data = cases.getJSONArray("data")
                    for (i in 0 until data.length()){
                        var address = data.getJSONObject(i)

                        addresses.add(Addresses(address.getString("id")
                        ,address.getString("address"),address.getString("lat"),
                        address.getString("long")))
                    }

                    if (addresses.isEmpty()){
                        addLocation.visibility = View.VISIBLE
                    }else {
                        addLocation.visibility = View.GONE
                        rvUserAddress.layoutManager = LinearLayoutManager(this.requireContext())
                        rvUserAddress.setHasFixedSize(true)
                        val productAdapter = AddressesAdapter(
                            this.requireActivity(),
                            addresses,
                            requireActivity().supportFragmentManager
                        )
                        rvUserAddress.adapter = productAdapter
                    }
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