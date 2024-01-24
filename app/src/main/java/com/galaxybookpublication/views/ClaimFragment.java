package com.galaxybookpublication.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxybookpublication.R;
import com.galaxybookpublication.activity.CreateClaimActivity;
import com.galaxybookpublication.activity.Utility;
import com.galaxybookpublication.adapter.ClaimListAdapter;
import com.galaxybookpublication.adapter.CliamGetListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.api.CommonFunctions;
import com.galaxybookpublication.databinding.FragmentClaimBinding;
import com.galaxybookpublication.intrefaces.BookOnClick;
import com.galaxybookpublication.intrefaces.ClaimOnClick;
import com.galaxybookpublication.modelapi.ClaimDeleteApiResponse;
import com.galaxybookpublication.modelapi.ClaimListApiResponse;
import com.galaxybookpublication.modelapi.ClaimOptionListApiResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class ClaimFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    FragmentClaimBinding binding;
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private FragmentManager fragmentManager;

    private int total_page;
    int page;

    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    File uploadImage = null;

    String Filter_ClaimFor = "";
    String Filter_FromDate = "";
    String Filter_ToDate = "";
    public ClaimFragment() {
        // Required empty public constructor
    }

    public static ClaimFragment newInstance(String param1, String param2) {
        ClaimFragment fragment = new ClaimFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_claim, container, false);
        View view = binding.getRoot();
        fragmentManager = activity.getSupportFragmentManager();

        initialView();

        return view;
    }

    private void initialView() {
        binding.imgCreateClaim.setOnClickListener(this);
        binding.imgPrevious.setOnClickListener(this);
        binding.imgNext.setOnClickListener(this);

        binding.edtClaimFor.setOnClickListener(this);
        binding.edtFromDate.setOnClickListener(this);
        binding.edtToDate.setOnClickListener(this);
        binding.txtSearch.setOnClickListener(this);
        binding.txtClear.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.imgCreateClaim){
            Bundle bundle = new Bundle();
            bundle.putString("from", "add");
            CommonFunctions.getInstance().newIntent(activity, CreateClaimActivity.class,bundle,false,false);
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
           // datePickerDialog.getDatePicker().setMinDate(ca.getTimeInMillis());
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
        else if (view == binding.edtClaimFor){
            CommonApiCalls.getInstance().claimList(activity,new CommonCallback.Listener() {
                @Override
                public void onSuccess(Object body) {
                    ClaimOptionListApiResponse response = (ClaimOptionListApiResponse) body;

                    Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_list);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationCombo;
                    dialog.setCancelable(false);

                    ImageView imgClose = dialog.findViewById(R.id.imgClose);
                    TextView txtHeader = dialog.findViewById(R.id.txtHeader);
                    RecyclerView rvList = dialog.findViewById(R.id.rvList);
                    TextView txtNoDataFound = dialog.findViewById(R.id.txtNoDataFound);
                    txtHeader.setText("Claim For");

                    dialog.show();

                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    if (response.getData().size() > 0){
                        rvList.setVisibility(View.VISIBLE);
                        txtNoDataFound.setVisibility(View.GONE);

                        rvList.setLayoutManager(new GridLayoutManager(activity, 1));
                        ClaimListAdapter adapter = new ClaimListAdapter(activity, response.getData(), new BookOnClick() {
                            @Override
                            public void onClickView(String key,String name) {
                                binding.edtClaimFor.setText(name);
                                binding.edtClaimFor.setTag(key);
                                dialog.dismiss();
                            }
                        });
                        rvList.setAdapter(adapter);
                    }
                    else {
                        rvList.setVisibility(View.GONE);
                        txtNoDataFound.setVisibility(View.VISIBLE);
                    }

                }
                @Override
                public void onFailure(String reason) {
                    Toast.makeText(activity, reason, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if (view == binding.txtSearch){
            if (binding.edtClaimFor.getTag() != null) {
                Filter_ClaimFor = binding.edtClaimFor.getTag().toString();
            }
            else {
                Filter_ClaimFor = "";
            }
            Filter_FromDate = binding.edtFromDate.getText().toString().trim();
            Filter_ToDate = binding.edtToDate.getText().toString().trim();
            page = 1;
            getList();
        }
        else if (view == binding.txtClear){
            Filter_ClaimFor = "";
            Filter_FromDate = "";
            Filter_ToDate = "";

            binding.edtFromDate.setText("");
            binding.edtToDate.setText("");
            binding.edtClaimFor.setText("");
            page = 1;
            getList();
        }

    }



    private void getList() {
        CommonApiCalls.getInstance().claimGetList(activity,page,Filter_ClaimFor,Filter_FromDate,Filter_ToDate, new CommonCallback.Listener() {
            @Override
            public void onSuccess(Object body) {
                ClaimListApiResponse data = (ClaimListApiResponse) body;

                // Item Details
                if (data.getData() != null){
                    if (data.getData().getData().size() > 0){
                        binding.lyoutBttomView.setVisibility(View.VISIBLE);
                        binding.rvItemList.setVisibility(View.VISIBLE);
                        binding.txtNoDataFound.setVisibility(View.GONE);

                        List<ClaimListApiResponse.Datum> list = data.getData().getData();

                        loadListAdapter(list);
                    }
                    else {
                        binding.lyoutBttomView.setVisibility(View.GONE);
                        binding.rvItemList.setVisibility(View.GONE);
                        binding.txtNoDataFound.setVisibility(View.VISIBLE);
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

    private void loadListAdapter(List<ClaimListApiResponse.Datum> list) {
        if (list.size() > 0) {
            binding.rvItemList.setVisibility(View.VISIBLE);
            binding.txtNoDataFound.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            binding.rvItemList.setLayoutManager(layoutManager);
            CliamGetListAdapter adapter = new CliamGetListAdapter(activity, list, new ClaimOnClick() {
                @Override
                public void onClickEdit(String uuid,String claim_for_label,String claim_for,String amount,String proof,
                                        String status) {

                    Bundle bundle = new Bundle();
                    bundle.putString("from", "edit");
                    bundle.putString("uuid", uuid);
                    bundle.putString("claim_for_label", claim_for_label);
                    bundle.putString("claim_for", claim_for);
                    bundle.putString("amount", amount);
                    bundle.putString("proof", proof);
                    bundle.putString("status", status);
                    CommonFunctions.getInstance().newIntent(activity, CreateClaimActivity.class,bundle,false,false);

                }

                @Override
                public void onClickDelete(String uuid) {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_pic);
                   // dialog.setTitle("Are You Sure Want to Delete this Claim ?");
                    dialog.setCancelable(false);

                    TextView txtHeader = dialog.findViewById(R.id.txtHeader);
                    txtHeader.setText("Are You Sure Want to Delete this Claim ?");
                    TextView txtNo = dialog.findViewById(R.id.txtNo);
                    TextView txtYes = dialog.findViewById(R.id.txtYes);

                    dialog.show();
                    txtNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    txtYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                            CommonApiCalls.getInstance().claimDelete(activity,uuid, new CommonCallback.Listener() {
                                @Override
                                public void onSuccess(Object body) {
                                    ClaimDeleteApiResponse data = (ClaimDeleteApiResponse) body;
                                    Toast.makeText(activity,data.getMessage(),Toast.LENGTH_SHORT).show();
                                    getList();
                                }

                                @Override
                                public void onFailure(String reason) {
                                    Toast.makeText(activity,reason,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
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
        page = 1;
        getList();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(activity);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        uploadImage = destination;

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // binding.imgPic.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());

                try {
                    uploadImage = new File(Environment.getExternalStorageDirectory() + File.separator + data.getData());
                    uploadImage.createNewFile();

//Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = new FileOutputStream(uploadImage);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       // binding.imgPic.setImageBitmap(bm);
    }


}
