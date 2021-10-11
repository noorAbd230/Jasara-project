package com.example.jasaraapplication.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.HashMap


class MoreFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_more, container, false)

        getUser()
        root.setting.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                SettingsFragment()
            ).addToBackStack(null).commit()
        }


        root.payment_method.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                PaymentMethodsFragment()
            ).addToBackStack(null).commit()
        }

        root.profile.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                ProfileFragment()
            ).addToBackStack(null).commit()
        }

        return root
    }

    fun getUser(){
        val url="https://jazara.applaab.com/api/getUser"

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
                Request.Method.POST, url, null,
                Response.Listener { response ->

                    val cases = response.getJSONObject("data")
                    val user = cases.getJSONObject("user")
                    more_name.text = user.getString("name")
                    more_mobile.text = user.getString("mobile")
                    Picasso.get().load(user.getString("profile_img_url")).error(R.drawable.meats).into(img_more)

                    val editor: SharedPreferences.Editor =
                        requireActivity().getSharedPreferences("profilePref", Context.MODE_PRIVATE).edit()
                    editor.putString("name",user.getString("name"))
                    editor.putString("mobile",user.getString("mobile"))
                    editor.putString("email",user.getString("email"))
                    editor.putString("img",user.getString("profile_img_url"))
                    editor.apply()

                },
                Response.ErrorListener { error ->
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Authorization"] = "Bearer "+ NumberValidationActivity.token
                    headers["Accept"] = "application/json"
                    return headers
                }
            }
            MySingleton.getInstance()!!.addRequestQueue(jsonObject)
        }
    }

}