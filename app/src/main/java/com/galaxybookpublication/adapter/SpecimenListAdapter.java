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
import com.galaxybookpublication.modelapi.SpecimenListApiResponse;

import java.util.List;

public class SpecimenListAdapter extends RecyclerView.Adapter<SpecimenListAdapter.ViewHolder> {

    private final Context context;
    private final List<SpecimenListApiResponse.Datum> specimenlistArray;
    private final ClientOnClick clientOnClick;

    public SpecimenListAdapter(Context context, List<SpecimenListApiResponse.Datum> specimenlistArray, ClientOnClick clientOnClick) {
        this.context = context;
        this.specimenlistArray = specimenlistArray;
        this.clientOnClick = clientOnClick;
    }


    @NonNull
    @Override
    public SpecimenListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_specimen_list, parent, false);
        return new SpecimenListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final SpecimenListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        SpecimenListApiResponse.Datum response = specimenlistArray.get(position);

        holder.txtSchoolName.setText(response.getSchool_name());
        holder.txtPinCode.setText(response.getPincode());
        holder.txtTaluk.setText(response.getTaluk());
        if (response.getBook() != null) {
            if (response.getBook().size() > 0) {
                String bookName = "";
                for (int i = 0; i < response.getBook().size(); i++) {
                    if (i == 0) {
                        bookName = response.getBook().get(i).getInfo().getName();
                    } else {
                        bookName = bookName + "\n" + response.getBook().get(i).getInfo().getName();
                    }
                }
                holder.txtBookName.setText(bookName);
            }
        }

        holder.txtDistrictName.setText(response.getDistrict().getName());
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
        return specimenlistArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSchoolName,txtPinCode,txtTaluk,txtBookName,txtDistrictName,txtStatus;
        LinearLayout lyoutParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSchoolName = itemView.findViewById(R.id.txtSchoolName);
            txtPinCode = itemView.findViewById(R.id.txtPinCode);
            txtTaluk = itemView.findViewById(R.id.txtTaluk);
            txtBookName = itemView.findViewById(R.id.txtBookName);
            txtDistrictName = itemView.findViewById(R.id.txtDistrictName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            lyoutParent = itemView.findViewById(R.id.lyoutParent);
        }
    }
}