package com.galaxybookpublication.views;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.galaxybookpublication.R;
import com.galaxybookpublication.activity.SpecimenCreateActivity;
import com.galaxybookpublication.adapter.SpecimenListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.api.CommonFunctions;
import com.galaxybookpublication.databinding.FragmentSpecimenBinding;
import com.galaxybookpublication.intrefaces.ClientOnClick;
import com.galaxybookpublication.modelapi.SpecimenListApiResponse;

import java.util.List;

public class SpecimenFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    FragmentSpecimenBinding binding;
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private FragmentManager fragmentManager;

    public SpecimenFragment() {
        // Required empty public constructor
    }

    public static SpecimenFragment newInstance(String param1, String param2) {
        SpecimenFragment fragment = new SpecimenFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_specimen, container, false);
        View view = binding.getRoot();
        fragmentManager = activity.getSupportFragmentManager();

        initialView();

        return view;
    }

    private void initialView() {
        binding.imgCreateSpecimen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imgCreateSpecimen){
            CommonFunctions.getInstance().newIntent(activity, SpecimenCreateActivity.class,Bundle.EMPTY,false,false);
        }
    }


    private void getList() {
        CommonApiCalls.getInstance().specimenList(activity, new CommonCallback.Listener() {
            @Override
            public void onSuccess(Object body) {
                SpecimenListApiResponse data = (SpecimenListApiResponse) body;

                // Item Details
                if (data.getData() != null){
                    if (data.getData().size() > 0){
                        binding.rvItemList.setVisibility(View.VISIBLE);
                        binding.txtNoDataFound.setVisibility(View.GONE);

                        List<SpecimenListApiResponse.Datum> list = data.getData();

                        loadListAdapter(list);
                    }
                    else {
                        binding.rvItemList.setVisibility(View.GONE);
                        binding.txtNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

               /* if (page == 1 && data.getData().size() == 0){
                    return;
                }

                total_page = Integer.parseInt(data.getTotal_page());
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
                }*/
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(activity,reason,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadListAdapter(List<SpecimenListApiResponse.Datum> list) {
        if (list.size() > 0) {
            binding.rvItemList.setVisibility(View.VISIBLE);
            binding.txtNoDataFound.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            binding.rvItemList.setLayoutManager(layoutManager);
            SpecimenListAdapter adapter = new SpecimenListAdapter(activity, list, new ClientOnClick() {
                @Override
                public void onClickView(String key) {

                }
            });
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
        getList();
    }

}
