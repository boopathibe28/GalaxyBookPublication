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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxybookpublication.R;
import com.galaxybookpublication.adapter.BookListAdapter;
import com.galaxybookpublication.adapter.DistrictListAdapter;
import com.galaxybookpublication.api.CommonApiCalls;
import com.galaxybookpublication.api.CommonCallback;
import com.galaxybookpublication.api.GPSTracker;
import com.galaxybookpublication.databinding.ActivitySpecimenCreateBinding;
import com.galaxybookpublication.intrefaces.BookListOnClick;
import com.galaxybookpublication.intrefaces.BookOnClick;
import com.galaxybookpublication.modelapi.BookListApiResponse;
import com.galaxybookpublication.modelapi.DistrictListApiResponse;
import com.galaxybookpublication.modelapi.SpecimenDataApiResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SpecimenCreateActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySpecimenCreateBinding binding;
    RecyclerView bookRvList;
    TextView bookTxtNoDataFound;
    String bookName = "";
    ArrayList<String> bookKey;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    File uploadImage = null;
    double cLatitude = 0.0;
    double cLongitude = 0.0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_specimen_create);

        initialView();
        bookKey = new ArrayList<>();
        checkAndRequestPermissions(SpecimenCreateActivity.this);

        GPSTracker gpsTracker = new GPSTracker(SpecimenCreateActivity.this);
         cLatitude = gpsTracker.getLatitude();
         cLongitude = gpsTracker.getLongitude();

    }

    private void initialView() {
        binding.imgBack.setOnClickListener(this);

        binding.lyoutBook.setOnClickListener(this);
        binding.edtBook.setOnClickListener(this);
        binding.imgBookDown.setOnClickListener(this);

        binding.lyoutDistrict.setOnClickListener(this);
        binding.edtDistrict.setOnClickListener(this);
        binding.imgDistrictDown.setOnClickListener(this);

        binding.txtCreate.setOnClickListener(this);
        binding.lyoutVisitedEvidence.setOnClickListener(this);
        binding.edtVisitedEvidence.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.imgBack){
            finish();
        }
        else if (view == binding.lyoutBook || view == binding.edtBook || view == binding.imgBookDown){
            CommonApiCalls.getInstance().getBookList(SpecimenCreateActivity.this,new CommonCallback.Listener() {
                @Override
                public void onSuccess(Object body) {
                    BookListApiResponse response = (BookListApiResponse) body;

                    Dialog dialog = new Dialog(SpecimenCreateActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_book_list);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationCombo;
                    dialog.setCancelable(false);

                    ImageView imgClose = dialog.findViewById(R.id.imgClose);
                    TextView txtHeader = dialog.findViewById(R.id.txtHeader);
                    TextView txtSubmit = dialog.findViewById(R.id.txtSubmit);
                     bookRvList = dialog.findViewById(R.id.rvList);
                     bookTxtNoDataFound = dialog.findViewById(R.id.txtNoDataFound);
                    txtHeader.setText("Select Book");

                    dialog.show();

                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    txtSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            binding.edtBook.setText(bookName);
                        }
                    });

                    if (response.getData().size() > 0){
                       bookAdapter(response.getData());
                    }
                    else {
                        bookRvList.setVisibility(View.GONE);
                        bookTxtNoDataFound.setVisibility(View.VISIBLE);
                    }

                }
                @Override
                public void onFailure(String reason) {
                    Toast.makeText(SpecimenCreateActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if (view == binding.lyoutDistrict || view == binding.edtDistrict || view == binding.imgDistrictDown){
            CommonApiCalls.getInstance().getDistrictList(SpecimenCreateActivity.this,new CommonCallback.Listener() {
                @Override
                public void onSuccess(Object body) {
                    DistrictListApiResponse response = (DistrictListApiResponse) body;

                    Dialog dialog = new Dialog(SpecimenCreateActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_list);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationCombo;
                    dialog.setCancelable(false);

                    ImageView imgClose = dialog.findViewById(R.id.imgClose);
                    TextView txtHeader = dialog.findViewById(R.id.txtHeader);
                    RecyclerView rvList = dialog.findViewById(R.id.rvList);
                    TextView txtNoDataFound = dialog.findViewById(R.id.txtNoDataFound);
                    txtHeader.setText("Select District");

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

                        rvList.setLayoutManager(new GridLayoutManager(SpecimenCreateActivity.this, 1));
                        DistrictListAdapter adapter = new DistrictListAdapter(SpecimenCreateActivity.this, response.getData(), new BookOnClick() {
                            @Override
                            public void onClickView(String key,String name) {
                                binding.edtDistrict.setText(name);
                                binding.edtDistrict.setTag(key);

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
                    Toast.makeText(SpecimenCreateActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (view == binding.lyoutVisitedEvidence || view == binding.edtVisitedEvidence){
            /*if(checkAndRequestPermissions(SpecimenCreateActivity.this)){
                chooseImage(SpecimenCreateActivity.this);
            }*/
            chooseImage(SpecimenCreateActivity.this);
        }
        else if (view == binding.txtCreate){
            if (binding.edtSchoolName.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Enter valid School Name", Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtBook.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Select valid Book", Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtDistrict.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Select valid District", Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtTaluk.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Enter valid Taluk", Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtPincode.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Enter valid Pincode", Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtRemarks.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Enter valid Remarks", Toast.LENGTH_SHORT).show();
            }
            else if (binding.edtVisitedFeedback.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Enter valid Feedback", Toast.LENGTH_SHORT).show();
            }
            else if (uploadImage == null){
                Toast.makeText(this,"Kindly select valid Visited Evidence",Toast.LENGTH_SHORT).show();
            }
            else {
                String book_key = "";
                if (bookKey.size() > 0){
                    for (int i = 0; i < bookKey.size(); i++) {
                        if (i == 0){
                            book_key = bookKey.get(i);
                        }
                        else {
                            book_key = book_key+","+bookKey.get(i);
                        }
                    }
                }

                CommonApiCalls.getInstance().specimenDataCreate(SpecimenCreateActivity.this,
                        binding.edtSchoolName.getText().toString().trim(),
                        book_key,
                        binding.edtDistrict.getTag().toString().trim(),
                        binding.edtTaluk.getText().toString().trim(),
                        binding.edtPincode.getText().toString().trim(),
                        binding.edtRemarks.getText().toString().trim(),cLatitude+"",cLongitude+"",
                        binding.edtVisitedFeedback.getText().toString().trim(),uploadImage,
                        new CommonCallback.Listener() {
                    @Override
                    public void onSuccess(Object body) {
                        SpecimenDataApiResponse response = (SpecimenDataApiResponse) body;
                        Toast.makeText(SpecimenCreateActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(SpecimenCreateActivity.this, reason, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void bookAdapter(List<BookListApiResponse.Datum> data) {
        bookRvList.setVisibility(View.VISIBLE);
        bookTxtNoDataFound.setVisibility(View.GONE);

        bookRvList.setLayoutManager(new GridLayoutManager(SpecimenCreateActivity.this, 1));
        BookListAdapter adapter = new BookListAdapter(SpecimenCreateActivity.this,bookKey, data, new BookListOnClick() {
            @Override
            public void onClickView(String key,String name,String addOrRemove) {
                if (addOrRemove.equals("add")) {
                    bookName = bookName + "\n" + name;
                }
                else if (addOrRemove.equals("remove")){
                    String[] split = bookName.split("\n");

                    for (int i = 0; i < split.length; i++) {
                        if (split[i].equals(name)){
                            bookName = bookName.replace("\n"+name,"");
                        }
                    }
                }

                bookAdapter(data);
            }
        });
        bookRvList.setAdapter(adapter);
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
                if (ContextCompat.checkSelfPermission(SpecimenCreateActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();

                } else if (ContextCompat.checkSelfPermission(SpecimenCreateActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    chooseImage(SpecimenCreateActivity.this);
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
        File filesDir = SpecimenCreateActivity.this.getFilesDir();
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
