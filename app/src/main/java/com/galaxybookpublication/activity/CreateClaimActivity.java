package com.galaxybookpublication.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.galaxybookpublication.R;
import com.galaxybookpublication.adapter.ClaimListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.databinding.ActivityCreateClaimBinding;
import com.galaxybookpublication.intrefaces.BookOnClick;
import com.galaxybookpublication.modelapi.ClaimEditApiResponse;
import com.galaxybookpublication.modelapi.ClaimOptionListApiResponse;
import com.galaxybookpublication.modelapi.ClaimUploadApiResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateClaimActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityCreateClaimBinding binding;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    File uploadImage = null;
    String from = "";
    String uuid = "";
    String status = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_claim);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_claim);
        if (getIntent().getExtras() != null) {
            from = getIntent().getExtras().getString("from");

            if (from.equals("edit")){
                uuid = getIntent().getExtras().getString("uuid");
                String claim_for_label = getIntent().getExtras().getString("claim_for_label");
                String claim_for = getIntent().getExtras().getString("claim_for");
                String amount = getIntent().getExtras().getString("amount");
                String proof = getIntent().getExtras().getString("proof");
                status = getIntent().getExtras().getString("status");

                binding.edtClaimFor.setText(claim_for_label);
                binding.edtClaimFor.setTag(claim_for);

                binding.edtAmount.setText(amount);

             //   binding.edtProof.setText(proof);

                if (!proof.isEmpty()){
                    Glide.with(CreateClaimActivity.this).load(proof).into(binding.imgPic);
                }
            }
        }


        initialView();

        checkAndRequestPermissions(CreateClaimActivity.this);

    }

    private void initialView() {
        binding.imgBack.setOnClickListener(this);

        binding.lyoutClaimFor.setOnClickListener(this);
        binding.edtClaimFor.setOnClickListener(this);

        binding.lyoutProof.setOnClickListener(this);
        binding.edtProof.setOnClickListener(this);
        binding.txtSubmit.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if (view == binding.imgBack){
            finish();
        }
        else if (view == binding.lyoutClaimFor || view == binding.edtClaimFor){
            CommonApiCalls.getInstance().claimList(CreateClaimActivity.this,new CommonCallback.Listener() {
                @Override
                public void onSuccess(Object body) {
                    ClaimOptionListApiResponse response = (ClaimOptionListApiResponse) body;

                    Dialog dialog = new Dialog(CreateClaimActivity.this);
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

                        rvList.setLayoutManager(new GridLayoutManager(CreateClaimActivity.this, 1));
                        ClaimListAdapter adapter = new ClaimListAdapter(CreateClaimActivity.this, response.getData(), new BookOnClick() {
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
                    Toast.makeText(CreateClaimActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if (view == binding.lyoutProof || view == binding.edtProof){
            /*if(checkAndRequestPermissions(CreateClaimActivity.this)){
                chooseImage(CreateClaimActivity.this);
            }*/
            chooseImage(CreateClaimActivity.this);
        }
        else if (view == binding.txtSubmit){
            if (binding.edtClaimFor.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateClaimActivity.this,"Kindly select Claim For",Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtAmount.getText().toString().trim().isEmpty()){
                Toast.makeText(CreateClaimActivity.this,"Kindly enter valid Amount",Toast.LENGTH_SHORT).show();
            }
            else {
                if (from.equals("add")) {
                    if (uploadImage == null){
                        Toast.makeText(CreateClaimActivity.this,"Kindly select valid proof",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CommonApiCalls.getInstance().claimUpload(CreateClaimActivity.this, uploadImage, binding.edtClaimFor.getTag().toString().trim(), binding.edtAmount.getText().toString().trim(), new CommonCallback.Listener() {
                        @Override
                        public void onSuccess(Object body) {
                            ClaimUploadApiResponse response = (ClaimUploadApiResponse) body;
                            Toast.makeText(CreateClaimActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String reason) {
                            Toast.makeText(CreateClaimActivity.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (from.equals("edit")){
                    CommonApiCalls.getInstance().claimEdit(CreateClaimActivity.this,"PUT",uuid,uploadImage, binding.edtClaimFor.getTag().toString().trim(), binding.edtAmount.getText().toString().trim(), new CommonCallback.Listener() {
                        @Override
                        public void onSuccess(Object body) {
                            ClaimEditApiResponse response = (ClaimEditApiResponse) body;
                            Toast.makeText(CreateClaimActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String reason) {
                            Toast.makeText(CreateClaimActivity.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }



    private void chooseImage(Context context){

        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array

        // create a dialog for showing the optionsMenu

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set the items in builder

        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(optionsMenu[i].equals("Take Photo")){

                    // Open the camera and get the photo

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){

                    // choose from  external storage

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();
    }


    // function to check permission

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    // Handled permission Result


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(CreateClaimActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();

                } else if (ContextCompat.checkSelfPermission(CreateClaimActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    chooseImage(CreateClaimActivity.this);
                }
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        binding.imgPic.setImageBitmap(selectedImage);
                        persistImage(selectedImage,data.getDataString());
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                binding.imgPic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                persistImage(BitmapFactory.decodeFile(picturePath),picturePath);
                            }
                        }

                    }
                    break;
            }
        }
    }


    private void persistImage(Bitmap bitmap, String name) {
        File filesDir = CreateClaimActivity.this.getFilesDir();
        File imageFile = new File(filesDir, "Test" + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.flush();
            os.close();
            uploadImage = imageFile;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

}
