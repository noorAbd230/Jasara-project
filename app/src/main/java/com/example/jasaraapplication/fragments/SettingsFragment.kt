package com.example.jasaraapplication.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.jasaraapplication.MySingleton
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.CheckNumberActivity
import com.example.jasaraapplication.activites.FormActivity
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.tvUserName
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class SettingsFragment : Fragment()  , GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 199
    var result: PendingResult<LocationSettingsResult>? = null
    private var locationManager: LocationManager? = null
    private var handler: Handler? = null
    var mLocation: Location? = null
    var lat = 0.0
    var lng = 0.0
    private val SPLASH_DISPLAY_LENGTH = 1000
    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = location
            }
            lat = mLocation!!.latitude
            lng = mLocation!!.longitude

            Log.e("Splash", "$lat $lng")
            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(this)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_settings, container, false)


        loadLoacle()
        var cPrefs = requireActivity().getSharedPreferences("profilePref",
            Context.MODE_PRIVATE
        )

        val name = cPrefs.getString("name", null)
        val mobile = cPrefs.getString("mobile", null)
        val img = cPrefs.getString("img", null)

        if (img!=null)  Picasso.get().load(img).error(R.drawable.meats).into(root.img_setting)

        root.userName.text = name
        root.userMobile.text = mobile

        root.toMore_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                MoreFragment()
            ).addToBackStack(null).commit()
        }

        root.favorites.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                FavoritesFragment()
            ).addToBackStack(null).commit()
        }

        root.orders.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                OrdersFragment()
            ).addToBackStack(null).commit()
        }

        root.lang.setOnClickListener {
            showChangeLanguageDialog()
        }
        root.location.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                MapFragment()
            ).addToBackStack(null).commit()
            val editor: SharedPreferences.Editor =
                requireActivity().getSharedPreferences("editAddress", Context.MODE_PRIVATE).edit()
                 editor.putString("edit","No")
                   editor.apply()
        }
        
        root.setting_logout.setOnClickListener {
            var sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            var editor = sharedPreferences.edit()
            editor.putString("firstTime", "logout")
            editor.apply()
            logout()
        }

        if (root.switchLoc.isChecked){
            requestStoragePermission()
            if (!isLocationServiceEnabled(requireContext())) if (mGoogleApiClient != null) {
                mGoogleApiClient!!.connect()
            }
        }else{
            if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            }
        }
        root.update_profile.setOnClickListener {
            val editor: SharedPreferences.Editor =
                requireActivity().getSharedPreferences("imagepref", Context.MODE_PRIVATE).edit()
            editor.putString("fromProfile","no")
            editor.apply()
            var i= Intent(requireContext(), FormActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }

        return root
    }

    fun logout(){
        val url="https://jazara.applaab.com/api/logout"

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

                  Log.e("logout", response.toString())

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




    fun showChangeLanguageDialog(){
        var listItems = arrayOf<String>("عربي","English")


        var mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("اختر اللغة..")

        mBuilder.setSingleChoiceItems(listItems,-1,
            DialogInterface.OnClickListener() { dialogInterface, i ->
            if (i == 0) {
                setLocale("ar")
                requireActivity().recreate()
                val editor: SharedPreferences.Editor =
                    requireActivity().getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE).edit()
                editor.putString("from","settings")
                editor.apply()

            }else
                if (i == 1) {
                    setLocale("en")
                    requireActivity().recreate()
                    val editor: SharedPreferences.Editor =
                        requireActivity().getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE).edit()
                    editor.putString("from","settings")
                    editor.apply()
                }


            dialogInterface.dismiss()

        })

        var mDialog =  mBuilder.create()
        mDialog.show()
    }



    private fun setLocale(lang:String) {

        val locale= Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        var editor=requireActivity().getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()

    }

    private fun loadLoacle(){
        var prefs=requireActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language:String? = prefs.getString("My_Lang","ar")
        println("RESULT$language")
        setLocale(language!!)
    }

    private fun requestStoragePermission() {
        Dexter.withActivity(requireActivity())
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.areAllPermissionsGranted()
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                1
                            )
                        } else {
                            mFusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(requireContext())
                            mLocationRequest = LocationRequest.create()
                            mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            if (isLocationServiceEnabled(requireContext())) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(
                                            requireContext(),
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                        == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        mFusedLocationClient!!.requestLocationUpdates(
                                            mLocationRequest,
                                            mLocationCallback,
                                            Looper.myLooper()
                                        )
                                    } //Request Location Permission
                                } else {
                                    mFusedLocationClient!!.requestLocationUpdates(
                                        mLocationRequest,
                                        mLocationCallback,
                                        Looper.myLooper()
                                    )
                                }
                                if (isLocationServiceEnabled(requireContext())) {

                                }
                            } else {
                                mGoogleApiClient = GoogleApiClient.Builder(requireContext())
                                    .addConnectionCallbacks(this@SettingsFragment)
                                    .addOnConnectionFailedListener(this@SettingsFragment)
                                    .addApi(LocationServices.API)
                                    .build()
                                mGoogleApiClient!!.connect()
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                Toast.makeText(
                    requireContext(),
                    "try_again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("NeedPermissions")
        builder.setMessage("PermissionsDes")
        builder.setPositiveButton("GOTOSETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    @SuppressLint("WrongConstant")
    fun isLocationServiceEnabled(context: Context): Boolean {
        var gps_enabled = false
        var network_enabled = false
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val criteria = Criteria()
            criteria.powerRequirement =
                Criteria.POWER_MEDIUM // Chose your desired power consumption level.
            criteria.accuracy = Criteria.ACCURACY_MEDIUM // Choose your accuracy requirement.
            criteria.isSpeedRequired = true // Chose if speed for first location fix is required.
            criteria.isAltitudeRequired = true // Choose if you use altitude.
            criteria.isBearingRequired = true // Choose if you use bearing.
            criteria.isCostAllowed = true // Choose if this provider can waste money :-)
            locationManager!!.getBestProvider(criteria, true)
            gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: IndexOutOfBoundsException) {
            ex.message
        }
        return gps_enabled || network_enabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> requestStoragePermission()
                Activity.RESULT_CANCELED -> requireActivity().finish()
                else -> {
                }
            }
            else -> {
            }
        }
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(3 * 1000)
            .setFastestInterval(1000)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result!!.setResultCallback(
            ResultCallback { result: LocationSettingsResult ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        status.startResolutionForResult(requireActivity(), REQUEST_LOCATION)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        )
    }


    override fun onConnectionSuspended(i: Int) {}


//    override fun onStart() {
//        super.onStart()
//        if (!isLocationServiceEnabled(requireContext())) if (mGoogleApiClient != null) {
//            mGoogleApiClient!!.connect()
//        }
//    }
//
//    override fun onStop() {
//        //TODO this added
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient!!.disconnect()
//        }
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
//        }
//        super.onStop()
//    }
//
//    override fun onDestroy() {
//        if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
//        handler!!.removeCallbacksAndMessages(null)
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
//        }
//        super.onDestroy()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
//        }
//    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

}