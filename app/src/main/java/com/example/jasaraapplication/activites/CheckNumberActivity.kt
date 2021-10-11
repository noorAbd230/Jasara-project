package com.example.jasaraapplication.activites

import android.R.attr
import android.R.id
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.R
import kotlinx.android.synthetic.main.activity_check_number.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class CheckNumberActivity : AppCompatActivity() {

    companion object{
       lateinit var number : String
        var verCode : Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_number)

        btnSend.setOnClickListener {
            pushMobileNum()
        }
    }


//    fun sentMsg(verificationCode:Int){
//        var smsNumber = String.format("smsto: %s",
//            ccp!!.selectedCountryCode + editTextPhone.editText!!.text)
//        var smsMsg = "${verificationCode}رمز التحقق هو : "
//        val smsIntent = Intent(Intent.ACTION_SENDTO)
//        smsIntent.data = Uri.parse(smsNumber)
//
//        smsIntent.putExtra("sms_body", smsMsg)
//        if (smsIntent.resolveActivity(packageManager) != null) {
//            startActivity(smsIntent)
//        } else {
//            Log.d("SmS", "Can't resolve app for ACTION_SENDTO Intent");
//        }
//        var smsManager = SmsManager.getDefault()
//        smsManager.sendTextMessage(smsNumber,null,smsMsg,null,null)
//    }


//   fun getVerificationCode(){
//       val url="https://jazara.applaab.com/api/sendVerificationCode"
//
//       val cache= MySingleton.getInstance()!!.getRequestQueue()!!.cache
//       val entry=   cache.get(url)
//       if (entry != null){
//           try {
//               val da=String(entry!!.data, Charset.forName("UTF-8"))
//
//
//           }catch (e: UnsupportedEncodingException){
//               Log.e("no", e.message!!)
//           }
//       }else {
//
//           val jsonObject = object : JsonObjectRequest(
//               Request.Method.GET, url, null,
//               Response.Listener { response ->
//
//                   val cases = response.getJSONObject("data")
//                   var verCode =  cases.getInt("verification_code")
//                   sentMsg(verCode)
//                   Log.e("verCode","$verCode")
//               },
//               Response.ErrorListener { error ->
//                   Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
//               }
//
//           ) {
//               override fun getHeaders(): MutableMap<String, String> {
//                   val headers =
//                       HashMap<String, String>()
//                   headers["User-Agent"] = "Mozilla/5.0"
//                   return headers
//               }
//           }
//           MySingleton.getInstance()!!.addRequestQueue(jsonObject)
//       }
//    }
    private fun pushMobileNum(){
        val url="https://jazara.applaab.com/api/sendVerificationCode"
        val queue= Volley.newRequestQueue(this)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("push num","$data")
                val cases = data.getJSONObject("data")
                 verCode =  cases.getInt("verification_code")
               // sendSms(verCode)
                Log.e("verCode","$verCode")
                Toast.makeText(this,"$verCode", Toast.LENGTH_SHORT).show()

                var i = Intent(this,NumberValidationActivity::class.java)
                i.putExtra("verCode",verCode.toString())
                startActivity(i)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"${error.message}", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                number = "${ccp!!.selectedCountryCode}${editTextPhone.editText!!.text}"
                params["mobile"] = number
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

    fun sendSms(verificationCode:Int): String? {
        var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        return try {
            // Construct data
            val apiKey = "apikey=" + "NzI2NzU5MzM2ZjU5MzY2MjY2NjY3NTcyNGU3YTQ0MzM="
            val message = "&message=Your verification code : $verificationCode"
            val sender = "&sender=" + "Jazara"
            val numbers = "&numbers=" + "+${ccp!!.selectedCountryCode}${editTextPhone.editText!!.text}"
            // Send data

            val conn: HttpURLConnection =
                URL("https://api.txtlocal.com/send/?").openConnection() as HttpURLConnection
            val data: String = "$apiKey  $numbers  $message  $sender"
            conn.doOutput = true
            conn.getOutputStream().write(data.toByteArray(charset("UTF-8")))
            val rd = BufferedReader(InputStreamReader(conn.getInputStream()))
            val stringBuffer = StringBuffer()
            var line: String?
            while (rd.readLine().also { line = it } != null) {
                stringBuffer.append(line)
                Log.e("res",stringBuffer.toString())
            }
            rd.close()



            return stringBuffer.toString()
        } catch (e: Exception) {
            println("Error SMS $e")
            "Error $e"
        }


    }
}