package com.galaxybookpublication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxybookpublication.R;
import com.galaxybookpublication.intrefaces.ClientOnClick;
import com.galaxybookpublication.modelapi.ClientListApiResponse;

import java.util.List;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ViewHolder> {

    private final Context context;
    private final List<ClientListApiResponse.Datum> clientlistArray;
    private final ClientOnClick clientOnClick;

    public ClientListAdapter(Context context, List<ClientListApiResponse.Datum> clientlistArray, ClientOnClick clientOnClick) {
        this.context = context;
        this.clientlistArray = clientlistArray;
        this.clientOnClick = clientOnClick;
    }


    @NonNull
    @Override
    public ClientListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_client_list, parent, false);
        return new ClientListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ClientListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ClientListApiResponse.Datum response = clientlistArray.get(position);

        holder.txtName.setText(response.getName());
        holder.txtEmail.setText(response.getEmail());
        holder.txtPhoneNumber.setText(response.getPhone_number());
        holder.txtEmergencyContact.setText(response.getEmergency_contact());
        holder.txtAddress.setText(response.getAddress());
        holder.txtPlaceName.setText(response.getDistrict().getName());
        holder.txtStatus.setText(response.getDistrict().getStatus());

        holder.lyoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientOnClick.onClickView(response.getUuid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientlistArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName,txtEmail,txtPhoneNumber,txtEmergencyContact,txtAddress,
                txtPlaceName,txtStatus;
        LinearLayout lyoutParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPhoneNumber = itemView.findViewById(R.id.txtPhoneNumber);
            txtEmergencyContact = itemView.findViewById(R.id.txtEmergencyContact);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtPlaceName = itemView.findViewById(R.id.txtPlaceName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            lyoutParent = itemView.findViewById(R.id.lyoutParent);
        }
    }
}