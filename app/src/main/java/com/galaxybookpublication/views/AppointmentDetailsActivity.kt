package com.galaxybookpublication.views

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.ActivityAppointmentDetailsBinding
import com.galaxybookpublication.models.data.response.AppointmentResponse
import com.galaxybookpublication.models.data.response.ClientAppointmentResponse
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.receivedAmt
import com.galaxybookpublication.viewmodels.AppointmentDetailViewModel
import com.galaxybookpublication.views.MainActivity.Companion.hashMapPaymentAmtTrack
import com.galaxybookpublication.views.dialog.CheckoutDialog
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailsActivity : AppCompatActivity() {
    private lateinit var ClientID: String
    private lateinit var appointmentDetailBinding: ActivityAppointmentDetailsBinding
    private val appointmentViewModel by lazy { ViewModelProvider(this)[AppointmentDetailViewModel::class.java] }
    private lateinit var preference: SharedPreferences
    private lateinit var appointmentPaymentClientDetails: ClientAppointmentResponse.Data
    private lateinit var appointmentSpecimenDetails: AppointmentResponse.Datas.Data

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_appointment_details)
        preference = SharedPreferenceHelper.customPreference(this)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Appointment Form"

        if (intent.getStringExtra("FromPage") == "Payment") {
           /* appointmentPaymentClientDetails = intent.getSerializableExtra("OrderDetails") as ClientAppointmentResponse.Data
            Log.e("appointmentdata",Gson().toJson(appointmentPaymentClientDetails))
            appointmentDetailBinding.txtOrderNo.setText(appointmentPaymentClientDetails.order_number)
            appointmentDetailBinding.txtClientName.setText(intent.getStringExtra("ClientName"))
            appointmentDetailBinding.txtProductType.setText(appointmentPaymentClientDetails.product)
            appointmentDetailBinding.txtTotal.setText(appointmentPaymentClientDetails.total_amount)
            appointmentDetailBinding.txtReceivedAmt.setText(appointmentPaymentClientDetails.received_amount)
            appointmentDetailBinding.txtPendingAmt.setText(appointmentPaymentClientDetails.pending_amount)*/

            ClientID = intent.getStringExtra("ClientID")!!
            appointmentDetailBinding.txtOrderNo.setText(intent.getStringExtra("AppointmentId"))
            appointmentDetailBinding.txtClientName.setText(intent.getStringExtra("ClientName"))
            appointmentDetailBinding.txtProductType.setText(intent.getStringExtra("ClientName"))
            appointmentDetailBinding.txtTotal.setText(intent.getStringExtra("total_amount"))
          //  appointmentDetailBinding.txtReceivedAmt.setText(intent.getStringExtra("ClientName"))
          //  appointmentDetailBinding.txtPendingAmt.setText(intent.getStringExtra("ClientName"))
        } else {
            appointmentDetailBinding.btnSubmit.text = "Checkout"
            appointmentSpecimenDetails =
                intent.getSerializableExtra("OrderDetails") as AppointmentResponse.Datas.Data
            if (intent.getStringExtra("CheckedInStatus") == "Checkedin") {
                appointmentDetailBinding.btnSubmit.visibility = View.VISIBLE
            } else {
                appointmentDetailBinding.btnSubmit.visibility = View.GONE
            }
            appointmentDetailBinding.txtInputOrderNo.visibility = View.GONE
            appointmentDetailBinding.txtInputTotalAmt.visibility = View.GONE
            appointmentDetailBinding.txtInputReceivedAmt.visibility = View.GONE
            appointmentDetailBinding.txtInputPendingAmt.visibility = View.GONE
            appointmentDetailBinding.txtInputCollectAmt.visibility = View.GONE
            appointmentDetailBinding.txtInputPaymentMode.visibility = View.GONE
            appointmentDetailBinding.txtInputPaymentRefId.visibility = View.GONE
            appointmentDetailBinding.txtInputRemarks.visibility = View.GONE
            appointmentDetailBinding.txtClientName.setText(intent.getStringExtra("ClientName"))
            appointmentDetailBinding.txtInputProductType.hint = "Appointment Type"
            appointmentDetailBinding.txtProductType.setText(appointmentSpecimenDetails.type)
        }

        appointmentDetailBinding.txtAppointmentDate.setText(intent.getStringExtra("AppointmentDate"))
        appointmentDetailBinding.txtDistrict.setText(intent.getStringExtra("DistrictName"))
        val appointmentId = intent.getStringExtra("AppointmentId")

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val paidAt_date_time = formatter.format(time)

        var paymentMode = ""

        val stringArray = arrayOf("Cash", "Cheque", "DD", "NEFT", "UPI")
        val arrayAdapter = ArrayAdapter(this, R.layout.item_spinner_text, stringArray)
        appointmentDetailBinding.autoTvPaymentMode.setAdapter(arrayAdapter)
        appointmentDetailBinding.autoTvPaymentMode.setOnItemClickListener { adapterView, view, position, id ->
            paymentMode = stringArray[position]
        }

        appointmentViewModel.checkoutLiveData.observe(this) {
            hideProgress()
            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK, Intent())
            finish()
        }

        appointmentDetailBinding.btnSubmit.setOnClickListener {
            if (intent.getStringExtra("FromPage") == "Payment" && validatePayment()) {

                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_alert)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                val tvQue = dialog.findViewById<TextView>(R.id.tvQue)
                val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                val txtYes = dialog.findViewById<TextView>(R.id.txtYes)
                val txtNo = dialog.findViewById<TextView>(R.id.txtNo)
                val tvEnterAmount = dialog.findViewById<TextView>(R.id.tvEnterAmount)
                val tvPaymentMode = dialog.findViewById<TextView>(R.id.tvPaymentMode)
                val tvPaymentRefID = dialog.findViewById<TextView>(R.id.tvPaymentRefID)


                txtYes.text = "Submit"
                txtNo.text = "Cancel"

                tvEnterAmount.text =  "Amount : "+appointmentDetailBinding.txtCollectedAmt.text.toString()
                tvPaymentMode.text =  "Payment Mode : "+paymentMode
                tvPaymentRefID.text =  "Payment Ref ID : "+appointmentDetailBinding.tvPaymentRefId.text.toString()

                txtYes.setOnClickListener { dialog.dismiss()

                    showProgress()
                    appointmentViewModel.postSubmitOrder(
                        //  if (intent.getStringExtra("FromPage") == "Payment") {
                       /* appointmentPaymentClientDetails.uuid*/
                        ClientID,
                        appointmentId.toString(),
                        appointmentDetailBinding.txtCollectedAmt.text.toString(),
                        paidAt_date_time,
                        paymentMode,
                        appointmentDetailBinding.tvPaymentRefId.text.toString(),
                        appointmentDetailBinding.txtRemarks.text.toString(),
                        "Bearer ${preference.authToken}"
                    )
                }
                txtNo.setOnClickListener { dialog.dismiss() }
                val window = dialog.window
                val wlp = window!!.attributes
                wlp.gravity = Gravity.CENTER
                window!!.attributes = wlp
                dialog.show()

            }
            else {
                val checkoutDialog = CheckoutDialog(
                    this,
                    ClientID,
                    "specimen",
                    object : CheckoutDialog.OnCheckOutListener {

                        override fun proceedCheckout(
                            checkoutType: String,
                            amount: String,
                            review: String,
                            file: File
                        ) {
                            showProgress()
                            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.US)
                                .format(Calendar.getInstance().time)
                            appointmentViewModel.checkout(
                                ClientID!!,
                                currentTime,
                                amount,
                                MainActivity.wayLatitude.toString(),
                                MainActivity.wayLongitude.toString(),
                                review,
                                file,
                                "Bearer ${preference.authToken}"
                            )
                        }
                    })
                checkoutDialog.show(
                    supportFragmentManager,
                    "Checkout Dialog"
                )
            }

        }

        appointmentViewModel.appointmentPostLiveData.observe(this, Observer {
            hideProgress()
            var enteredAmt: Int = appointmentDetailBinding.txtCollectedAmt.text.toString().toInt()
            Toast.makeText(baseContext, it.message, Toast.LENGTH_SHORT).show()
            if (intent.getStringExtra("FromPage") == "Payment") {
                if (hashMapPaymentAmtTrack.containsKey(ClientID)) {
                    val totalPaidAmt =
                        hashMapPaymentAmtTrack.get(ClientID) ?: 0
                    enteredAmt += totalPaidAmt
                }
                hashMapPaymentAmtTrack[ClientID] = enteredAmt
            }
            val gson = Gson()
            val hashmapString = gson.toJson(hashMapPaymentAmtTrack)
            preference.receivedAmt = hashmapString
            finish()
        })
    }

    private fun showProgress() {
        appointmentDetailBinding.clProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        appointmentDetailBinding.clProgress.visibility = View.GONE
    }

    private fun validatePayment(): Boolean {
        val valid = if (TextUtils.isEmpty(appointmentDetailBinding.txtCollectedAmt.text.toString().trim())) {
            Toast.makeText(this, "Enter the Collected Amount", Toast.LENGTH_SHORT).show()
            false
        } else if (TextUtils.isEmpty(appointmentDetailBinding.autoTvPaymentMode.text.toString().trim())) {
            Toast.makeText(this, "Select payment type", Toast.LENGTH_SHORT).show()
            false
        } else if (TextUtils.isEmpty(appointmentDetailBinding.tvPaymentRefId.text.toString().trim())) {
            Toast.makeText(this, "Enter payment reference id", Toast.LENGTH_SHORT).show()
            false
        } else if (TextUtils.isEmpty(appointmentDetailBinding.txtRemarks.text.toString().trim())) {
            Toast.makeText(this, "Enter the remarks", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
        return valid
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }



}