package com.galaxybookpublication.api;

import static com.galaxybookpublication.api.MyApplication.context;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;



public class CommonFunctions {
    public static String CheckStatus = "";
    public static String fromPage = "";
    public static String isFromAppointments = "";
    public static String Auth = "";
    private static CommonFunctions ourInstance = new CommonFunctions();

    private CommonFunctions() {
    }

    public static CommonFunctions getInstance() {
        if (ourInstance == null) {
            synchronized (CommonFunctions.class) {
                if (ourInstance == null)
                    ourInstance = new CommonFunctions();
            }
        }
        return ourInstance;
    }


    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Hide actionbar from activity class
     *
     * @param actionbar
     */
    public void hideActionBar(ActionBar actionbar) {
        if (actionbar != null) {
            actionbar.hide();
        }
    }

    /**
     * @param context          Context fromactivity or fragment
     * @param bundle           Bundle of values for next Activity
     * @param destinationClass Destination Activity
     * @param isFinish         Current activity need to finish or not
     */
    public void newIntent(Context context, Class destinationClass, Bundle bundle, boolean isFinish, boolean isFlags) {
        if (!isActivityRunning(context)) {
            return;
        }
        Intent intent = new Intent(context, destinationClass);
        intent.putExtras(bundle);
        if (isFlags) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (isFinish) {
            ((Activity) context).finish();
        }
    }

    public void newIntent(Context context, Class destinationClass, Bundle bundle) {
        if (!isActivityRunning(context)) {
            return;
        }
        Intent intent = new Intent(context, destinationClass);
        intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);

    }

    /**
     * new intent with result get called here
     *
     * @param context
     * @param destinationClass
     * @param bundle
     * @param isFinish
     * @param requestCode
     */
    public void newIntentForResult(Context context, Class destinationClass, Bundle bundle, boolean isFinish, int requestCode) {
        Intent intent = new Intent(context, destinationClass);
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent, requestCode);
        if (isFinish) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((Activity) context).finish();
        }
    }

    public void EmptyField(Context context, String message) {
        String result = MessageFormat.format("Cannot Be Empty", message);
        Toast mdToast = Toast.makeText(context, result, Toast.LENGTH_LONG);
        mdToast.show();
    }

    /**
     * This method will help to find the
     * activity is running or not
     *
     * @param ctx
     * @return
     */
    public boolean isActivityRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    public static boolean CheckInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }



    /**
     * When the Api throw an error this function will call
     * <p>
     * *** Uses
     * * Dismiss Dialog
     * * Print stack
     * * Return error block
     * * Display error
     *
     * @param context
     * @param errorBody
     * @param listener
     */
    public void apiErrorConverter(Context context, Object errorBody, CommonCallback.Listener listener) {
        try {
            InputStream inputStream = ((ResponseBody) errorBody).byteStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new GsonBuilder().create();
            ErrorApiResponse mError = gson.fromJson(bufferedReader.readLine(), ErrorApiResponse.class);
//            CommonFunctions.getInstance().displayKnownError(context, mError.getMessage());

           // validationError(context, mError.getMessage());

            listener.onFailure(mError.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
           // validationError(context, LanguageConstants.somethingWentWrong);
            listener.onFailure("Something Went Wrong");
        }
        CustomProgressDialog.getInstance().dismiss();
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // Mobile number validation
    public boolean isValidMobile(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            return Patterns.PHONE.matcher(mobile).matches();
        }
        return false;
    }


    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    private void updateLocale(@NonNull Context context, @NonNull Locale locale) {
        final Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }





    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public void loadImageByFresco(SimpleDraweeView sdView, String url) {
        try {
            url = url.startsWith("http") ? url : Urls.BASE_URL + url;
            Uri uri = Uri.parse(url);

            int width = 200, height = 200;
            //sdView.getHierarchy().setPlaceholderImage(R.drawable.ic_no_image);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .disableDiskCache()
                    .setRequestPriority(Priority.HIGH)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(sdView.getController())
                    .setImageRequest(request)
                    .build();
            sdView.setController(controller);
             sdView.getHierarchy().setProgressBarImage(new CircleProgressDrawable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImageByFrescodrawble(SimpleDraweeView sdView, Integer resId) {
        try {
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                    .path(String.valueOf(resId))
                    .build();
            sdView.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
