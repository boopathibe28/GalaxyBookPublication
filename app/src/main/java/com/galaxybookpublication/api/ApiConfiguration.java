package com.galaxybookpublication.api;


import static com.galaxybookpublication.api.CommonFunctions.Auth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfiguration {

    public String ProdOrDev = "";
    private static ApiConfiguration ourInstance = new ApiConfiguration();
    Retrofit mRetrofit;

    public static ApiConfiguration getInstance() {
        if (ourInstance == null) {
            synchronized (ApiConfiguration.class) {
                if (ourInstance == null)
                    ourInstance = new ApiConfiguration();
            }
        }
        ourInstance.config();
        return ourInstance;
    }
    private ApiConfiguration() {
    }

    private void config() {
        Gson gson = new GsonBuilder().setLenient().create();
        String BASE_URL = Urls.BASE_URL;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getRequestHeader(Auth))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private OkHttpClient getRequestHeader(final String token) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClientBuild = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", token)
                                .addHeader("Accept-Language","")
                                .build();
                        if (!CheckInternetConnection()) {
                            throw new NoConnectivityException();
                        } else {
                            Response response = chain.proceed(newRequest);
                            return response;
                        }
                    }
                })
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS);

        if (ProdOrDev != "") {
            okHttpClientBuild.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    HttpUrl url = request.url().newBuilder().addQueryParameter("env", "dev").build();
                    request = request.newBuilder().url(url).build();

                    if (!CheckInternetConnection()) {
                        throw new NoConnectivityException();
                    } else {
                        Response response = chain.proceed(request);
                        return response;
                    }
                }
            });
        }
        OkHttpClient okHttpClient = okHttpClientBuild.build();
        return okHttpClient;
    }

    public Retrofit getApiBuilder() {

        return mRetrofit;
    }

    public class NoConnectivityException extends IOException {
        @Override
        public String getMessage() {
            return "";
        }
    }

    public boolean CheckInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}