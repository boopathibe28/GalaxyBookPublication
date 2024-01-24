package com.galaxybookpublication.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxybookpublication.R;
import com.galaxybookpublication.modelapi.AppointmentListApiResponse;
import com.galaxybookpublication.views.AppointmentDetailsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private final Context context;
    private final List<AppointmentListApiResponse.Datum> data;
    NavController navController;
    public AppointmentListAdapter(Context context, List<AppointmentListApiResponse.Datum> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_appointment_list, parent, false);
        return new AppointmentListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AppointmentListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        AppointmentListApiResponse.Datum response = data.get(position);

        if (response.getType().toLowerCase().equals("payment")) {
            holder.itemViews.setVisibility(View.VISIBLE);
            holder.tvClientName.setText(response.getClient().getName());
            holder.tvDistrict.setText(response.getClient().getDistrict().getName());
            holder.tvClientName.setText(response.getClient().getName());
            holder.tvStatus.setText(response.getStatus());
            holder.btnCheckin.setVisibility(View.GONE);

            String inputPattern = "dd-MM-yyyy";
            String outputPattern = "dd \n MMM \n yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = "";

            try {
                date = inputFormat.parse(response.getAppointment_date());
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDate.setText(str);

            if (response.getStatus().toLowerCase().equals("checkedin")) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red_700));
                holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.red_700));
                holder.view1.setBackgroundColor(ContextCompat.getColor(context, R.color.red_700));
            } else if (response.getStatus().toLowerCase().equals("completed")) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
                holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                holder.view1.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            }

        }
        else {
            holder.itemViews.setVisibility(View.GONE);
        }

        holder.itemViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!response.getStatus().toLowerCase().equals("completed")){

                    /*Intent(context, AppointmentDetailsActivity::class.java)
                    intent.putExtra("OrderDetails", appointmentClientList[position])
                    intent.putExtra("ClientName", clientName)
                    intent.putExtra("AppointmentDate", appointmentDate)
                    intent.putExtra("AppointmentId", appointmentId)
                    intent.putExtra("DistrictName", districtName)
                    intent.putExtra("FromPage", "Payment")
                    startForResult.launch(intent)*/

                   /* navController.navigate(
                            AppointmentFragmentDirections.Companion.actionAppointmentToClientFragment(
                                    data.get(position).getStatus(),
                                    data.get(position).getUuid(),
                                    data.get(position).getClient().getName(),
                                    data.get(position).getAppointment_date(),
                                    data.get(position).getClient().getDistrict().getName(),
                                    data.get(position).getUuid(),
                                    data.get(position).getCheckin(),
                                    data.get(position).getCheckout()));*/

                    Intent intent = new Intent(context, AppointmentDetailsActivity.class);
                    intent.putExtra("appointmentStatus",response.getStatus());
                    intent.putExtra("CheckedInStatus",response.getStatus());
                    intent.putExtra("clientId",response.getClient().getUuid());
                    intent.putExtra("ClientName",response.getClient().getName());
                    intent.putExtra("AppointmentDate",response.getAppointment_date());
                    intent.putExtra("DistrictName",response.getClient().getDistrict().getName());
                    intent.putExtra("AppointmentId",response.getUuid());
                    intent.putExtra("CheckedIn",response.getCheckin());
                    intent.putExtra("checkout",response.getCheckout());
                    intent.putExtra("FromPage","Payment");
                    intent.putExtra("OrderDetails", String.valueOf(data.get(position)));
                    ((Activity) context).startActivity(intent);
                }
                else {
                    Toast.makeText(context, "The Order is completed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemViews;
        View view,view1;
        Button btnCheckin;
        TextView tvDate,tvClientName,tvDistrict,tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemViews = itemView.findViewById(R.id.itemViews);
            view = itemView.findViewById(R.id.view);
            view1 = itemView.findViewById(R.id.view1);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCheckin = itemView.findViewById(R.id.btnCheckin);
        }
    }
}