package com.galaxybookpublication.views

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.galaxybookpublication.api.CommonFunctions.isFromAppointments
import com.galaxybookpublication.databinding.FragmentClientBinding
import com.galaxybookpublication.models.data.response.ClientAppointmentResponse
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.viewmodels.ClientAppointmentViewModel
import com.galaxybookpublication.views.AppointmentFragment.Companion.orderCheckin
import com.galaxybookpublication.views.adapters.ClientAppointmentAdapter
import com.galaxybookpublication.views.dialog.CheckoutDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ClientAppointmentListFragment : Fragment() {
    private lateinit var clientAppointmentBinding: FragmentClientBinding
    private val clientAppointmentViewModel by lazy { ViewModelProvider(this)[ClientAppointmentViewModel::class.java] }
    private lateinit var preference: SharedPreferences
    var clientAppointmentAdapter: ClientAppointmentAdapter? = null
    var clientName = ""
    var clientId = ""
    var appointmentDate = ""
    var districtName = ""
    var appointmentId = ""
    var checkin = ""
    var checkout = ""
    var appointmentStatus = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        clientAppointmentBinding = FragmentClientBinding.inflate(inflater, container, false)
        return clientAppointmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preference = SharedPreferenceHelper.customPreference(requireContext())
        clientAppointmentBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        clientAppointmentBinding.recyclerView.smoothScrollToPosition(0);
        clientName = arguments?.getString("clientName").toString()
        appointmentDate = arguments?.getString("appointmentDate").toString()
        districtName = arguments?.getString("districtName").toString()
        appointmentId = arguments?.getString("appointmentId").toString()
        checkin = arguments?.getString("checkin").toString()
        checkout = arguments?.getString("checkout").toString()
        appointmentStatus = arguments?.getString("appointmentStatus").toString()
        clientId = arguments?.getString("clientId").toString()

        if (checkin != null && !checkin.equals("null") && !checkin.isEmpty()){
            clientAppointmentBinding.btnCheckIn.visibility = View.GONE
        }

        clientAppointmentViewModel.checkoutLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            MainActivity.hashMapPaymentAmtTrack = hashMapOf()
            clientAppointmentViewModel.getClientAppointmentDetails(
                "Pending", arguments?.getString("clientId")!!, "Bearer ${preference.authToken}")
            clientAppointmentBinding.progressBar.visibility = View.GONE
            isFromAppointments = "true"
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startForResult.launch(intent)
        }

       /* clientAppointmentViewModel.appointmentClientLiveData.observe(viewLifecycleOwner) {
            clientAppointmentBinding.progressBar.visibility = View.GONE
            if (it.data.isNotEmpty()) {
                val clientList = it.data.filter { it.pending_amount != "0" }
                updateButtonStatus(clientList)
                if (clientAppointmentAdapter != null) {
                    clientAppointmentAdapter!!.setItems(it.data)
                }
                else {
                    clientAppointmentAdapter = ClientAppointmentAdapter(requireContext(), it.data)
                    clientAppointmentBinding.recyclerView.adapter = clientAppointmentAdapter
                    clientAppointmentAdapter!!.setOnItemClickListener(object :
                        ClientAppointmentAdapter.OnItemClickListener {
                        override fun onItemClick(
                            position: Int,
                            appointmentClientList: List<ClientAppointmentResponse.Data>
                        ) {
                            if (clientAppointmentBinding.btnCheckIn.visibility == View.VISIBLE) {
                                Toast.makeText(context, "Please checkin and proceed", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                if (appointmentClientList[position].payment_status != "Paid") {
                                    val intent = Intent(context, AppointmentDetailsActivity::class.java)
                                    intent.putExtra("OrderDetails", appointmentClientList[position])
                                    intent.putExtra("ClientName", clientName)
                                    intent.putExtra("AppointmentDate", appointmentDate)
                                    intent.putExtra("AppointmentId", appointmentId)
                                    intent.putExtra("DistrictName", districtName)
                                    intent.putExtra("ClientID", clientId)
                                    intent.putExtra("FromPage", "Payment")
                                  //  startForResult.launch(intent)
                                    startActivity(intent)
                                }
                                else {
                                    Toast.makeText(context, "The Order is completed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    })
                }
            } else {
                clientAppointmentBinding.tvNoOrders.visibility = View.VISIBLE
                clientAppointmentBinding.btnCheckOut.visibility = View.GONE
                clientAppointmentBinding.btnCheckIn.visibility = View.GONE
            }
        }*/

        clientAppointmentBinding.btnAppointmentForm.setOnClickListener {
            if (clientAppointmentBinding.btnCheckIn.visibility == View.VISIBLE) {
                Toast.makeText(context, "Please checkin and proceed", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(context, AppointmentDetailsActivity::class.java)
                // intent.putExtra("OrderDetails", appointmentClientList[position])
                intent.putExtra("ClientName", clientName)
                intent.putExtra("AppointmentDate", appointmentDate)
                intent.putExtra("AppointmentId", appointmentId)
                intent.putExtra("DistrictName", districtName)
                intent.putExtra("ClientID", clientId)
                intent.putExtra("FromPage", "Payment")
                // startForResult.launch(intent)
                startActivity(intent)
            }
        }

        clientAppointmentBinding.btnCheckOut.setOnClickListener {
            val checkoutDialog = CheckoutDialog(
                requireContext(),
                appointmentId,
                "payment",
                object : CheckoutDialog.OnCheckOutListener {

                    override fun proceedCheckout(
                        checkoutType: String,
                        amount: String,
                        review: String,
                        file: File
                    ) {
                        clientAppointmentBinding.progressBar.visibility = View.VISIBLE
                        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.US)
                            .format(Calendar.getInstance().time)
                        clientAppointmentViewModel.checkout(
                            appointmentId,
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
                childFragmentManager,
                "Checkout Dialog"
            )
        }

        clientAppointmentBinding.btnCheckIn.setOnClickListener {
            orderCheckin = true
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("HH:mm:ss",Locale.US)
            val currentTime = formatter.format(time)

            val formatterDate = SimpleDateFormat("yyyy-MM-dd",Locale.US)
            val date = Date()
            val currentDate = formatterDate.format(date)

            clientAppointmentViewModel.checkInAppointment(
                appointmentId,
                currentTime,
                MainActivity.wayLatitude.toString(),
                MainActivity.wayLongitude.toString(),
                currentDate,
                "Bearer ${preference.authToken}"
            )
        }

        clientAppointmentViewModel.appointmentCheckInLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if (it.success) {
                    orderCheckin = false
                    checkin = it.data.checkin
                    Toast.makeText(context, "CheckedIn to Client location", Toast.LENGTH_SHORT)
                        .show()
                    clientAppointmentBinding.btnCheckIn.visibility = View.GONE


                    val intent = Intent(context, AppointmentDetailsActivity::class.java)
                   // intent.putExtra("OrderDetails", appointmentClientList[position])
                    intent.putExtra("ClientName", clientName)
                    intent.putExtra("AppointmentDate", appointmentDate)
                    intent.putExtra("AppointmentId", appointmentId)
                    intent.putExtra("DistrictName", districtName)
                    intent.putExtra("ClientID", clientId)
                    intent.putExtra("FromPage", "Payment")
                   // startForResult.launch(intent)
                    startActivity(intent)
                }
            })
    }

    private fun updateButtonStatus(clientList: List<ClientAppointmentResponse.Data>) {
        if(clientList.isNotEmpty()) {
            if (appointmentStatus == "Checkedin") {
                clientAppointmentBinding.btnCheckIn.visibility = View.GONE
                clientAppointmentBinding.btnCheckOut.visibility = View.VISIBLE
            } else if ((checkin.isNotEmpty() || checkin != "null") && (checkout.isNotEmpty() || checkout != "null") && clientList.isNotEmpty() && orderCheckin) {
                clientAppointmentBinding.btnCheckIn.visibility = View.GONE
                clientAppointmentBinding.btnCheckOut.visibility = View.VISIBLE
            } else if ((checkin.isNotEmpty() || checkin != "null") && (checkout.isNotEmpty() || checkout != "null") && clientList.isNotEmpty()) {
                clientAppointmentBinding.btnCheckIn.visibility = View.VISIBLE
                clientAppointmentBinding.btnCheckOut.visibility = View.VISIBLE
            } else if ((checkout.isEmpty() || checkout == "null") && (checkin.isNotEmpty() || checkin != "null")) {
                clientAppointmentBinding.btnCheckIn.visibility = View.GONE
                clientAppointmentBinding.btnCheckOut.visibility = View.VISIBLE
            } else if (((checkout.isEmpty() || checkout == "null") && (checkin.isEmpty() || checkin == "null"))) {
                clientAppointmentBinding.btnCheckIn.visibility = View.VISIBLE
                clientAppointmentBinding.btnCheckOut.visibility = View.VISIBLE
            } else {
                clientAppointmentBinding.btnCheckIn.visibility = View.GONE
                clientAppointmentBinding.btnCheckOut.visibility = View.GONE
            }
        } else {
            clientAppointmentBinding.btnCheckIn.visibility = View.GONE
            clientAppointmentBinding.btnCheckOut.visibility = View.GONE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
       // clientAppointmentBinding.progressBar.visibility = View.VISIBLE
        clientAppointmentBinding.progressBar.visibility = View.GONE
        clientAppointmentViewModel.getClientAppointmentDetails(
            "Pending",
            clientId,
            "Bearer ${preference.authToken}"
        )
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

}