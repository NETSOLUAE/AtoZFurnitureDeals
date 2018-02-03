package com.netsol.atoz.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.netsol.atoz.R;

import java.util.regex.Pattern;

/**
 * Created by macmini on 10/28/17.
 */

public class Helper {
    private Context context;
    private AlertAction alertAction;
    private AlertDialog alertDialog;

    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public Helper(Context context, AlertAction alertAction) {
        this.context = context;
        this.alertAction = alertAction;
    }

    //Alert Dialog
    public void cancelableAlertDialog(String heading, String message, int buttonType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (buttonType == 2) {
            //1. create a dialog object 'dialog'
            alertDialog = builder
                    .setTitle(heading)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertAction.onOkClicked();
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            alertAction.onCancelClicked();
                        }
                    }).create();
        } else {
            //1. create a dialog object 'dialog'
            alertDialog = builder
                    .setTitle(heading)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertAction.onOkClicked();
                        }

                    }).create();

        }

        //2. now setup to change color of the button
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    public void nonCancelableAlertDialog(String heading, String message, int buttonType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (buttonType == 2) {
            //1. create a dialog object 'dialog'
            alertDialog = builder
                    .setTitle(heading)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertAction.onOkClicked();
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            alertAction.onCancelClicked();
                        }
                    }).create();
        } else {
            //1. create a dialog object 'dialog'
            alertDialog = builder
                    .setTitle(heading)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertAction.onOkClicked();
                        }

                    }).create();

        }

        //2. now setup to change color of the button
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    //Checking valid Email
    public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    //Checking valid Mobile Number
    public static boolean isValidMobile(String phone) {
        boolean check=false;
        int length = phone.length();
        check = length <= 12;
        return check;
    }

    //Getting rounded Bitmap
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    //Setting Badge Count for Cart
    public void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    //Dismiss Progress Dialog
    public void dismissProgressDialog(ProgressDialog progressDialog) {
        if ((progressDialog != null) && (progressDialog.isShowing())) {
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {
                Log.e(Constants.LOG_ATOZ, "Exception is " + Log.getStackTraceString(ex));
            }
        }
    }
    /**
     * Gets the corresponding path to a file from the given content:// URI
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public String getFilePathFromContentUri(Uri selectedVideoUri,
                                             ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    //Create MD5 hash value from String
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /* Changes and Darkens Status Bar Color.
       The public static function which can be called from other classes
       */
    public void darkenStatusBar(Activity activity, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(
                    darkenColor(
                            ContextCompat.getColor(activity, color)));
        }

    }

    // Code to darken the color supplied (mostly color of toolbar)
    private static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    //method to get the right URL to use in the intent
    public static String getFacebookUrl(Activity activity, String facebook_url) {
        if (activity == null || activity.isFinishing()) return null;

        PackageManager packageManager = activity.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                Log.d("facebook api", "new");
                return "fb://facewebmodal/f?href=" + facebook_url;
            } else { //older versions of fb app
                Log.d("facebook api", "old");
                return "fb://page/" + splitUrl(activity, facebook_url);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("facebook api", "exception");
            return facebook_url; //normal web url
        }
    }

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/187763038630619"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/atozfurnituredeals"));
        }
    }

    /***
     * this method used to get the facebook profile name only , this method split domain into two part index 0 contains https://www.facebook.com and index 1 contains after / part
     * @param context contain context
     * @param url contains facebook url like https://www.facebook.com/kfc
     * @return if it successfully split then return "kfc"
     *
     * if exception in splitting then return "https://www.facebook.com/kfc"
     *
     */
    public static String splitUrl(Context context, String url) {
        if (context == null) return null;
        Log.d("Split string: ", url + " ");
        try {
            String splittedUrl[] = url.split(".com/");
            Log.d("Split string: ", splittedUrl[1] + " ");
            return splittedUrl.length == 2 ? splittedUrl[1] : url;
        } catch (Exception ex) {
            return url;
        }
    }
}
