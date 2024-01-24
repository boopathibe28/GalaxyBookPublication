package com.galaxybookpublication.views;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.galaxybookpublication.activity.PaymentActivity;
import com.galaxybookpublication.adapter.ClientListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.api.CommonFunctions;
import com.galaxybookpublication.databinding.FragmentClientMenuBinding;
import com.galaxybookpublication.intrefaces.ClientOnClick;
import com.galaxybookpublication.modelapi.ClientListApiResponse;

import java.util.List;

public class ClientFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    FragmentClientMenuBinding binding;
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private FragmentManager fragmentManager;
    int page;
    List<ClientListApiResponse.Datum> clientlistArray;
    List<ClientListApiResponse.Datum> tempArrayList;
    private int total_page;
    private String queryName = "";
    public ClientFragment() {
        // Required empty public constructor
    }

    public static ClientFragment newInstance(String param1, String param2) {
        ClientFragment fragment = new ClientFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_menu, container, false);
        View view = binding.getRoot();
        fragmentManager = activity.getSupportFragmentManager();

        initialView();

        binding.edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence query, int i, int i1, int i2) {
                if (query.length() > 0){

                    /*tempArrayList = new ArrayList<>();
                    tempArrayList.clear();

                    for (int count = 0; count < clientlistArray.size(); count++) {
                        String strName = clientlistArray.get(count).getName();

                        if (strName.toLowerCase().contains(query.toString().toLowerCase())) {
                            tempArrayList.add(clientlistArray.get(count));
                        }
                    }
                    loadListAdapter(tempArrayList);*/
                   // queryName = query.toString();

                }
                else {
                    loadListAdapter(clientlistArray);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void initialView() {
        binding.imgPrevious.setOnClickListener(this);
        binding.imgNext.setOnClickListener(this);
        binding.imgClear.setOnClickListener(this);
        binding.txtSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.txtLoadMore){
           /* if (page < total_page){
                page = page + 1;
                scrolltoPosition = listArray.size();
                getList();
            }*/
        }
        else if (view == binding.txtSearch){
            if (binding.edtSearch.getText().toString().trim().isEmpty()){
                Toast.makeText(activity,"Enter valid Name",Toast.LENGTH_SHORT).show();
            }
            else {
                queryName = binding.edtSearch.getText().toString().trim();
                page = 0;
                getList();
            }
        }
        else if (view == binding.imgPrevious){
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
        else if (view == binding.imgClear){
            queryName = "";
            binding.edtSearch.setText("");
            page = 1;
            getList();
        }
    }


    private void getList() {
        CommonApiCalls.getInstance().clientList(activity,page,queryName, new CommonCallback.Listener() {
            @Override
            public void onSuccess(Object body) {
                ClientListApiResponse data = (ClientListApiResponse) body;

                // Item Details
                if (data.getData() != null){
                    if (data.getData().getData().size() > 0){

                       clientlistArray = data.getData().getData();

                        loadListAdapter(clientlistArray);
                    }
                }

                if (page == 1 && data.getData().getData().size() == 0){
                    return;
                }

                total_page = Integer.parseInt(data.getData().getMeta().getTotal_pages());
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

    private void loadListAdapter(List<ClientListApiResponse.Datum> clientlistArray) {
        if (clientlistArray.size() > 0) {
            binding.rvItemList.setVisibility(View.VISIBLE);
            binding.txtNoDataFound.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            binding.rvItemList.setLayoutManager(layoutManager);
            ClientListAdapter clientListAdapter = new ClientListAdapter(activity, clientlistArray, new ClientOnClick() {
                @Override
                public void onClickView(String key) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    CommonFunctions.getInstance().newIntent(activity, PaymentActivity.class, bundle, false, false);
                }
            });
            binding.rvItemList.setAdapter(clientListAdapter);

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
