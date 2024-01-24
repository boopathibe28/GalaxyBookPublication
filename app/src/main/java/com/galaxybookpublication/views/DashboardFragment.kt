package com.galaxybookpublication.views

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.galaxybookpublication.R
import com.galaxybookpublication.activity.SpecimenCreateActivity
import com.galaxybookpublication.api.CommonFunctions
import com.galaxybookpublication.api.CommonFunctions.CheckStatus
import com.galaxybookpublication.databinding.FragmentDashboardBinding
import com.galaxybookpublication.models.interfaces.ServiceCallBacks
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.isTimerStarted
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.lastNoticedTime
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.startTimer
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.stopTimeMillSec
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.userName
import com.galaxybookpublication.util.OnCheckInListener
import com.galaxybookpublication.util.StopwatchService
import com.galaxybookpublication.util.StopwatchService.Companion.IS_ACTIVITY_RUNNING
import com.galaxybookpublication.viewmodels.DashboardViewModel
import com.galaxybookpublication.views.MainActivity.Companion.wayLatitude
import com.galaxybookpublication.views.MainActivity.Companion.wayLongitude
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class DashboardFragment : Fragment(), ServiceCallBacks {
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
    var uploadImage: File? = null
    //variable declaration
    private lateinit var dashboardBinding: FragmentDashboardBinding
    private lateinit var preference: SharedPreferences
    private lateinit var serviceIntent: Intent
    private val dashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().registerReceiver(updateTime, IntentFilter(StopwatchService.TIMER_UPDATED))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)
        return dashboardBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreferenceHelper.customPreference(requireContext())
        serviceIntent = Intent(requireContext(), StopwatchService::class.java)

        CommonFunctions.Auth = "Bearer ${preference.authToken}"
        dashboardBinding.tvWelcomeName.text =
            resources.getString(R.string.welcome_user, preference.userName)

        dashboardViewModel.todayLog("Bearer ${preference.authToken}")
        dashboardViewModel.totalPendingAppointments(authToken = "Bearer ${preference.authToken}")

        checkAndRequestPermissions(activity)

        setCheckInCheckOutButtonClickListener()
        timerHandler()
        setLiveDataObserver()
        (activity as MainActivity).setOnCheckInListener(object : OnCheckInListener
        {
            override fun onCheckIn() {
                dismissLoader()
                setButtonVisibility(View.GONE, View.VISIBLE)
                startTimerCallAPI(isCheckIn = true)
            }

            override fun onCheckOut() {
                dismissLoader()
                setButtonVisibility(View.VISIBLE, View.GONE)
                dashboardBinding.tvTimeRunned.text = dashboardBinding.timeView.text
                dashboardBinding.timeView.text = "00:00:00"
                startTimerCallAPI(isCheckIn = false)

            }

            override fun dismissLoader() {
                dismissProgress()
                timerHandler()
            }

            override fun showLoader() {
                showProgress()
            }
        })
    }

    private fun showProgress() {
        dashboardBinding.btnCheckIn.visibility = View.GONE
        dashboardBinding.btnCheckOut.visibility = View.GONE
        dashboardBinding.pbProgress.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        dashboardBinding.pbProgress.visibility = View.GONE
    }

    private fun timerHandler() {
        if (preference.startTimer) {
            dashboardBinding.btnCheckOut.visibility = View.VISIBLE
            dashboardBinding.btnCheckIn.visibility = View.GONE
        } else {
            dashboardBinding.btnCheckIn.visibility = View.VISIBLE
            dashboardBinding.btnCheckOut.visibility = View.GONE
        }
    }

    private fun setLiveDataObserver() {
        dashboardViewModel.dashboardTodayAttendanceLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null && it.success && it.data.isNotEmpty() && preference.startTimer) {

            }
        })

        dashboardViewModel.dashboardLiveData.observe(viewLifecycleOwner) {
            if (it != null && it.success) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                dashboardViewModel.todayLog("Bearer ${preference.authToken}")
            }
            else{
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }


        dashboardViewModel.dashboardLiveDataError.observe(viewLifecycleOwner) {
            if (it != null) {
                stopTimer()
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        dashboardViewModel.dashboardAppointmentsLiveData.observe(viewLifecycleOwner) {
            if (it != null && it.success) {
                dashboardBinding.tvVisitors.text = "${it.datas.data.size} upcoming Appointments"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (preference.lastNoticedTime != 0) {
            preference.isTimerStarted = true
            startTimer()
        }
    }

    private fun setCheckInCheckOutButtonClickListener() {
        dashboardBinding.btnCheckIn.setOnClickListener {

          //  Toast.makeText(activity,"CheckIn Click",Toast.LENGTH_SHORT).show()

               /* if (checkAndRequestPermissions(requireActivity())) {
                    chooseImage(requireActivity())
                }*/
            chooseImage(requireActivity())
            CheckStatus = "true"

            //(activity as MainActivity).startCheckIn(isCheckIn = true)
        }

        dashboardBinding.btnCheckOut.setOnClickListener {

                /*if (checkAndRequestPermissions(requireActivity())) {
                    chooseImage(requireActivity())
                }*/
            chooseImage(requireActivity())
            CheckStatus = "false"
           // (activity as MainActivity).startCheckIn(isCheckIn = false)
        }
    }

    private fun startTimerCallAPI(isCheckIn: Boolean) {
        val type = if(isCheckIn) {
            "Checkin"
        } else {
            "Checkout"
        }

        if(isCheckIn) {
            startTimer()
        } else {
            stopTimer()
        }

        uploadImage?.let {
            dashboardViewModel.markAttendance(
                wayLatitude.toString(),
                wayLongitude.toString(),
                type, it,
                "Bearer ${preference.authToken}"
            )
        }
    }

    private fun setButtonVisibility(checkInVisibility: Int, checkOutVisibility: Int) {
        dashboardBinding.btnCheckIn.visibility = checkInVisibility
        dashboardBinding.btnCheckOut.visibility = checkOutVisibility
        if (checkInVisibility == View.GONE) {
            dashboardBinding.tvTimeRunned.visibility = View.INVISIBLE
        } else {
            dashboardBinding.tvTimeRunned.visibility = checkInVisibility
        }
    }

    private fun startTimer() {
        if (!IS_ACTIVITY_RUNNING) {
            requireActivity().startService(serviceIntent)
            (activity as MainActivity).checkPermissionRequired()
            preference.isTimerStarted = true
            preference.startTimer = true
        }
    }

    private fun stopTimer() {
        requireActivity().stopService(serviceIntent)
        preference.lastNoticedTime = 0
        preference.startTimer = false
        preference.stopTimeMillSec = 0
        preference.isTimerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // timeStarted = true
            time = intent.getDoubleExtra(StopwatchService.TIMER_EXTRA, 0.0)
            preference.lastNoticedTime = time.roundToInt()
            preference.stopTimeMillSec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
            dashboardBinding.timeView.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hour, min, sec)

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(updateTime)
    }

    override fun onStopService(activity: MainActivity) {
        serviceIntent = Intent(activity, StopwatchService::class.java)
        activity.stopService(serviceIntent)
        activity.preference.lastNoticedTime = 0
        activity.preference.startTimer = false
        activity.preference.stopTimeMillSec = 0
        activity.preference.isTimerStarted = false
    }


    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Exit"
        ) // create a menuOption Array

        // create a dialog for showing the optionsMenu
        val builder = AlertDialog.Builder(context)

        // set the items in builder
        builder.setItems(
            optionsMenu
        ) { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {

                // Open the camera and get the photo
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (optionsMenu[i] == "Choose from Gallery") {

                // choose from  external storage
              /*  val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)*/
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }


    // function to check permission
    fun checkAndRequestPermissions(context: Activity?): Boolean {
        val WExtstorePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(), listPermissionsNeeded
                    .toTypedArray(),
                SpecimenCreateActivity.REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    // Handled permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        when (requestCode) {
            SpecimenCreateActivity.REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    requireActivity(),
                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    requireActivity(),
                    "FlagUp Requires Access to Your Storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                chooseImage(requireActivity())
            }
        }
    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                  //  binding.imgPic.setImageBitmap(selectedImage)
                    persistImage(selectedImage, data.dataString)
                }

                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = requireActivity().getContentResolver().query(
                            selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath = cursor.getString(columnIndex)
                           // binding.imgPic.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                            cursor.close()
                            persistImage(BitmapFactory.decodeFile(picturePath), picturePath)
                        }
                    }
                }
            }
        }
    }


    private fun persistImage(bitmap: Bitmap?, name: String?) {
        val filesDir: File = requireActivity().getFilesDir()
        val imageFile = File(filesDir, "Test" + ".jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
            uploadImage = imageFile

            if (CheckStatus.equals("true")) {
                CheckStatus = "";
                (activity as MainActivity).startCheckIn(isCheckIn = true)
            }
            else{
                CheckStatus = "";
                (activity as MainActivity).startCheckIn(isCheckIn = false)
            }
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
    }

}