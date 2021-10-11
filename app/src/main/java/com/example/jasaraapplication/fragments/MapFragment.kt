package com.example.jasaraapplication.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.CheckNumberActivity
import com.example.jasaraapplication.activites.FormActivity.Companion.cityId
import com.example.jasaraapplication.activites.FormActivity.Companion.districtId
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.adapter.ImageAdapter
import com.example.jasaraapplication.model.SliderOffers
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_check_number.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class MapFragment : Fragment() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var edit = "No"
    lateinit var prefs: SharedPreferences
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocation: Location? = null
    var lat = 0.0
    var lng = 0.0
    companion object{
        lateinit var country :String
        lateinit var countryCode :String
        lateinit var addressId :String
    }
    lateinit var location : LatLng
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val languageToLoad = "ar"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(
            config,
            resources.displayMetrics
        )
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        map_view.getMapAsync(this)
      //  ,
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_map, container, false)


        root.fromMap_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                SettingsFragment()
            ).commit()

        }
         prefs = requireActivity().getSharedPreferences("editAddress",
            Context.MODE_PRIVATE
        )
        val id = prefs.getString("id", null)
        val title = prefs.getString("title", null)


         edit = prefs.getString("edit", "No")!!
        Log.e("edit",edit!!)
        when (edit){
            "No" -> {
                root.tvLocation.text = requireActivity().resources.getString(R.string.saveLocation)

            }
            "Yes" -> {
                root.tvLocation.text = requireActivity().resources.getString(R.string.editLocation)
                root.countryName.text = title
            }
        }

        root.track_address.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                UserAddressesFragment()
            ).addToBackStack(null).commit()
        }
       // getMyAddress()
        root.saveLocation.setOnClickListener {
            //getAddress(location.latitude,location.longitude)
            if (root.tvLocation.text == "تعديل موقعك"){
                updateAddress(id!!)
            }else
            addNewAddress()
        }
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity,R.raw.mapstyle))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        val lat = prefs.getString("lat", null)
        val lang = prefs.getString("lang", null)
        when(edit){
            "No" -> {

                val afif = LatLng(23.9052203, 42.9124955)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(afif,15f))

                var editor= prefs.edit()
                edit="Yes"
                editor.putString("edit",edit)
                editor.apply()
            }
            "Yes" -> {val afif = LatLng(lat!!.toDouble(), lang!!.toDouble())
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(afif,15f))}

        }


        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            location  = latLng
            getAddress(latLng.latitude,latLng.longitude)
        }
    }


    fun getAddress(lat: Double, lng: Double) {

        var addressList: List<Address>? = null

           val locale = Locale("ar")
            val geoCoder = Geocoder(context, locale)
            try {
                addressList = geoCoder.getFromLocation(lat,lng, 1)
                if (addressList!!.isNotEmpty()) {

                    val address = addressList[0]
                    Log.e("Address", address.toString())
                    country = address.subAdminArea
                    countryName.text = country
                    countryCode = if (address.postalCode != null) {
                        address.postalCode
                    } else "444"
                    Toast.makeText(context, countryCode,Toast.LENGTH_LONG).show()

                }

            } catch (e: IOException) {
                e.printStackTrace()
            }



    }

    private fun updateAddress(id:String){

        val url="https://jazara.applaab.com/api/addresses/$id"
        val queue= Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("updateAddress","$data")



            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["address"] = country
                params["location"] = "${location.latitude},${location.longitude}"
                params["code"] = countryCode
                params["city_id"] = cityId
                params["district_id"] = districtId
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

    private fun addNewAddress(){
        val url="https://jazara.applaab.com/api/addresses"
        val queue= Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
               // val data=  JSONObject(response)
                Log.e("Add New Address", response)

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["address"] = country
                params["location"] = "${location.latitude},${location.longitude}"
                params["code"] = countryCode
                params["city_id"] = cityId
                params["district_id"] = districtId

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


    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = location
            }

            lat = mLocation!!.latitude
            lng = mLocation!!.longitude

            val location = LatLng(lat, lng)

            mMap.addMarker(MarkerOptions().position(location).title("Your Location"))


            Log.e("MapsFragment", "$lat $lng")
            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(this)
            }
        }
    }




    fun getLocationRequest(): LocationRequest? {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        return locationRequest
    }
}