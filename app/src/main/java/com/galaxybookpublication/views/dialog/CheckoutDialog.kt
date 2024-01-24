package com.galaxybookpublication.views.dialog

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.DialogCheckoutBinding
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.receivedAmt
import com.galaxybookpublication.util.Cache
import com.galaxybookpublication.viewmodels.CheckoutDialogViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class CheckoutDialog(
    context: Context,
    appointmentId: String,
    appointmentType: String,
    listener: OnCheckOutListener
) :
    DialogFragment() {
    val _context = context
    val _appointmentId = appointmentId
    val _appointmentType = appointmentType
    private lateinit var checkoutBinding: DialogCheckoutBinding
    private val checkDialogViewModel by lazy { ViewModelProvider(this)[CheckoutDialogViewModel::class.java] }
    private lateinit var preference: SharedPreferences
    var file: File? = null
    var dialogListener = listener
    var totalValues = 0
    var fileName = ""
    var tempFile : File ?= null

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.CAMERA
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        checkoutBinding = DialogCheckoutBinding.inflate(inflater, container, false)
        return checkoutBinding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun validation(): Boolean {
        val validation: Boolean
        if (TextUtils.isEmpty(checkoutBinding.tvReview.text)) {
            validation = false
            Toast.makeText(_context, "Enter Visiting Review", Toast.LENGTH_SHORT).show()
        } else if (file == null) {
            validation = false
            Toast.makeText(_context, "Upload client place image", Toast.LENGTH_SHORT).show()
        } else {
            validation = true
        }
        return validation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreferenceHelper.customPreference(_context)
        val gson = Gson()
        val receivedAmtString = preference.receivedAmt
        if(!receivedAmtString.isNullOrEmpty()){
            val type = object : TypeToken<HashMap<String, Int>>() {}.type
            val appointmentReceivedAmtHashMap: HashMap<String, Int> =
                gson.fromJson(receivedAmtString, type)

            totalValues = 0
            for (value in appointmentReceivedAmtHashMap.values) {
                totalValues += value
            }
        }

        checkoutBinding.tvTotalAmtReceived.text =
            String.format(getString(R.string.received_amt), totalValues)

        checkoutBinding.imgVisitedProof.setOnClickListener {
            checkPermissionRequired()
        }

        checkoutBinding.btnCheckout.setOnClickListener {
            if (validation()) {
                dismissAllowingStateLoss()
                if (_appointmentType == "specimen") {
                    dialogListener.proceedCheckout(_appointmentType,"0",checkoutBinding.tvReview.text.toString(),
                        file!!)
                } else {
                    dialogListener.proceedCheckout(_appointmentType,totalValues.toString(),checkoutBinding.tvReview.text.toString(),
                        file!!)
                }
            }
        }

        checkDialogViewModel.checkoutLiveData.observe(this, Observer {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
            if (_appointmentType == "specimen") {
                requireActivity().finish()
            } /*else {
                findNavController().navigateUp()
            }*/
        })
    }

    private fun hasPermissions(context: Context, permission: Array<String>): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager() && permission.all {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            permission.all {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e("RESULT ", result.resultCode.toString())
            if (result.resultCode == Activity.RESULT_OK) {
//                val imageBitMap = result.data?.extras?.get("data")
//                Glide.with(requireActivity())
//                    .load(imageBitMap)
//                    .circleCrop()
//                    .into(checkoutBinding.imgVisitedProof)
//                Log.e("DATA BITMAP", imageBitMap.toString())
//                file = GalaxyBookPublicationUtil().saveBitmap(imageBitMap as Bitmap)
//                Log.e("DATA BITMAP FILE", file.toString())

                if (getPickImageResultUri(result.data) != null) {
                    val picUri = getPickImageResultUri(result.data)
                    Glide.with(requireActivity())
                        .load(picUri)
                        .circleCrop()
                        .into(checkoutBinding.imgVisitedProof)
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            file = tempFile!!
                            Log.e("filePath",file?.path?:"")
                        }
                    }
                } else {
                    val imageBitMap = result.data?.extras?.get("data")
                    Glide.with(requireActivity())
                        .load(imageBitMap)
                        .circleCrop()
                        .into(checkoutBinding.imgVisitedProof)
                    val fileUri = Cache(requireContext()).saveToCacheAndGetUri(imageBitMap as Bitmap)
                    file = fileUri.toFile()
                }
            }
        }

    /**
     * Get the URI of the selected image from [.getPickImageChooserIntent].<br></br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            if(action != null)
                isCamera = action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Permission required")
        builder.setMessage("Some permissions are needed to be allowed to use this app without any problems.")
        builder.setPositiveButton("Grant") { dialog, which ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()
            ) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }*/
            if (!granted) {
                showPermissionDialog()
            }
        }

    private fun checkPermissionRequired() {
        if (hasPermissions(requireActivity(), PERMISSIONS)) {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            activityResultLauncher.launch(cameraIntent)
            // Determine Uri of camera image to save.
            fileName = System.currentTimeMillis().toString()+".png"
            val outputFileUri = getCaptureImageOutputUri()
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (outputFileUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            activityResultLauncher.launch(captureIntent)
        } else {
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        try {
            val getImage: File? = requireActivity().cacheDir
            if (getImage != null) {
                tempFile = File(getImage.path, fileName)

                outputFileUri = FileProvider.getUriForFile(
                    requireContext(), "${requireContext().packageName}.provider",
                    tempFile!!
                )
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return outputFileUri
    }

    interface OnCheckOutListener {
        fun proceedCheckout(checkoutType: String, amount: String, review: String, file: File) //or whatever args you want
    }
}