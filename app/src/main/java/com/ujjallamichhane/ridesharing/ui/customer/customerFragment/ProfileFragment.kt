package com.ujjallamichhane.ridesharing.ui.customer.customerFragment

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
import com.bumptech.glide.Glide
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
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

class ProfileFragment : Fragment() {
    private lateinit var imgProfileCustomer: CircleImageView
    private lateinit var imgBtnUpload: ImageView
    private lateinit var etCustomerFullname: EditText
    private lateinit var etCustomerEmail: EditText
    private lateinit var etCustomerPhone: EditText
    private lateinit var etCustomerGender: EditText
    private lateinit var btnUpdateCProfile: Button

    private var REQUEST_GALLERY_CODE = 0
    private var REQUEST_CAMERA_CODE = 1
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
            imgProfileCustomer = view.findViewById(R.id.imgProfileCustomer)
            imgBtnUpload= view.findViewById(R.id.imgBtnUpload)
            etCustomerFullname= view.findViewById(R.id.etCustomerFullname)
            etCustomerEmail= view.findViewById(R.id.etCustomerEmail)
            etCustomerPhone= view.findViewById(R.id.etCustomerPhone)
            etCustomerGender= view.findViewById(R.id.etCustomerGender)
            btnUpdateCProfile= view.findViewById(R.id.btnUpdateCProfile)

            customerDetails()
        return view
    }

    private fun customerDetails(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val customerRepository = CustomerRepository()
                val response = customerRepository.getCustomerDetails()
                withContext(Dispatchers.Main) {
                    etCustomerFullname.setText(response.data!!.fullname)
                    etCustomerEmail.setText(response.data.email)
                    etCustomerPhone.setText(response.data.contact)
                    etCustomerGender.setText(response.data.gender)

//                    val imagePath = ServiceBuilder.BASE_URL + response.data.photo
//
//                    Glide.with(requireContext())
//                        .load(imagePath)
//                        .fitCenter()
//                        .into(imgProfile)
                }


            } catch (ex: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                imgProfileCustomer.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                imgProfileCustomer.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                uploadImage()
            }
        }
    }
    private fun uploadImage() {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val reqFile =
                RequestBody.create(MediaType.parse("image/jpeg"), file)
            val image =
                MultipartBody.Part.createFormData("photo", file.name, reqFile)
//           val userId = ServiceBuilder.userId!!
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val customerRepository = CustomerRepository()
                    val response = customerRepository.uploadImage(image)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Mero Error ", ex.localizedMessage)
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