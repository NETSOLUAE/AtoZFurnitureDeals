package com.netsol.atoz.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;

import com.netsol.atoz.R;
import com.netsol.atoz.Util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by macmini on 12/10/17.
 */

public class WebserviceManager {
    private Context context;
    JsonParser jsonParser;

    public WebserviceManager(Context context) {
        this.context = context;
        jsonParser = new JsonParser(context);
    }

    public Object getHttpResponse(String url, boolean innerParse) {
//        OkHttpClient httpClient = getUnsafeOkHttpClient();
        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        httpClient = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (innerParse) {
                if (url.contains(Constants.END_POINT_CATEGORIES)) {
                    return jsonParser.parseCategoryDetails(response.body().string());
                } else if (url.contains(Constants.END_POINT_HOME)) {
                    return jsonParser.parseHomeDetails(response.body().string());
                } else if (url.contains(Constants.END_POINT_PRODUCTS) || url.contains(Constants.END_POINT_PRODUCT)){
                    return jsonParser.parseProductDetails(response.body().string());
                } else if (url.contains(Constants.END_POINT_ADDRESS)){
                    return jsonParser.parseAddressResponse(response.body().string());
                } else if (url.contains(Constants.END_POINT_ORDER)){
                    return jsonParser.parseOrderGroup(response.body().string());
                } else if (url.contains(Constants.END_POINT_AREA)){
                    return jsonParser.parseAreaDetails(response.body().string());
                } else if (url.contains(Constants.END_POINT_NATIONALITY)){
                    return jsonParser.parseNationalityDetails(response.body().string());
                } else if (url.contains(Constants.END_POINT_ORDER_COMPLETE)){
                    return jsonParser.parseSaveOrder(response.body().string(), "credit");
                } else if (url.contains(Constants.END_POINT_SEARCH_RESULT)){
                    return jsonParser.parseSearchProductDetails(response.body().string());
                } else if (url.contains(Constants.END_POINT_FAQ)){
                    return jsonParser.parseFaq(response.body().string());
                } else {
                    return response.body().string();
                }
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "error in getting response get request okhttp");
        }
        return null;
    }

    public Object getHttpResponseAllProducts(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        httpClient = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            return jsonParser.parseProductDetails(response.body().string());
        } catch (IOException e) {
            Log.e(TAG, "error in getting response get request okhttp");
        }
        return null;
    }

    public Object getQueryHttpResponse(String requestParam) {
        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl.Builder httpBuider = HttpUrl.parse(Constants.BASE_URL).newBuilder();
        httpBuider.addQueryParameter("coupon", requestParam);

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "error in getting response get request with query string okhttp");
        }
        return null;
    }

    public Object postHttpResponse(String url, RequestBody formBody) {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e(TAG, "Got response from server using OkHttp ");
                return response.body().string();
            }

        } catch (IOException e) {
            Log.e(TAG, "error in getting response post request okhttp");
        }
        return null;

    }

    public void getAsyncCall(){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.BASE_URL)
                .addHeader("CLIENT", "AD")
                .addHeader("USERID", "343")
                .build();

        //okhttp asynchronous call
        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(TAG, "error in getting response using async okhttp call");
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    throw new IOException("Error response " + response);
                }

                Log.i(TAG,responseBody.string());
            }
        });
    }

    public Object postJson(String url, String jsonStr){

        final MediaType mediaType
                = MediaType.parse("application/json");

        OkHttpClient httpClient = new OkHttpClient();

//        String jsonStr = "{\"coupon\":\"upto 20% off\", \"selectedDate\" : \"20/11/2016\"}";

        //post json using okhttp
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, jsonStr))
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e(TAG, "Got response from server for JSON post using OkHttp ");
                return jsonParser.parseSaveOrder(response.body().string(), "review");
            } else {
                return response.body().string();
            }

        } catch (IOException e) {
            Log.e(TAG, "error in getting response for json post request okhttp");
        }
        return null;
    }

    public Object multipart(String url, String uriString, String filepath){
        OkHttpClient httpClient = new OkHttpClient();
        //creates multipart http requests
        Uri filePathUri = Uri.parse(filepath);
        String fileNamefromUri = filePathUri.getLastPathSegment();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_photo", fileNamefromUri,
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(filepath)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e(TAG, "Got response from server for multipart request using OkHttp ");
                return jsonParser.parseUploadProfile(response.body().string());
            }

        } catch (IOException e) {
            Log.e(TAG, "error in getting response for multipart request okhttp");
        }
        return null;
    }

    public String sendUserProfile (String urlString, String filepath, String userID) {
        String result = "";
        URL url = null;
        HttpURLConnection connection = null;
        String query = urlString;
        String charset = "UTF-8";
        File sourceFile = new File(filepath);
        sourceFile = compressFile(sourceFile, context);

        try {
            FileUploader multipart = new FileUploader(query, charset);

            multipart.addFilePart("user_photo", sourceFile);
//            multipart.addFormField("user_id", userID);

            String response = multipart.finish();
            result = response;
//            result = String.valueOf(response);
            System.out.println("SERVER REPLIED:");

//            for (String line : response) {
//                System.out.println(line);
//            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static File compressFile(File file, Context context) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
//        try {
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[] {
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return new java.security.cert.X509Certificate[]{};
//                        }
//                    }
//            };
//
//            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.connectTimeout(60, TimeUnit.SECONDS);
//            builder.readTimeout(60, TimeUnit.SECONDS);
//            builder.writeTimeout(60, TimeUnit.SECONDS);
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
//            builder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//
//            OkHttpClient okHttpClient = builder.build();
//            return okHttpClient;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
