package com.galaxybookpublication.views

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.FragmentProfileBinding
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.address
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.dob
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.emaiId
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.emergencyContact
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.gender
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.phoneNo
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.userName
import com.galaxybookpublication.util.GalaxyBookPublicationUtil
import com.galaxybookpublication.viewmodels.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random


class ProfileFragment : Fragment() {
    private var _profileBinding: FragmentProfileBinding? = null

    private val profileBinding get() = _profileBinding!!
    private lateinit var preference: SharedPreferences
    private val profileViewModel by lazy { ViewModelProvider(this)[ProfileViewModel::class.java] }
    var file: File? = null

    companion object {
        var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.CAMERA
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        preference = SharedPreferenceHelper.customPreference(requireContext())
        profileViewModel.getProfile(authToken = preference.authToken.toString())

        profileViewModel.profileLiveData.observe(viewLifecycleOwner) {
            profileBinding.progressBar.visibility = View.GONE
            if (it != null && it.success) {
                Glide.with(requireActivity())
                    .load(it.data.profileImg)
                    .circleCrop()
                    .placeholder(R.drawable.user_small)
                    .into(profileBinding.imgProfile)
                profileBinding.etvUserName.setText(it.data.name)
                profileBinding.etvMailId.setText(it.data.email)
                profileBinding.etvPhoneNo.setText(it.data.phoneNumber)
                profileBinding.etvAddress.setText(it.data.address)
                profileBinding.etvEmergencyContact.setText(it.data.emergencyContact)
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                if (profileBinding.btnUpload.visibility == View.VISIBLE) {
                    profileBinding.btnUpload.visibility = View.GONE
                }
            }
        }

        profileViewModel.profileUpdateErrorLiveData.observe(viewLifecycleOwner) {
            profileBinding.progressBar.visibility = View.GONE
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        profileViewModel.profileUpdateLiveData.observe(viewLifecycleOwner) {
            profileBinding.progressBar.visibility = View.GONE
            if (it.success) {
                profileBinding.etvUserName.setText(it.data.name)
                profileBinding.etvMailId.setText(it.data.email)
                profileBinding.etvPhoneNo.setText(it.data.phoneNumber)
                profileBinding.etvAddress.setText(it.data.address)
                profileBinding.etvEmergencyContact.setText(it.data.emergencyContact)
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            profileBinding.btnSave.visibility = View.GONE
            profileBinding.etvUserName.isEnabled = false
            profileBinding.etvMailId.isEnabled = false
            profileBinding.etvPhoneNo.isEnabled = false
            profileBinding.etvAddress.isEnabled = false
            profileBinding.etvEmergencyContact.isEnabled = false
        }

        profileBinding.imgProfile.setOnClickListener {
            checkPermissionRequired()
        }

        profileBinding.tvChangePassword.setOnClickListener {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        profileBinding.btnSave.setOnClickListener {
            profileBinding.progressBar.visibility = View.VISIBLE
            profileViewModel.updateProfile(
                "Bearer ${preference.authToken}",
                preference.userName.toString(),
                preference.emaiId.toString(),
                preference.phoneNo.toString(),
                preference.address.toString(),
                preference.dob.toString(),
                preference.gender.toString(),
                preference.emergencyContact.toString()
            )
        }

        profileBinding.btnUpload.setOnClickListener {
            if (file != null) {
                profileBinding.progressBar.visibility = View.VISIBLE
                profileViewModel.uploadImage(
                    "Bearer ${preference.authToken}", file!!
                )
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        (menuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.edit_menu -> {
                        profileBinding.btnSave.visibility = View.VISIBLE
                        profileBinding.etvUserName.isEnabled = true
                        profileBinding.etvMailId.isEnabled = true
                        profileBinding.etvPhoneNo.isEnabled = true
                        profileBinding.etvAddress.isEnabled = true
                        profileBinding.etvEmergencyContact.isEnabled = true
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj =
                arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            cursor!!.moveToFirst()
            val column_index = cursor.getColumnIndex(proj[0])
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
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
                val imageUri = result.data?.data
                if (imageUri != null) {
                    Glide.with(requireActivity())
                        .load(imageUri)
                        .circleCrop()
                        .into(profileBinding.imgProfile)

                    file = getRealPathFromURI(requireContext(), imageUri)?.let { File(it) }
                    // profileBinding.imgProfile.setImageURI(imageUri)
                    Log.e("DATA URI", imageUri.toString())
                    Log.e("DATA BITMAP FILE", file.toString())
                } else {
                    val imageBitMap = result.data?.extras?.get("data") as Bitmap
                    Glide.with(requireActivity())
                        .load(imageBitMap)
                        .circleCrop()
                        .into(profileBinding.imgProfile)
                    //profileBinding.imgProfile.setImageBitmap(imageBitMap as Bitmap)
                    Log.e("DATA BITMAP", imageBitMap.toString())
                    file = GalaxyBookPublicationUtil().saveBitmap(imageBitMap as Bitmap)
                    Log.e("DATA BITMAP FILE", file.toString())
                }
            }
        }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (!granted) {
                showPermissionDialog()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && permissions[Manifest.permission.MANAGE_EXTERNAL_STORAGE] == false
                && !Environment.isExternalStorageManager()
            ) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
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

    private fun checkPermissionRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (hasPermissions(requireActivity(), PERMISSIONS)) {
                profileBinding.btnUpload.visibility = View.VISIBLE
                val galleryIntent = Intent().apply {
                    action = Intent.ACTION_PICK
                    type = "image/*"
                }
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val chooserIntent = Intent.createChooser(galleryIntent, "Select")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                activityResultLauncher.launch(chooserIntent)
            } else {
                permReqLauncher.launch(PERMISSIONS)
            }
        } else {
            if (hasPermissions(requireActivity(), PERMISSIONS)) {
                profileBinding.btnUpload.visibility = View.VISIBLE
                val galleryIntent = Intent().apply {
                    action = Intent.ACTION_PICK
                    type = "image/*"
                }
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val chooserIntent = Intent.createChooser(galleryIntent, "Select")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
                activityResultLauncher.launch(chooserIntent)
            } else {
                permReqLauncher.launch(PERMISSIONS)
            }
        }
    }

    private fun createFolder() {
        val folderName = "galaxyBookPublication"
        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + folderName
        )
        val folderCreated = file.mkdir()
        if (folderCreated) {
            Toast.makeText(context, "Folder created.....\n" + file.absolutePath, Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(context, "Folder not created....", Toast.LENGTH_SHORT).show()
        }
    }
}