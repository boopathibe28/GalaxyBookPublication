package com.galaxybookpublication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.galaxybookpublication.R;
import com.galaxybookpublication.adapter.PaymentListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.databinding.ActivityPaymentBinding;
import com.galaxybookpublication.intrefaces.PaymentOnClick;
import com.galaxybookpublication.modelapi.PaymentOrderListApiResponse;
import com.galaxybookpublication.modelapi.PdfLinkApiResponse;

import java.util.List;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityPaymentBinding binding;
    String uuid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);

        if (getIntent().getExtras() != null){
            uuid = getIntent().getExtras().getString("key");
        }
        initialView();
        getList();
    }

    private void initialView() {
        binding.imgBack.setOnClickListener(this);
        binding.imgReciptDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imgBack){
            finish();
        }
        else if (view == binding.imgReciptDownload){
            receiptDownloadApiCall();
        }
    }

    private void getList() {
        CommonApiCalls.getInstance().paymentOrderList(PaymentActivity.this,uuid, new CommonCallback.Listener() {
            @Override
            public void onSuccess(Object body) {
                PaymentOrderListApiResponse response = (PaymentOrderListApiResponse) body;

                if (response.getData().getPayments().getData() != null){
                    if (response.getData().getPayments().getData().size() > 0){

                        List<PaymentOrderListApiResponse.Datum> paymentlistArray = response.getData().getPayments().getData();

                        loadListAdapter(paymentlistArray);
                    }
                }

                if (response.getData().getSummary() != null){
                    binding.txtTotalAmount.setText(response.getData().getSummary().getTotal_amount());
                    binding.txtReceivedAmount.setText(response.getData().getSummary().getReceived_amount());
                    binding.txtPendingAmount.setText(response.getData().getSummary().getPending_amount());
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(PaymentActivity.this,reason,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListAdapter(List<PaymentOrderListApiResponse.Datum> paymentlistArray) {
        if (paymentlistArray.size() > 0) {
            binding.rvPaymentList.setVisibility(View.VISIBLE);
            binding.txtNoDataFound.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentActivity.this, LinearLayoutManager.VERTICAL, false);
            binding.rvPaymentList.setLayoutManager(layoutManager);
            PaymentListAdapter paymentListAdapter = new PaymentListAdapter(PaymentActivity.this, paymentlistArray, new PaymentOnClick() {
                @Override
                public void onClickView(String key) {

                }
            });
            binding.rvPaymentList.setAdapter(paymentListAdapter);

        }
        else {
            binding.rvPaymentList.setVisibility(View.GONE);
            binding.txtNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    private void receiptDownloadApiCall() {
        CommonApiCalls.getInstance().paymentReceiptDownload(PaymentActivity.this,uuid, new CommonCallback.Listener() {
            @Override
            public void onSuccess(Object body) {
                PdfLinkApiResponse response = (PdfLinkApiResponse) body;

                String URL = response.getFile_url();

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(PaymentActivity.this,reason,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
