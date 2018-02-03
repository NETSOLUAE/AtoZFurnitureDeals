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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netsol.atoz.Activity.AddressActivity;
import com.netsol.atoz.Adapter.AddressAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.support.constraint.R.id.parent;

/**
 * Created by macmini on 10/31/17.
 */

public class ShippingAddressFragment extends Fragment implements AddressAdapter.OnAddressEdit, AddressAdapter.OnEnable, AlertAction {

    Context context;
    ListView addressList;
    ImageView addLocation;
    private Helper helper;
    JsonParser jsonParser;
    ArrayList<Address> addressArrayList;
    WebserviceManager _webserviceManager;
    DatabaseManager databaseManager;
    SharedPreferences sharedPref;
    AddressAdapter addressAdapter;
    private ProgressDialog progressDialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ShippingAddressFragment() {
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
        context = super.getContext();
        _webserviceManager = new WebserviceManager(context);
        helper = new Helper(context, this);
        jsonParser = new JsonParser(context);
        databaseManager = new DatabaseManager(context);
        sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_shippingaddress, container, false);
        addressList = (ListView) view.findViewById(R.id.address_list);
        addLocation = (ImageView) view.findViewById(R.id.button_add_location);
        ImageView followFb = (ImageView) view.findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) view.findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) view.findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) view.findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) view.findViewById(R.id.follow_cam);
        ImageView followPin = (ImageView) view.findViewById(R.id.follow_pintrest);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddressActivity.class);
                intent.putExtra("HEADING", "Add New Location");
                intent.putExtra("ACTIVITY", "false");
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);

        new ShippingAddressFragment.CityCall().execute(this, "post", "");

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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@" + "atozfurniture"));
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
        followPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_pintrest_app))));
                } catch (Exception e) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_pintrest))));
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnAddressEditClicked(String addressID) {
        Intent intent = new Intent(getActivity(), AddressActivity.class);
        intent.putExtra("HEADING", "Edit Location");
        intent.putExtra("ADDRESS_ID", addressID);
        intent.putExtra("ACTIVITY", "false");
        startActivity(intent);
    }

    @Override
    public void OnEnableClicked(boolean isEnabled, String addressID) {

        String userId = sharedPref.getString(Constants.USER_ID, "");
        String enable = "";
        if (isEnabled) {
            enable = "Yes";
        } else {
            enable = "No";
        }
        RequestBody formBody = new FormBody.Builder()
                .add("add_id", addressID)
                .add("enabled", enable)
                .add("user_id", userId)
                .build();

        new ShippingAddressFragment.UpdateAddressCall().execute(this, "post", formBody);
    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    @Override
    public void onResume() {
        SetAddress();
        super.onResume();
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

    public void SetAddress() {
        addressList.invalidate();
        addressArrayList = new ArrayList<>();
        addressArrayList = databaseManager.getAddress();

        addressAdapter = new AddressAdapter(getActivity(), addressArrayList);
        addressAdapter.setOnAddressEditListener(this);
        addressAdapter.setOnEnableListener(this);
        addressList.setAdapter(addressAdapter);
    }

    private class CityCall extends AsyncTask<Object, Void, Object> {

        private String TAG = ShippingAddressFragment.CityCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_add));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_CITY;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, false);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                String parseUpdate = jsonParser.parseCityDetails(result.toString());
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    new ShippingAddressFragment.AreaCall().execute(this, "post", "");
                } else {
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class AreaCall extends AsyncTask<Object, Void, Object> {

        private String TAG = ShippingAddressFragment.AreaCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_AREA;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    String userId = sharedPref.getString(Constants.USER_ID, "");
                    new ShippingAddressFragment.AddressCall().execute(userId, "post", "");
                } else {
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class AddressCall extends AsyncTask<String, Void, Object> {

        private String TAG = ShippingAddressFragment.AddressCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(String... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ADDRESS + params[0];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    helper.dismissProgressDialog(progressDialog);
                } else if (result.toString().equalsIgnoreCase("NoData")) {
                    helper.dismissProgressDialog(progressDialog);
                } else {
                    helper.dismissProgressDialog(progressDialog);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class UpdateAddressCall extends AsyncTask<Object, Void, Object> {
        private ProgressDialog progressDialog;
        private String TAG = ShippingAddressFragment.UpdateAddressCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.saving_add));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ADDRESS_UPDATE;
            RequestBody requestParam = (RequestBody) params[2];
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
//            http://atozfurniture.ae/apisv1/azapi.php?mode=address&action=save&add_id=3&add_fullname=SaranyaMarappan&add_street=12&add_building=AlAttarTower&add_apartment=1501
//            // &add_floor=15&add_landmark=NearToMetro&add_phone=971586329944&add_mobile=971586983399&add_area=Dubai&add_location_type=work&add_delivery_time=10.00A.Mto02.00P.M
//            // &add_shipping_note=Quick&add_city=Dubai&add_country=UAE&userid=4
            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String parseUpdate = jsonParser.parseUpdatedAddress(result.toString());
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    addressArrayList = new ArrayList<>();
                    addressArrayList = databaseManager.getAddress();
                    addressAdapter.updateAddressList(addressArrayList);
//                    helper.cancelableAlertDialog("", context.getString(R.string.address_save_message), 1);
                } else {
                    SetAddress();
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                SetAddress();
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}