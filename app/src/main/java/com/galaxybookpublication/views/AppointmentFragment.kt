package com.galaxybookpublication.views

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.galaxybookpublication.databinding.FragmentAppointmentBinding
import com.galaxybookpublication.models.AppointmentParentData
import com.galaxybookpublication.models.data.response.AppointmentResponse
import com.galaxybookpublication.models.data.response.OrderCheckedInResponse
import com.galaxybookpublication.models.repo.SharedPreferenceHelper
import com.galaxybookpublication.models.repo.SharedPreferenceHelper.authToken
import com.galaxybookpublication.viewmodels.AppointmentViewModel
import com.galaxybookpublication.views.adapters.AppointmentAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppointmentFragment : Fragment() {
    private var Filter_FromDate = ""
    private var Filter_ToDate = ""
    private var page : Int = 1
    private var total_page : Int = 0

    private lateinit var appointmentBinding: FragmentAppointmentBinding
    private val appointmentViewModel by lazy { ViewModelProvider(this)[AppointmentViewModel::class.java] }
    private lateinit var preference: SharedPreferences
    private lateinit var navController: NavController
    private val appointmentListData: ArrayList<AppointmentParentData> = ArrayList()

    var appointmentAdapter: AppointmentAdapter? = null
        companion object {
        var orderCheckin = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appointmentBinding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return appointmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preference = SharedPreferenceHelper.customPreference(requireContext())
        navController = findNavController()
        appointmentBinding.progressBar.visibility = View.VISIBLE
        appointmentBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        initAdapter()
        setLiveDataObservation()

        appointmentBinding.edtFromDate.setOnClickListener {
            val ca = Calendar.getInstance()
            val mYear = ca[Calendar.YEAR]
            val mMonth = ca[Calendar.MONTH]
            val mDay = ca[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                { view, year, monthOfYear, dayOfMonth -> // day
                    var day = dayOfMonth.toString() + ""
                    if (dayOfMonth < 10) {
                        day = "0$dayOfMonth"
                    }
                    // month
                    val month_ = monthOfYear + 1
                    var month = month_.toString() + ""
                    if (month_ < 10) {
                        month = "0$month_"
                    }
                    appointmentBinding.edtFromDate.setText("$day-$month-$year")
                    appointmentBinding.edtFromDate.setTag("$year-$month-$day")
                }, mYear, mMonth, mDay
            )
          //  datePickerDialog.datePicker.minDate = ca.timeInMillis
            datePickerDialog.show()
        }


        appointmentBinding.edtToDate.setOnClickListener {
            if (appointmentBinding.edtFromDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(activity, "Kindly Select From Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                { view, year, monthOfYear, dayOfMonth -> // day
                    var day = dayOfMonth.toString() + ""
                    if (dayOfMonth < 10) {
                        day = "0$dayOfMonth"
                    }
                    // month
                    val month_ = monthOfYear + 1
                    var month = month_.toString() + ""
                    if (month_ < 10) {
                        month = "0$month_"
                    }
                    appointmentBinding.edtToDate.setText("$day-$month-$year")
                    appointmentBinding.edtToDate.setTag("$year-$month-$day")
                }, mYear, mMonth, mDay
            )
            //  datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

            //  datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            val fromDate: String = appointmentBinding.edtFromDate.getTag().toString().trim()
            val split = fromDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val sYear = split[0].toInt()
            val sMonth = split[1].toInt() - 1
            val sDay = split[2].toInt()

            val ca = Calendar.getInstance()
            ca[Calendar.YEAR] = sYear
            ca[Calendar.MONTH] = sMonth
            ca[Calendar.DAY_OF_MONTH] = sDay

            datePickerDialog.datePicker.minDate = ca.timeInMillis
            datePickerDialog.show()
        }

        appointmentBinding.txtSearch.setOnClickListener {
            if (appointmentBinding.edtFromDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(activity, "Kindly select valid From date", Toast.LENGTH_SHORT)
            } else if (appointmentBinding.edtToDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(activity, "Kindly select valid To date", Toast.LENGTH_SHORT)
            } else {
                Filter_FromDate = appointmentBinding.edtFromDate.getTag().toString().trim()
                Filter_ToDate = appointmentBinding.edtToDate.getTag().toString().trim()
                page = 1
                appointmentViewModel.appointmentList("Payment","",page.toString(),Filter_FromDate,Filter_ToDate,"Bearer ${preference.authToken}")
            }
        }

        appointmentBinding.txtClear.setOnClickListener {
            Filter_FromDate = ""
            Filter_ToDate = ""

            appointmentBinding.edtFromDate.setText("")
            appointmentBinding.edtToDate.setText("")
            page = 1
            appointmentViewModel.appointmentList("Payment","",page.toString(),Filter_FromDate,Filter_ToDate,"Bearer ${preference.authToken}")
        }

        appointmentBinding.imgPrevious.setOnClickListener {
            if (page <= total_page) {
                page =  page - 1
                appointmentViewModel.appointmentList("Payment","",page.toString(),Filter_FromDate,Filter_ToDate,"Bearer ${preference.authToken}")
            }
        }

        appointmentBinding.imgNext.setOnClickListener {
            if (page < total_page) {
                page = page + 1
                appointmentViewModel.appointmentList("Payment","",page.toString(),Filter_FromDate,Filter_ToDate,"Bearer ${preference.authToken}")
            }
        }
    }

    private fun initAdapter() {
        appointmentAdapter = AppointmentAdapter(requireActivity())
        appointmentBinding.recyclerView.adapter = appointmentAdapter
        appointmentAdapter!!.setOnItemClickListener(object :
            AppointmentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, data: AppointmentResponse.Datas.Data) {
                if (data.type == "Payment") {
                    /*if (data.status == "Checkedin"){
                        val clientName = data.client.name
                        val appointmentDate = data.appointment_date
                        val appointmentId = data.uuid
                        val clientID = data.client.uuid
                        val districtName = data.client.district.name
                        val intent = Intent(context, AppointmentDetailsActivity::class.java)
                        intent.putExtra("OrderDetails", data)
                        intent.putExtra("ClientName", clientName)
                        intent.putExtra("AppointmentDate", appointmentDate)
                        intent.putExtra("AppointmentId", appointmentId)
                        intent.putExtra("ClientID", clientID)
                        intent.putExtra("DistrictName", districtName)
                        intent.putExtra("FromPage", data.type)
                        intent.putExtra("CheckedIn", data.checkin)
                        intent.putExtra("CheckedInStatus", data.status)
                        intent.putExtra("total_amount", data.total_amount)
                        startActivity(intent)
                    }
                    else*/ if(data.status != "Completed") {
                        navController.navigate(
                            AppointmentFragmentDirections.actionAppointmentToClientFragment(
                                data.status,
                                data.client.uuid,
                                data.client.name,
                                data.appointment_date,
                                data.client.district.name,
                                data.uuid,
                                data.checkin,
                                data.checkout
                            ))

                    } else {
                        Toast.makeText(requireContext(), "The Order is completed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if(data.status != "Completed") {
                        val clientName = data.client.name
                        val appointmentDate = data.appointment_date
                        val appointmentId = data.uuid
                        val clientID = data.client.uuid
                        val districtName = data.client.district.name
                        val intent = Intent(context, AppointmentDetailsActivity::class.java)
                        intent.putExtra("OrderDetails", data)
                        intent.putExtra("ClientName", clientName)
                        intent.putExtra("AppointmentDate", appointmentDate)
                        intent.putExtra("AppointmentId", appointmentId)
                        intent.putExtra("ClientID", clientID)
                        intent.putExtra("DistrictName", districtName)
                        intent.putExtra("FromPage", data.type)
                        intent.putExtra("CheckedIn", data.checkin)
                        intent.putExtra("CheckedInStatus", data.status)
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "The Order is completed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCheckInClick(position: Int, data: AppointmentResponse.Datas.Data) {
                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)
                val currentTime = formatter.format(time)

                val formatterDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = Date()
                val currentDate = formatterDate.format(date)

                appointmentViewModel.checkInAppointment(
                    data,
                    currentTime,
                    MainActivity.wayLatitude.toString(),
                    MainActivity.wayLongitude.toString(),
                    currentDate,
                    "Bearer ${preference.authToken}"
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.e("RESUME", "OnBack")
        page = 1
        appointmentViewModel.appointmentList("Payment","",page.toString(),Filter_FromDate,Filter_ToDate,"Bearer ${preference.authToken}")
    }

    private fun setLiveDataObservation() {
        appointmentViewModel.appointmentCheckInLiveData.observe(
            requireActivity()
        ) { orderCheckin ->
            Toast.makeText(context, orderCheckin.message, Toast.LENGTH_SHORT).show()
            specimenDetails(appointmentViewModel.lastUpdatedAppointment!!, orderCheckin)

        }

        appointmentViewModel.appointmentLiveData.observe(requireActivity(), Observer {
            appointmentBinding.progressBar.visibility = View.GONE
            if (it.datas.data.isEmpty()) {
                appointmentBinding.recyclerView.visibility = View.GONE
                appointmentBinding.tvNoAppointments.visibility = View.VISIBLE
            } else {
                appointmentBinding.recyclerView.visibility = View.VISIBLE
                appointmentBinding.tvNoAppointments.visibility = View.GONE
                val appointmentList = it.datas.data
                val paymentAppointmentList =
                    appointmentList.filter { it.type == "Payment" } as MutableList<AppointmentResponse.Datas.Data>
            //    val specimenAppointmentList = appointmentList.filter { it.type == "Specimen" } as MutableList<AppointmentResponse.Datas.Data>
                val parentPaymentAppointmentObject = AppointmentParentData("Payment", subList = paymentAppointmentList)
               // val parentSpecimenAppointmentObject = AppointmentParentData("Specimen", subList = specimenAppointmentList)

                //it.datas.meta

                if (page == 1 && it.datas.data.size == 0) {
                    return@Observer
                }

                total_page = it.datas.meta.total_pages
                appointmentBinding.txtLoadMore.setText("$page/$total_page Load more")

                if (page == 1) {
                    appointmentBinding.imgPrevious.setVisibility(View.GONE)
                } else if (page > 1) {
                    appointmentBinding.imgPrevious.setVisibility(View.VISIBLE)
                }

                if (total_page == page) {
                    appointmentBinding.imgNext.setVisibility(View.GONE)
                } else if (total_page > page) {
                    appointmentBinding.imgNext.setVisibility(View.VISIBLE)
                }

                if (total_page == 1) {
                    appointmentBinding.lyoutBttomView.setVisibility(View.GONE)
                } else {
                    appointmentBinding.lyoutBttomView.setVisibility(View.VISIBLE)
                }
                appointmentListData.clear()
                appointmentListData.add(parentPaymentAppointmentObject)
              //  appointmentListData.add(parentSpecimenAppointmentObject)
                appointmentAdapter!!.setItems(appointmentListData)
            }
            // }
        })
    }

    private fun specimenDetails(
        it: AppointmentResponse.Datas.Data,
        orderCheckin: OrderCheckedInResponse? = null
    ) {
        val clientName = it.client.name
        val appointmentDate = it.appointment_date
        val appointmentId = it.uuid
        val districtName = it.client.district.name
        val intent = Intent(context, AppointmentDetailsActivity::class.java)
        intent.putExtra("OrderDetails", it)
        intent.putExtra("ClientName", clientName)
        intent.putExtra("AppointmentDate", appointmentDate)
        intent.putExtra("AppointmentId", appointmentId)
        intent.putExtra("DistrictName", districtName)
        intent.putExtra("FromPage", it.type)
        if (orderCheckin != null) {
            intent.putExtra("CheckedInStatus", orderCheckin.data.status)
        }
        startActivity(intent)
    }
}