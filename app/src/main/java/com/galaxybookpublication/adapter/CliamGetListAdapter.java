package com.galaxybookpublication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galaxybookpublication.R;
import com.galaxybookpublication.intrefaces.ClaimOnClick;
import com.galaxybookpublication.modelapi.ClaimListApiResponse;

import java.util.List;

public class CliamGetListAdapter extends RecyclerView.Adapter<CliamGetListAdapter.ViewHolder> {

    private final Context context;
    private final List<ClaimListApiResponse.Datum> data;
    private final ClaimOnClick claim;

    public CliamGetListAdapter(Context context, List<ClaimListApiResponse.Datum> data, ClaimOnClick claim) {
        this.context = context;
        this.data = data;
        this.claim = claim;
    }


    @NonNull
    @Override
    public CliamGetListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_claim_get_list, parent, false);
        return new CliamGetListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final CliamGetListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ClaimListApiResponse.Datum response = data.get(position);

        holder.txtClaimFor.setText(response.getClaim_for_label());
        holder.txtAmount.setText(response.getAmount());
        holder.txtStatus.setText(response.getStatus());

        if (!response.getProof().isEmpty()){
            Glide.with(context).load(response.getProof()).into(holder.imgProof);
        }

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                claim.onClickEdit(response.getUuid(),response.getClaim_for_label(),response.getClaim_for(),response.getAmount(),response.getProof(),response.getStatus());
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                claim.onClickDelete(response.getUuid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtClaimFor,txtAmount,txtStatus;
        LinearLayout lyoutParent;
        ImageView imgProof,imgEdit,imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtClaimFor = itemView.findViewById(R.id.txtClaimFor);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgProof = itemView.findViewById(R.id.imgProof);
            lyoutParent = itemView.findViewById(R.id.lyoutParent);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}