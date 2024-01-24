package com.galaxybookpublication.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.ItemAppointmentBinding
import com.galaxybookpublication.databinding.ItemAppointmentChildBinding
import com.galaxybookpublication.models.AppointmentParentData
import com.galaxybookpublication.models.AppointmentParentData.Constants
import com.galaxybookpublication.models.data.response.AppointmentResponse
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AppointmentAdapter(context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    private var _context = context
    private var appointmentList = arrayListOf<AppointmentParentData>()

    interface OnItemClickListener {
        fun onItemClick(position: Int, data: AppointmentResponse.Datas.Data)
        fun onCheckInClick(position: Int, data: AppointmentResponse.Datas.Data)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == Constants.PARENT) {
            val bindingItem = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AppointmentParentViewHolder(bindingItem)
        } else {
            val bindingItem = ItemAppointmentChildBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            AppointmentChildViewHolder(bindingItem, mListener)
        }
    }

    class AppointmentChildViewHolder(
        binding: ItemAppointmentChildBinding,
        mListener: OnItemClickListener
    ) : ViewHolder(binding.root) {
        var _binding: ItemAppointmentChildBinding = binding
    }

    class AppointmentParentViewHolder(
        binding: ItemAppointmentBinding
    ) : ViewHolder(binding.root) {
        var _binding: ItemAppointmentBinding = binding
    }

    override fun getItemViewType(position: Int): Int {
        return appointmentList[position].type
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    fun setItems(
        appointmentList: ArrayList<AppointmentParentData>
    ) {
        this.appointmentList = appointmentList
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun collapseParentRow(position: Int) {
        if(appointmentList.isNotEmpty()) {
            val currentBoardingRow = appointmentList[position]
            val services = currentBoardingRow.subList
            appointmentList[position].isExpanded = false
            if (appointmentList[position].type == Constants.PARENT && appointmentList.size != 0) {
                services.forEach { _ ->
                    appointmentList.removeAt(position + 1)
                }
                notifyDataSetChanged()
            }
        }
    }

    private fun expandParentRow(position: Int) {
        try {
            if (appointmentList.isNotEmpty()) {
                val currentBoardingRow = appointmentList[position]
                val services = currentBoardingRow.subList
                currentBoardingRow.isExpanded = true
                var nextPosition = position
                if (currentBoardingRow.type == Constants.PARENT) {

                    services.forEach { service ->
                        val parentModel = AppointmentParentData()
                        parentModel.type = Constants.CHILD
                        val subList: ArrayList<AppointmentResponse.Datas.Data> = ArrayList()
                        subList.add(service)
                        parentModel.subList = subList
                        appointmentList.add(++nextPosition, parentModel)
                    }
                    notifyDataSetChanged()
                }
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun expandOrCollapseParentItem(singleBoarding: AppointmentParentData, position: Int) {
        if (singleBoarding.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = appointmentList[position]
        if (data.type == Constants.PARENT) {
            holder as AppointmentParentViewHolder
            expandParentRow(position)
            holder.apply {
                _binding.parentTitle.text = data.parentTitle
                _binding.downIv.setOnClickListener {
                    expandOrCollapseParentItem(data, position)
                }
            }
        } else {
            holder as AppointmentChildViewHolder
            holder.apply {
                val childAppointment = data.subList.first()
                holder._binding.itemView.setOnClickListener {
                    mListener.onItemClick(position, childAppointment)
                }

                val originalFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val targetFormat: DateFormat = SimpleDateFormat("dd \n MMM \n yyyy", Locale.US)
                val date: Date = originalFormat.parse(childAppointment.appointment_date) as Date
                val formattedDate: String = targetFormat.format(date)
                if (childAppointment.status.trim().lowercase() == "checkedin") {
                    holder._binding.tvStatus.setTextColor(ContextCompat.getColor(_context,R.color.red_700))
                    holder._binding.view.setBackgroundColor(ContextCompat.getColor(_context,R.color.red_700))
                    holder._binding.view1.setBackgroundColor(ContextCompat.getColor(_context,R.color.red_700))
                } else if (childAppointment.status.trim().lowercase() == "completed") {
                    holder._binding.tvStatus.setTextColor(ContextCompat.getColor(_context,R.color.green))
                    holder._binding.view.setBackgroundColor(ContextCompat.getColor(_context,R.color.green))
                    holder._binding.view1.setBackgroundColor(ContextCompat.getColor(_context,R.color.green))
                }
                when (childAppointment.type) {
                    "Payment" -> {
                        holder._binding.tvClientName.text = childAppointment.client.name
                        holder._binding.tvDistrict.text = childAppointment.client.district.name
                        holder._binding.tvDate.text = formattedDate
                        holder._binding.tvStatus.text = childAppointment.status
                        holder._binding.btnCheckin.visibility = View.GONE
                    }
                   /* "Specimen" -> {
                        holder._binding.tvClientName.text = childAppointment.client.name
                        holder._binding.tvDistrict.visibility = View.GONE
                      //  holder._binding.tvDate.text = formattedDate
                        holder._binding.tvStatus.text = childAppointment.status
                        if (childAppointment.status == "Checkedin" || childAppointment.status == "Completed") {
                            holder._binding.btnCheckin.visibility = View.GONE
                        } else {
                            holder._binding.btnCheckin.visibility = View.VISIBLE
                            holder._binding.btnCheckin.setOnClickListener {
                                mListener.onCheckInClick(position,childAppointment)
                            }
                        }
                    }*/
                }
            }
        }
    }

}