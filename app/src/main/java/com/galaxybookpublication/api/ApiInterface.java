package com.galaxybookpublication.api;

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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET(Urls.CLIENT_LIST)
    Call<ClientListApiResponse> client_list(@Query("page") String page,
                                            @Query("name") String name);


    @GET(Urls.PAYMENT_ORDER_LIST)
    Call<PaymentOrderListApiResponse> payment_order_list(@Path("client_id") String client_id);


    @GET(Urls.SPECIMEN_LIST)
    Call<SpecimenListApiResponse> specimen_list();

    @GET(Urls.BOOK_LIST)
    Call<BookListApiResponse> book_list();

    @GET(Urls.DISTRICT_LIST)
    Call<DistrictListApiResponse> district_list();



    @Multipart
    @POST(Urls.SPECIMEN_LIST)
    Call<SpecimenDataApiResponse> specimen_data(@Part MultipartBody.Part file,
                                                @Part("school_name") RequestBody Part,
                                                @Part("book_id") RequestBody book_id,
                                                @Part("district_id") RequestBody district_id,
                                                @Part("taluk") RequestBody taluk,
                                                @Part("pincode") RequestBody pincode,
                                                @Part("remarks") RequestBody remarks,
                                                @Part("latitude") RequestBody latitude,
                                                @Part("longitude") RequestBody longitude,
                                                @Part("visited_feedback") RequestBody visited_feedback);


    @GET(Urls.CLAIM_OPTIONS)
    Call<ClaimOptionListApiResponse> claim_options();


    @GET(Urls.POST_CLAIM)
    Call<ClaimListApiResponse> claim_get_list(@Query("page") String page,
                                              @Query("claim_for") String claim_for,
                                              @Query("from_date") String from_date,
                                              @Query("to_date") String to_date);

    @GET(Urls.GET_APPOINTMENT)
    Call<AppointmentListApiResponse> appointment_get_list(@Query("page") String page,
                                                          @Query("from_date") String from_date,
                                                          @Query("to_date") String to_date);


    @Multipart
    @POST(Urls.POST_CLAIM)
    Call<ClaimUploadApiResponse> claim_upload(@Part MultipartBody.Part file,
                                              @Part("claim_for") RequestBody claim_for,
                                              @Part("amount") RequestBody amount);



    @Multipart
    @POST(Urls.CLAIM_EDIT)
    Call<ClaimEditApiResponse> claim_edit(@Part MultipartBody.Part file,
                                          @Part("claim_for") RequestBody claim_for,
                                          @Part("amount") RequestBody amount,
                                          @Part("_method") RequestBody _method,
                                          @Path("id") String id);



    @DELETE(Urls.DELETE_CLAIM)
    Call<ClaimDeleteApiResponse> claim_delete(@Path("id") String id);


    @GET(Urls.PAYMENT_PDF_DOWNLOAD)
    Call<PdfLinkApiResponse> payment_pdf_download(@Path("uuid") String uuid);


}
