package com.galaxybookpublication.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.galaxybookpublication.R
import com.galaxybookpublication.databinding.ItemClientAppointmentsBinding
import com.galaxybookpublication.models.data.response.ClientAppointmentResponse

class ClientAppointmentAdapter(context: Context, appointmentClientList: List<ClientAppointmentResponse.Data>) :
    RecyclerView.Adapter<ClientAppointmentAdapter.ClientAppointmentViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    val _context = context

    interface OnItemClickListener {
        fun onItemClick(position: Int, appointmentClientList: List<ClientAppointmentResponse.Data>)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    var _appointmentClientList = appointmentClientList


    class ClientAppointmentViewHolder(
        binding: ItemClientAppointmentsBinding,
        listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        var _binding: ItemClientAppointmentsBinding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientAppointmentViewHolder {
        val bindingItem =
            ItemClientAppointmentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ClientAppointmentViewHolder(bindingItem, mListener)
    }

    override fun getItemCount(): Int {
        return _appointmentClientList.size
    }

    fun setItems(appointmentClientList: List<ClientAppointmentResponse.Data>) {
        _appointmentClientList = appointmentClientList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ClientAppointmentViewHolder, position: Int) {
        val appointment = _appointmentClientList.get(position)
        if(_appointmentClientList[position].payment_status.trim().lowercase() == "installment"){
                holder._binding.tvStatus.setTextColor(ContextCompat.getColor(_context, R.color.blue))
                holder._binding.view.setBackgroundColor(ContextCompat.getColor(_context, R.color.blue))
                holder._binding.view1.setBackgroundColor(ContextCompat.getColor(_context, R.color.blue))
            } else if (_appointmentClientList[position].payment_status.trim().lowercase() == "paid") {
                holder._binding.tvStatus.setTextColor(ContextCompat.getColor(_context, R.color.green))
                holder._binding.view.setBackgroundColor(ContextCompat.getColor(_context, R.color.green))
                holder._binding.view1.setBackgroundColor(ContextCompat.getColor(_context, R.color.green))
            }else if (_appointmentClientList[position].payment_status.trim().lowercase() == "pending") {
            holder._binding.tvStatus.setTextColor(ContextCompat.getColor(_context, R.color.red_700))
            holder._binding.view.setBackgroundColor(ContextCompat.getColor(_context, R.color.red_700))
            holder._binding.view1.setBackgroundColor(ContextCompat.getColor(_context, R.color.red_700))
        }
            holder._binding.tvOrderId.text = appointment.order_number
            holder._binding.tvProductType.text = _appointmentClientList[position].product
            holder._binding.tvTotalAmt.text = "₹"+_appointmentClientList[position].total_amount+" Total Amt"
            holder._binding.tvReceivedAmt.text = "₹"+_appointmentClientList[position].received_amount+" Received Amt"
            holder._binding.tvPendingAmt.text =  "₹"+_appointmentClientList[position].pending_amount+"  Pending Amt"
            holder._binding.tvStatus.text = _appointmentClientList[position].payment_status
        holder._binding.itemView.setOnClickListener {
            mListener.onItemClick(position, _appointmentClientList)
        }
    }
}