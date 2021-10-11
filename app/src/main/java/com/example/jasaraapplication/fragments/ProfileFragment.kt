package com.example.jasaraapplication.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.FormActivity
import com.squareup.picasso.Picasso
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import kotlinx.android.synthetic.main.activity_check_number.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.io.ByteArrayOutputStream
import java.io.InputStream


class ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_profile, container, false)

        var cPrefs = requireActivity().getSharedPreferences("profilePref",
            Context.MODE_PRIVATE
        )


        val name = cPrefs.getString("name", null)
        val mobile = cPrefs.getString("mobile", null)
        val email = cPrefs.getString("email", null)
        val img = cPrefs.getString("img", null)

        if (img!=null){
            Picasso.get().load(img).error(R.drawable.meats).into(root.img_profile)
        }
        root.tvUserName.setText(name)
        root.tvMobileNum.setText(mobile)
        root.tvEmailAddress.setText(email)


        root.imageIcon.setOnClickListener {
            PickImageDialog.build(PickSetup())
                .setOnPickResult { r ->
                    val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(r.uri)
                    val image = BitmapFactory.decodeStream(inputStream)
                    sendImage(image)
                    root.img_profile.setImageBitmap(r.bitmap)
                    // uploadImage(r.uri)
                }
                .setOnPickCancel{
                }.show(requireActivity().supportFragmentManager)
        }


        root.editProfileLinear.setOnClickListener {

            var i= Intent(requireContext(), FormActivity::class.java)
            startActivity(i)
            requireActivity().finish()


//            if ( root.tvEditProfile.text == "تعديل الملف الشخصي"){
//                root.tvUserName.isEnabled = true
//                root.tvMobileNum.isEnabled = true
//                root.tvEmailAddress.isEnabled = true
//
//                root.editProfileBtn.visibility = View.VISIBLE
//                root.imageIcon.visibility = View.GONE
//                root.tvEditProfile.text = "حفظ التغيير"
//
//            }else{
//                root.tvUserName.isEnabled = false
//                root.tvMobileNum.isEnabled = false
//                root.tvEmailAddress.isEnabled = false
//
//                root.editProfileBtn.visibility = View.GONE
//                root.imageIcon.visibility = View.VISIBLE
//                root.tvEditProfile.text = "تعديل الملف الشخصي"
//
//            }

        }

        root.fromProfile_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                MoreFragment()
            ).commit()
        }

        return root
    }


    private fun sendImage(image: Bitmap?){
        val outputStream = ByteArrayOutputStream()
        image!!.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        var base64String = android.util.Base64.encodeToString(outputStream.toByteArray(),
            android.util.Base64.DEFAULT)
        val editor: SharedPreferences.Editor =
            requireActivity().getSharedPreferences("imagepref", Context.MODE_PRIVATE).edit()

        editor.putString("img",base64String)
        editor.putString("fromProfile","yes")
        editor.apply()



    }



}