package com.ujjallamichhane.ridesharing.ui.driver

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import com.ujjallamichhane.ridesharing.repository.DriverRepository
import com.ujjallamichhane.ridesharing.ui.driver.driverSettings.DriverSettingsFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DriverProfileFragment : Fragment() {

    private lateinit var imgProfile: CircleImageView
    private lateinit var tvUsername: TextView
    private lateinit var imgBtnUpload: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etGender: EditText
    private lateinit var etLicense: EditText
    private lateinit var etCitizenship: EditText
    private lateinit var appBar: AppBarLayout
    private lateinit var imgBack: ImageView
    private lateinit var btnUpdate: Button

    private var REQUEST_GALLERY_CODE = 0
    private var REQUEST_CAMERA_CODE = 1
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_driver_profile, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        imgBtnUpload = view.findViewById(R.id.imgBtnUpload)
        etFullName = view.findViewById(R.id.etFullName)
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etGender = view.findViewById(R.id.etGender)
        etLicense = view.findViewById(R.id.etLicence)
        etCitizenship = view.findViewById(R.id.etCitizenship)
        appBar = view.findViewById(R.id.appBar)
        imgBack = view.findViewById(R.id.imgBack)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        showUserDetails()

        imgBtnUpload.setOnClickListener{
            loadPopUpMenu()
        }

        btnUpdate.setOnClickListener{
            updateUserDetails()
        }

        imgBack.setOnClickListener {
            val ft = requireView().context as AppCompatActivity
            ft.supportFragmentManager.beginTransaction()
                .replace(R.id.driver_profile, DriverSettingsFragment())
                .addToBackStack(null)
                .commit();
            appBar.visibility = View.GONE
        }

        return view
    }

    private fun showUserDetails(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val driverRepository = DriverRepository()
                val response = driverRepository.getDriverDetails()
                withContext(Dispatchers.Main) {
                    tvUsername.setText(ServiceBuilder.driver!!.username)
                    etFullName.setText(response.driverData!!.fullname)
                    etUsername.setText(response.driverData.username)
                    etEmail.setText(response.driverData.email)
                    etPhone.setText(response.driverData.phone)
                    etGender.setText(response.driverData.gender)
                    etLicense.setText(response.driverData.licence)
                    etCitizenship.setText(response.driverData.citizenship)

                    if (ServiceBuilder.driver!!.photo.equals("")) {
                        Glide.with(requireContext())
                            .load(R.drawable.noimg)
                            .into(imgProfile)
                    }else{
//
                        val imagePath = ServiceBuilder.BASE_URL + response.driverData.photo
                        Glide.with(requireContext())
                            .load(imagePath)
                            .fitCenter()
                            .into(imgProfile)
                    }
                }

            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserDetails(){
        val fullname = etFullName.text.toString()
        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val gender = etGender.text.toString()
        val license = etLicense.text.toString()
        val citizenship = etCitizenship.text.toString()

        val driver = Driver( _id= ServiceBuilder.driver!!._id,fullname = fullname,username = username, email = email, phone = phone,
            gender = gender, licence = license, citizenship = citizenship)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val driverRepository = DriverRepository()
                val response = driverRepository.updateDriver(driver._id!!, driver)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Load pop up menu
    private fun loadPopUpMenu(){
        val popupMenu = PopupMenu(context, imgProfile)
        popupMenu.menuInflater.inflate(R.menu.gallery_camera, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCamera ->
                    openCamera()
                R.id.menuGallery ->
                    openGallery()
            }
            true
        }
        popupMenu.show()
    }

    private fun uploadImage() {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val reqFile =
                RequestBody.create(MediaType.parse("image/jpeg"), file)
            val image =
                MultipartBody.Part.createFormData("photo", file.name, reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val driverRepository = DriverRepository()
                    val response = driverRepository.uploadImage(image)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Hamro Error ", ex.localizedMessage)
                        Toast.makeText(
                            context,
                            ex.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_CODE && data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = activity?.contentResolver
                val cursor =
                    contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imageUrl = cursor.getString(columnIndex)
                imgProfile.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                imgProfile.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                uploadImage()
            }
        }
    }

    private fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? {
        var file: File? = null
        return try {
            file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitMapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitMapData)
            fos.flush()
            fos.close()
            file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    companion object {

    }
}