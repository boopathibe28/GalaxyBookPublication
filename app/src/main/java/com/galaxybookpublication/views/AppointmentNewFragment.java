package com.galaxybookpublication.views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.galaxybookpublication.R;
import com.galaxybookpublication.adapter.AppointmentListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.databinding.FragmentAppointmentNewBinding;
import com.galaxybookpublication.modelapi.AppointmentListApiResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AppointmentNewFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FragmentAppointmentNewBinding binding;
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private FragmentManager fragmentManager;

    private int total_page;
    int page;
    String Filter_FromDate = "";
    String Filter_ToDate = "";

    public AppointmentNewFragment() {
        // Required empty public constructor
    }

    public static AppointmentNewFragment newInstance(String param1, String param2) {
        AppointmentNewFragment fragment = new AppointmentNewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_new, container, false);
        View view = binding.getRoot();
        fragmentManager = activity.getSupportFragmentManager();

        initialView();

        return view;

    }

    private void initialView() {
        binding.imgPrevious.setOnClickListener(this);
        binding.imgNext.setOnClickListener(this);

        binding.edtFromDate.setOnClickListener(this);
        binding.edtToDate.setOnClickListener(this);
        binding.txtSearch.setOnClickListener(this);
        binding.txtClear.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.imgPrevious){
            if (page <= total_page){
                page = page - 1;
                getList();
            }
        }
        else if (view == binding.imgNext){
            if (page < total_page) {
                page = page + 1;
                getList();
            }
        }
        else if (view == binding.edtFromDate){
            final Calendar ca = Calendar.getInstance();
            int mYear = ca.get(Calendar.YEAR);
            int mMonth = ca.get(Calendar.MONTH);
            int mDay = ca.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // day
                    String day = dayOfMonth+"";
                    if (dayOfMonth < 10) {
                        day = "0"+dayOfMonth;
                    }
                    // month
                    int month_ = (monthOfYear + 1);
                    String month = month_+"";
                    if (month_ < 10) {
                        month = "0"+month_;
                    }
                    binding.edtFromDate.setText(day + "-" + month + "-" + year);
                    binding.edtFromDate.setTag(year + "-" + month + "-" + day);

                }
            }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(ca.getTimeInMillis());
            datePickerDialog.show();
        }
        else if (view == binding.edtToDate){
            if (binding.edtFromDate.getText().toString().trim().isEmpty()){
                Toast.makeText(activity,"Kindly Select From Date",Toast.LENGTH_SHORT).show();
                return;
            }
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // day
                    String day = dayOfMonth+"";
                    if (dayOfMonth < 10) {
                        day = "0"+dayOfMonth;
                    }
                    // month
                    int month_ = (monthOfYear + 1);
                    String month = month_+"";
                    if (month_ < 10) {
                        month = "0"+month_;
                    }
                    binding.edtToDate.setText(day + "-" + month + "-" + year);
                    binding.edtToDate.setTag(year + "-" + month + "-" + day);

                }
            }, mYear, mMonth, mDay);
            //  datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

            String fromDate = binding.edtFromDate.getTag().toString().trim();
            String[] split = fromDate.split("-");
            int sYear = Integer.parseInt(split[0]);
            int sMonth = Integer.parseInt(split[1]) - 1;
            int sDay = Integer.parseInt(split[2]);

            final Calendar ca = Calendar.getInstance();
            ca.set(Calendar.YEAR, sYear);
            ca.set(Calendar.MONTH, sMonth);
            ca.set(Calendar.DAY_OF_MONTH, sDay);

            datePickerDialog.getDatePicker().setMinDate(ca.getTimeInMillis());
            datePickerDialog.show();
        }
        else if (view == binding.txtSearch){
            if (binding.edtFromDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(activity,"Kindly select valid From date",Toast.LENGTH_SHORT);
            }
            else if (binding.edtToDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(activity,"Kindly select valid To date",Toast.LENGTH_SHORT);
            }
            else {
                Filter_FromDate = binding.edtFromDate.getText().toString().trim();
                Filter_ToDate = binding.edtToDate.getText().toString().trim();
                page = 1;
                getList();
            }
        }
        else if (view == binding.txtClear){
            Filter_FromDate = "";
            Filter_ToDate = "";

            binding.edtFromDate.setText("");
            binding.edtToDate.setText("");
            page = 1;
            getList();
        }

    }
    private void getList() {
        CommonApiCalls.getInstance().appointmentGetList(activity,page,Filter_FromDate,Filter_ToDate, new CommonCallback.Listener() {
            @Override
            public void onSuccess(Object body) {
                AppointmentListApiResponse data = (AppointmentListApiResponse) body;

                // Item Details
                if (data.getData() != null){
                    if (data.getData().getData().size() > 0){

                        List<AppointmentListApiResponse.Datum> list = data.getData().getData();

                        loadListAdapter(list);
                    }
                }

                if (page == 1 && data.getData().getData().size() == 0){
                    return;
                }

                total_page = data.getData().getMeta().getTotal_pages();
                binding.txtLoadMore.setText(page+"/"+total_page+" "+"Load more");

                if (page == 1){
                    binding.imgPrevious.setVisibility(View.GONE);
                }
                else if (page > 1){
                    binding.imgPrevious.setVisibility(View.VISIBLE);
                }

                if (total_page == page){
                    binding.imgNext.setVisibility(View.GONE);
                }
                else if (total_page > page){
                    binding.imgNext.setVisibility(View.VISIBLE);
                }

                if (total_page == 1){
                    binding.lyoutBttomView.setVisibility(View.GONE);
                }
                else {
                    binding.lyoutBttomView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(activity,reason,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadListAdapter(List<AppointmentListApiResponse.Datum> list) {
        if (list.size() > 0) {
            binding.rvItemList.setVisibility(View.VISIBLE);
            binding.txtNoDataFound.setVisibility(View.GONE);
            List<AppointmentListApiResponse.Datum> listData = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getType().toLowerCase().equals("payment")){
                    listData.add(list.get(i));
                }
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            binding.rvItemList.setLayoutManager(layoutManager);
            AppointmentListAdapter adapter = new AppointmentListAdapter(activity, listData);
            binding.rvItemList.setAdapter(adapter);

        }
        else {
            binding.rvItemList.setVisibility(View.GONE);
            binding.txtNoDataFound.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        if (context instanceof Activity) {
            activity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        getList();
    }


}