package com.example.jasaraapplication.activites

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.VolleyMultipartRequest
import com.example.jasaraapplication.activites.NumberValidationActivity.Companion.token
import com.example.jasaraapplication.fragments.ProfileFragment
import com.example.jasaraapplication.model.*
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.*
import java.io.File
import java.io.FileInputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class FormActivity : AppCompatActivity() {
//    lateinit var city:MutableList<String>
    companion object{
        lateinit var districtId :String
        lateinit var cityId :String
    }

    lateinit var gender : String
    lateinit var lang : String
    lateinit var nTitle : String
    val myCalendar: Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val items = listOf("male", "female")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        genderSpinner.adapter = adapter

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender =  items[position]
                Log.e("gender",gender)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val titleItems = listOf("ms", "mr")
        val titleAdapter = ArrayAdapter(this, R.layout.list_item, titleItems)
        spinnerNameTitle.adapter = titleAdapter

        spinnerNameTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                nTitle =  titleItems[position]
                Log.e("nTitle",nTitle)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val langItems = listOf("ar", "en")
        val langAdapter = ArrayAdapter(this, R.layout.list_item, langItems)
        spinnerLang.adapter = langAdapter

        spinnerLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lang =  langItems[position]
                Log.e("lang",lang)
                if (lang=="ar"){
                    var editor=getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE).edit()
                    editor.putString("My_Lang", "ar")
                    editor.apply()
                }else{
                    var editor= getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE).edit()
                    editor.putString("My_Lang", "en")
                    editor.apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        getCities()
         getQuarters()

        val date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }
        birthday.setOnClickListener{
                DatePickerDialog(
                    this@FormActivity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()

        }
        saveData.setOnClickListener {
            updateProfile()
        }
        go_back.setOnClickListener {
            var i= Intent(this, NumberValidationActivity::class.java)
            startActivity(i)
        }
    }


    private fun updateProfile(){

        val pref =
            getSharedPreferences("imagepref", Context.MODE_PRIVATE)
        var img = pref.getString("img",null)
        var from = pref.getString("fromProfile",null)
        val url="https://jazara.applaab.com/api/updateProfile"


        val queue= Volley.newRequestQueue(this)
        val request = object : VolleyMultipartRequest(
            Request.Method.POST,url,
            Response.Listener { response ->

               Log.e("update profile", response.toString())
                var i = Intent(this,
                    HomeActivity::class.java)
                startActivity(i)
                finish()
                val editor: SharedPreferences.Editor =
                    getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE).edit()
                editor.putString("from","settings")
                editor.apply()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"${error.message}", Toast.LENGTH_SHORT).show()
            }){

            override fun getParams(): MutableMap<String, String> {


                    var params = HashMap<String, String>()

                    params["mobile"] = etMobileNumber.text.toString()
                    params["name"] = firstName.text.toString()+lastName.text.toString()
                    params["gender"] = gender
                    params["password"] = password.text.toString()
                    params["email"] = etEmail.text.toString()
                    params["dob"] = birthday.text.toString()
                    params["city_id"] = cityId
                    params["name_title"] = nTitle
                    params["lang"] = lang
                    params["district id"] = districtId

                    return params


            }

            override fun getByteData(): Map<String, DataPart>? {
                val params: MutableMap<String, DataPart> = HashMap()

                if (from=="yes"){
                    params["image"] = DataPart(
                        "file_avatar.jpg",
                        getBitmapFromString(img!!),
                        "image/jpeg"
                    )
                }

                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                headers["Accept"] = "application/json"

                return headers
            }


        }
        queue.add(request)

    }


    fun getCities(){
        val url="https://jazara.applaab.com/api/getCities"
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
                    var city=mutableListOf<String>()
                    var citysId =mutableListOf<String>()

                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){

                        var cites = cases.getJSONObject(i)
                        city.add(cites.getString("name"))

                         citysId.add(cites.getString("id"))

                    }

                    val cityAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, city)
                    spinnerCity.adapter = cityAdapter
                    spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            cityId = citysId[position]
                            Log.e("city_id", cityId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                    }

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
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
    private fun updateLabel() {
        val myFormat = "MM/dd/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        birthday.setText(sdf.format(myCalendar.time))
    }
    fun getQuarters(){
        val url="https://jazara.applaab.com/api/getQuarters"
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
                    var quarters=mutableListOf<String>()
                    var quartersId =mutableListOf<String>()

                    val cases = response.getJSONArray("data")
                    for (i in 0 until cases.length()){

                        var cites = cases.getJSONObject(i)
                        quarters.add(cites.getString("name"))
                        quartersId.add(cites.getString("id"))
                    }

                    val quartersAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, quarters)
                    spinnerQuarters.adapter = quartersAdapter
                    spinnerQuarters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            districtId = quartersId[position]
                            Log.e("quarter_id", districtId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                    }

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
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


    private fun getBitmapFromString(image: String): ByteArray {
        val bytes: ByteArray = android.util.Base64.decode(image, android.util.Base64.DEFAULT)
        return bytes
    }
//    private fun getFileData(): ByteArray? {
//        val size = uri.length() as Int
//        val bytes = ByteArray(size)
//        val tmpBuff = ByteArray(size)
//        try {
//            FileInputStream(uri).use { inputStream ->
//                var read: Int = inputStream.read(bytes, 0, size)
//                if (read < size) {
//                    var remain = size - read
//                    while (remain > 0) {
//                        read = inputStream.read(tmpBuff, 0, remain)
//                        System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
//                        remain -= read
//                    }
//                }
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return bytes
//    }

}