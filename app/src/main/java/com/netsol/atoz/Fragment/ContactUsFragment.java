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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.netsol.atoz.Adapter.SpinnerCountryAdapter;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;
import com.netsol.atoz.Util.TextDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 10/31/17.
 */

public class ContactUsFragment extends Fragment implements AlertAction {
    String contactText;
    Button post;
    Spinner contactType;
    Context context;
    EditText name;
    EditText mobileNo;
    EditText email;
    EditText subject;
    EditText message;
    private Helper helper;
    JsonParser jsonParser;
    SharedPreferences sharedPref;
    WebserviceManager _webserviceManager;
    ArrayList<String> contactTypeList;
    public static boolean contactError = false;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContactUsFragment() {
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        context = super.getContext();
        _webserviceManager = new WebserviceManager(context);
        helper = new Helper(context, this);
        jsonParser = new JsonParser(context);
        name = (EditText)view.findViewById(R.id.contact_name);
        mobileNo = (EditText)view.findViewById(R.id.contact_mobile);
        email = (EditText)view.findViewById(R.id.contact_email);
        subject = (EditText)view.findViewById(R.id.contact_subject);
        message = (EditText)view.findViewById(R.id.contact_message);
        contactType = (Spinner) view.findViewById(R.id.contact_type);
        post = (Button) view.findViewById(R.id.button_contact_comments);
        ImageView followFb = (ImageView) view.findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) view.findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) view.findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) view.findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) view.findViewById(R.id.follow_cam);
        setHasOptionsMenu(true);

        sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        mobileNo.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(Constants.COUNTRY_CODE, context), null, null, null);

        contactTypeList = new ArrayList<>();
        Collections.addAll(contactTypeList, context.getResources().getStringArray(R.array.contact_type_value));
        SpinnerCountryAdapter locationAdapter = new SpinnerCountryAdapter(context, 0, contactTypeList);
        contactType.setAdapter(locationAdapter);

        contactType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contactText = contactTypeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1 = name.getText().toString();
                String mobie = mobileNo.getText().toString();
                String emailText = email.getText().toString();
                mobie = Constants.COUNTRY_CODE + mobie;
                String subject1 = subject.getText().toString();
                String desc = message.getText().toString();
                if ((name.length() <= 0 || mobileNo.length() <= 0 || subject.length() <= 0
                        || message.length() <= 0) || email.length() <= 0 || contactText.equalsIgnoreCase("Set Query")){
                    Toast.makeText(context, context.getString(R.string.all_fields_mandatory), Toast.LENGTH_LONG).show();
                } else if (mobie.length() < 13) {
                    Toast.makeText(context, context.getString(R.string.invalid_mobile), Toast.LENGTH_LONG).show();
                } else if (!Helper.checkEmail(emailText)) {
                    Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
                } else {
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", name1)
                            .add("mobile", mobie)
                            .add("email", emailText)
                            .add("type", contactText)
                            .add("subject", subject1)
                            .add("message", desc)
                            .build();

                    new ContactUsFragment.PostContactCall().execute(this, "post", formBody);
                }
            }
        });

        mobileNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void afterTextChanged(Editable s) {

                // Remove spacing char
                if (s.length() > 0 && s.length() == 3) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && s.length() == 3) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
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

    private class PostContactCall extends AsyncTask<Object, Void, Object> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.post_contact));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_CONTACT;
            RequestBody requestParam = (RequestBody) params[2];
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String parseUpdate = jsonParser.parseContact(result.toString());
                helper.cancelableAlertDialog("", parseUpdate, 1);
            } else {
                contactError = true;
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }
}
