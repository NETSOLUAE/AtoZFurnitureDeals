package com.netsol.atoz.Fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.netsol.atoz.Activity.SigninActivity;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 10/31/17.
 */

public class ResetPasswordFragment extends Fragment implements AlertAction {
    String confirmNewPasswordText;
    String newPasswordText;
    String oldPasswordText;
    String currentEmail;
    String currentPassword;
    Button resetPassword;
    Context context;
    EditText oldPassword;
    EditText newPassword;
    EditText confirmNewPassword;
    JsonParser jsonParser;
    private Helper helper;
//    ProgressDialog progressDialog;
    WebserviceManager _webserviceManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public static boolean resetError = false;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyOrderFragment newInstance(String param1, String param2) {
        MyOrderFragment fragment = new MyOrderFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        setHasOptionsMenu(true);
        context = super.getContext();
        _webserviceManager = new WebserviceManager(context);
//        progressDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        jsonParser = new JsonParser(context);
        helper = new Helper(context, this);
        oldPassword = (EditText) view.findViewById(R.id.reset_old_password);
        newPassword = (EditText) view.findViewById(R.id.reset_new_password);
        confirmNewPassword = (EditText) view.findViewById(R.id.reset_confirm_passowrd);
        resetPassword = (Button) view.findViewById(R.id.button_reset_password);
        ImageView followFb = (ImageView) view.findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) view.findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) view.findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) view.findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) view.findViewById(R.id.follow_cam);
        sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPassword = sharedPref.getString(Constants.PASSWORD,"");
                currentEmail = sharedPref.getString(Constants.EMAIL,"");
                oldPasswordText = oldPassword.getText().toString();
                newPasswordText = newPassword.getText().toString();
                confirmNewPasswordText = confirmNewPassword.getText().toString();

                if (!oldPasswordText.equalsIgnoreCase(currentPassword)) {
                    Toast.makeText(context, context.getString(R.string.old_password_incorrect), Toast.LENGTH_SHORT).show();
                } else if (newPasswordText.equalsIgnoreCase(oldPasswordText)) {
                    Toast.makeText(context, context.getString(R.string.old_new_password_mismatch), Toast.LENGTH_SHORT).show();
                } else if (!newPasswordText.equalsIgnoreCase(confirmNewPasswordText)) {
                    Toast.makeText(context, context.getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
                } else if (oldPasswordText.length() < 6 || newPasswordText.length() < 6 || confirmNewPasswordText.length() < 6) {
                    Toast.makeText(context, context.getString(R.string.password_error_message), Toast.LENGTH_LONG).show();
                } else {
                    String userId = sharedPref.getString(Constants.USER_ID, "");
                    RequestBody formBody = new FormBody.Builder()
                            .add("curpass", oldPasswordText)
                            .add("newpass", newPasswordText)
                            .add("newrepass", confirmNewPasswordText)
                            .add("email", currentEmail)
                            .add("user_id", userId)
                            .build();

                    new ResetPasswordFragment.ResetPasswordCall().execute(this, "post", formBody);
                }
            }
        });
        /* Footer Action
         */
        followFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String socailLink=context.getString(R.string.follow_fb);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = helper.getFacebookUrl(getActivity(), socailLink);
                    if (facebookUrl == null || facebookUrl.length() == 0) {
                        Log.d("facebook Url", " is coming as " + facebookUrl);
                        return;
                    }
                    intent.setData(Uri.parse(facebookUrl));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_fb)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(getActivity(), "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
        followGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_google)) );
                intent.setPackage( "com.google.android.apps.plus" );
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    startActivity( intent );
                } else {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_google)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        followLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@" + "a-to-z-furniture-2aa36a156"));
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/a-to-z-furniture-2aa36a156"));
                final PackageManager packageManager = context.getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_linkendin)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    startActivity(intent);
                }
            }
        });
        followTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;
                try{
                    // Get Twitter app
                    context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + context.getString(R.string.follow_twitter_user_id)));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_twitter)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(getActivity(), "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
        followCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(context.getString(R.string.follow_cam_app));
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_cam)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(getActivity(), "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onOkClicked() {
        if (!resetError) {
            editor.putBoolean(Constants.SESSION, false);
            editor.putString(Constants.PASSWORD, confirmNewPasswordText);
            editor.apply();
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            resetError = false;
        }
    }

    @Override
    public void onCancelClicked() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class ResetPasswordCall extends AsyncTask<Object, Void, Object> {
        private ProgressDialog progressDialog;
        private String TAG = ResetPasswordFragment.ResetPasswordCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.reseting_password));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_RESET_PASSWORD;
            RequestBody requestParam = (RequestBody) params[2];

            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String parseUpdate = jsonParser.parseResetPassword(result.toString());
                helper.cancelableAlertDialog("", parseUpdate, 1);
            } else {
                resetError = true;
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }
}
