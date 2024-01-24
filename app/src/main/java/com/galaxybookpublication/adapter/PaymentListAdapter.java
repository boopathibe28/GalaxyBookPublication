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

import com.galaxybookpublication.R;
import com.galaxybookpublication.intrefaces.PaymentOnClick;
import com.galaxybookpublication.modelapi.PaymentOrderListApiResponse;

import java.util.List;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ViewHolder> {

    private final Context context;
    private final List<PaymentOrderListApiResponse.Datum> paymentlistArray;
    private final PaymentOnClick paymentOnClick;

    public PaymentListAdapter(Context context, List<PaymentOrderListApiResponse.Datum> paymentlistArray, PaymentOnClick paymentOnClick) {
        this.context = context;
        this.paymentlistArray = paymentlistArray;
        this.paymentOnClick = paymentOnClick;
    }


    @NonNull
    @Override
    public PaymentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_payment_list, parent, false);
        return new PaymentListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PaymentListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        PaymentOrderListApiResponse.Datum response = paymentlistArray.get(position);

        holder.txtEmployeeName.setText(response.getEmployee());
        holder.txtAmount.setText(response.getAmount());
        holder.txtPaidAt.setText(response.getPaid_at());
        holder.txtReferenceNo.setText(response.getReference_no());
        holder.txtMode.setText(response.getMode());
        holder.txtRemarks.setText(response.getRemarks());
        holder.txtStatus.setText(response.getStatus());


        holder.imgReciptDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentOnClick.onClickView(response.getUuid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentlistArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmployeeName,txtAmount,txtPaidAt,txtReferenceNo,txtMode,
                txtRemarks,txtStatus;
        LinearLayout lyoutParent;
        ImageView imgReciptDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtEmployeeName = itemView.findViewById(R.id.txtEmployeeName);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtPaidAt = itemView.findViewById(R.id.txtPaidAt);
            txtReferenceNo = itemView.findViewById(R.id.txtReferenceNo);
            txtMode = itemView.findViewById(R.id.txtMode);
            txtRemarks = itemView.findViewById(R.id.txtRemarks);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            lyoutParent = itemView.findViewById(R.id.lyoutParent);
            imgReciptDownload = itemView.findViewById(R.id.imgReciptDownload);
        }
    }
}