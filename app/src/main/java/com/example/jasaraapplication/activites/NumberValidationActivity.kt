package com.example.jasaraapplication.activites

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.CheckNumberActivity.Companion.number
import com.example.jasaraapplication.activites.CheckNumberActivity.Companion.verCode
import com.example.jasaraapplication.fragments.HomeFragment
import com.example.jasaraapplication.fragments.SettingsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_number_validation.*
import org.json.JSONObject
import java.util.jar.Manifest

class NumberValidationActivity : AppCompatActivity() {
    companion object{
        lateinit var token : String
        lateinit var deviceToken : String
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_validation)

        var verCode = intent.getStringExtra("verCode")



        back.setOnClickListener {
            var i= Intent(this, CheckNumberActivity::class.java)
            startActivity(i)
            finish()
        }
        validatinBtn.setOnClickListener {
            var num1 = numOne.text.toString()
            var num2 = numTwo.text.toString()
            var num3 = numThree.text.toString()
            var num4 = numFour.text.toString()
            var verNum =  num1+num2+num3+num4

            if(verCode==verNum){
                getToken()
            }else{
                Snackbar.make(contextView, "رمز التحقق غير فعال, يرجى المحاولة مرة أخرى", Snackbar.LENGTH_LONG).show()
            }

        }
    }

    private fun getToken(){

        deviceToken = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)


        val url="https://jazara.applaab.com/api/checkVerificationCode"
        val queue= Volley.newRequestQueue(this)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                val cData = data.getJSONObject("data")
                Log.e("check ver code","$data")
                token =  cData.getString("access_token")
                // sendSms(verCode)
                Log.e("token",token)
                val editor: SharedPreferences.Editor =
                    getSharedPreferences("userLogin", Context.MODE_PRIVATE).edit()
                editor.putBoolean("login",true)
                editor.apply()
                var i = Intent(this,
                    HomeActivity::class.java)
                startActivity(i)
                finish()
                val pEditor: SharedPreferences.Editor =
                    getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE).edit()
                pEditor.putString("from","home")
                pEditor.apply()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show()
            }){

            override fun getParams(): MutableMap<String, String> {

                var params = HashMap<String, String>()
                params["mobile"] = number
                params["verification_code"] = verCode.toString()
                params["device_token"] = deviceToken
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }


        }



        queue.add(request)

    }
}