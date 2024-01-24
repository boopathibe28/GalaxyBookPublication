package com.galaxybookpublication.api;

import android.content.Context;

import com.galaxybookpublication.modelapi.AppointmentListApiResponse;
import com.galaxybookpublication.modelapi.BookListApiResponse;
import com.galaxybookpublication.modelapi.ClaimDeleteApiResponse;
import com.galaxybookpublication.modelapi.ClaimEditApiResponse;
import com.galaxybookpublication.modelapi.ClaimListApiResponse;
import com.galaxybookpublication.modelapi.ClaimOptionListApiResponse;
import com.galaxybookpublication.modelapi.ClaimUploadApiResponse;
import com.galaxybookpublication.modelapi.ClientListApiResponse;
import com.galaxybookpublication.modelapi.DistrictListApiResponse;
import com.galaxybookpublication.modelapi.PaymentOrderListApiResponse;
import com.galaxybookpublication.modelapi.PdfLinkApiResponse;
import com.galaxybookpublication.modelapi.SpecimenDataApiResponse;
import com.galaxybookpublication.modelapi.SpecimenListApiResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonApiCalls {
    private static final CommonApiCalls ourInstance = new CommonApiCalls();

    private CommonApiCalls() {
    }

    public static CommonApiCalls getInstance() {
        return ourInstance;
    }



    // ----- GET - Client List
    public void clientList(final Context context, int page,String queryName, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<ClientListApiResponse> call = apiInterface.client_list(page+"",queryName);
            call.enqueue(new Callback<ClientListApiResponse>() {
                @Override
                public void onResponse(Call<ClientListApiResponse> call, Response<ClientListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ClientListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }




    // ----- GET - Payment Order List
    public void paymentOrderList(final Context context, String client_id, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<PaymentOrderListApiResponse> call = apiInterface.payment_order_list(client_id);
            call.enqueue(new Callback<PaymentOrderListApiResponse>() {
                @Override
                public void onResponse(Call<PaymentOrderListApiResponse> call, Response<PaymentOrderListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<PaymentOrderListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }


    // ----- GET - Specimen List
    public void specimenList(final Context context, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<SpecimenListApiResponse> call = apiInterface.specimen_list();
            call.enqueue(new Callback<SpecimenListApiResponse>() {
                @Override
                public void onResponse(Call<SpecimenListApiResponse> call, Response<SpecimenListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<SpecimenListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }


    // ----- GET - Book List
    public void getBookList(final Context context, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<BookListApiResponse> call = apiInterface.book_list();
            call.enqueue(new Callback<BookListApiResponse>() {
                @Override
                public void onResponse(Call<BookListApiResponse> call, Response<BookListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<BookListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }



    // ----- GET - Book List
    public void getDistrictList(final Context context, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<DistrictListApiResponse> call = apiInterface.district_list();
            call.enqueue(new Callback<DistrictListApiResponse>() {
                @Override
                public void onResponse(Call<DistrictListApiResponse> call, Response<DistrictListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<DistrictListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }



    // ----- POST - Specimen Data Create
    public void specimenDataCreate(final Context context,String school_name,String book_id,
                                   String district_id,String taluk,String pincode,String remarks,String latitude,String longitude,
                                   String visited_feedback,File file,
                                   final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);


            MultipartBody.Part body = null;
            if (file != null && file.exists()) {
                RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body = MultipartBody.Part.createFormData("visited_evidence", file.getName(), fbody);
            }

            RequestBody school_name_ = RequestBody.create(MediaType.parse("text/plain"), school_name);
            RequestBody book_id_ = RequestBody.create(MediaType.parse("text/plain"), book_id);
            RequestBody district_id_ = RequestBody.create(MediaType.parse("text/plain"), district_id);
            RequestBody taluk_ = RequestBody.create(MediaType.parse("text/plain"), taluk);
            RequestBody pincode_ = RequestBody.create(MediaType.parse("text/plain"), pincode);
            RequestBody remarks_ = RequestBody.create(MediaType.parse("text/plain"), remarks);
            RequestBody latitude_ = RequestBody.create(MediaType.parse("text/plain"), latitude);
            RequestBody longitude_ = RequestBody.create(MediaType.parse("text/plain"), longitude);
            RequestBody visited_feedback_ = RequestBody.create(MediaType.parse("text/plain"), visited_feedback);

            Call<SpecimenDataApiResponse> call = apiInterface.specimen_data(body,school_name_,book_id_,district_id_,taluk_,
                    pincode_,remarks_,latitude_,longitude_,visited_feedback_);
            call.enqueue(new Callback<SpecimenDataApiResponse>() {
                @Override
                public void onResponse(Call<SpecimenDataApiResponse> call, Response<SpecimenDataApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<SpecimenDataApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }



    // ----- GET - Claim List
    public void claimList(final Context context, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<ClaimOptionListApiResponse> call = apiInterface.claim_options();
            call.enqueue(new Callback<ClaimOptionListApiResponse>() {
                @Override
                public void onResponse(Call<ClaimOptionListApiResponse> call, Response<ClaimOptionListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ClaimOptionListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }


    // ----- GET - appointment get List
    public void appointmentGetList(final Context context,int page,String Filter_FromDate,String Filter_ToDate, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<AppointmentListApiResponse> call = apiInterface.appointment_get_list(page+"",Filter_FromDate,Filter_ToDate);
            call.enqueue(new Callback<AppointmentListApiResponse>() {
                @Override
                public void onResponse(Call<AppointmentListApiResponse> call, Response<AppointmentListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<AppointmentListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }


    // ----- GET - Claim get List
    public void claimGetList(final Context context,int page,String Filter_ClaimFor,String Filter_FromDate,String Filter_ToDate, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<ClaimListApiResponse> call = apiInterface.claim_get_list(page+"",Filter_ClaimFor,Filter_FromDate,Filter_ToDate);
            call.enqueue(new Callback<ClaimListApiResponse>() {
                @Override
                public void onResponse(Call<ClaimListApiResponse> call, Response<ClaimListApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ClaimListApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }



    public void claimUpload(final Context context, File file, String id,String amount, final CommonCallback.Listener listener) {

        if (!CustomProgressDialog.getInstance().isShowing()) {
            CustomProgressDialog.getInstance().show(context);
        }

        MultipartBody.Part body = null;
        if (file != null && file.exists()) {
            RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("proof", file.getName(), fbody);
        }

        RequestBody id_ = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody amount_ = RequestBody.create(MediaType.parse("text/plain"), amount);

        ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
        Call<ClaimUploadApiResponse> call = apiInterface.claim_upload(body,id_,amount_);

        call.enqueue(new Callback<ClaimUploadApiResponse>() {
            @Override
            public void onResponse(Call<ClaimUploadApiResponse> call, Response<ClaimUploadApiResponse> response) {
                CustomProgressDialog.getInstance().dismiss();
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ClaimUploadApiResponse> call, Throwable t) {
                t.printStackTrace();
               // CommonFunctions.getInstance().apiErrorConverter(t.getMessage(), listener);
            }
        });
    }



    public void claimEdit(final Context context,String method,String uuid, File file, String claim_id,String amount, final CommonCallback.Listener listener) {

        if (!CustomProgressDialog.getInstance().isShowing()) {
            CustomProgressDialog.getInstance().show(context);
        }

        MultipartBody.Part body = null;
        if (file != null && file.exists()) {
            RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("proof", file.getName(), fbody);
        }

        RequestBody method_ = RequestBody.create(MediaType.parse("text/plain"), method);
        RequestBody claim_id_ = RequestBody.create(MediaType.parse("text/plain"), claim_id);
        RequestBody amount_ = RequestBody.create(MediaType.parse("text/plain"), amount);

        ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
        Call<ClaimEditApiResponse> call = apiInterface.claim_edit(body,claim_id_,amount_,method_,uuid);

        call.enqueue(new Callback<ClaimEditApiResponse>() {
            @Override
            public void onResponse(Call<ClaimEditApiResponse> call, Response<ClaimEditApiResponse> response) {
                CustomProgressDialog.getInstance().dismiss();
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ClaimEditApiResponse> call, Throwable t) {
                t.printStackTrace();
                // CommonFunctions.getInstance().apiErrorConverter(t.getMessage(), listener);
            }
        });
    }




    // ----- DELETE - Claim Delete
    public void claimDelete(final Context context,String id, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<ClaimDeleteApiResponse> call = apiInterface.claim_delete(id);
            call.enqueue(new Callback<ClaimDeleteApiResponse>() {
                @Override
                public void onResponse(Call<ClaimDeleteApiResponse> call, Response<ClaimDeleteApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ClaimDeleteApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }



    // ----- POST - PDF
    public void paymentReceiptDownload(final Context context,String uuid, final CommonCallback.Listener listener) {
        if (CommonFunctions.getInstance().isNetworkConnected()) {
            if (!CustomProgressDialog.getInstance().isShowing()) {
                CustomProgressDialog.getInstance().show(context);
            }
            ApiInterface apiInterface = ApiConfiguration.getInstance().getApiBuilder().create(ApiInterface.class);
            Call<PdfLinkApiResponse> call = apiInterface.payment_pdf_download(uuid);
            call.enqueue(new Callback<PdfLinkApiResponse>() {
                @Override
                public void onResponse(Call<PdfLinkApiResponse> call, Response<PdfLinkApiResponse> response) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure(response.message());
                    }
                    if (CustomProgressDialog.getInstance().isShowing()) {
                        CustomProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<PdfLinkApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    CommonFunctions.getInstance().apiErrorConverter(context,t.getMessage(), listener);

                }
            });
        }
        else {
            NoInternetConnectionDialog.getInstance().showDialog(context);
        }

    }



}
