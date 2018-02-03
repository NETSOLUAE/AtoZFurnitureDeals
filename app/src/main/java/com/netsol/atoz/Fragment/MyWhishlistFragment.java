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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netsol.atoz.Activity.CreditCardActivity;
import com.netsol.atoz.Activity.HomeActivity;
import com.netsol.atoz.Activity.PaymentOptionActivity;
import com.netsol.atoz.Activity.ReviewActivity;
import com.netsol.atoz.Activity.SelectAddressActivity;
import com.netsol.atoz.Activity.SigninActivity;
import com.netsol.atoz.Adapter.MyWhishlistAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 10/31/17.
 */

public class MyWhishlistFragment extends Fragment implements MyWhishlistAdapter.OnIsFavoriteDeleted, MyWhishlistAdapter.OnChangeGrandTotal, AlertAction{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Helper helper;
    private String mParam1;
    private String mParam2;
    Context context;
    Button checkout;
    Button explore;
    TextView totalPrice;
    ListView whisListView;
    LinearLayout emptyWhishList;
    LinearLayout whishList;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private MyWhishlistAdapter myWhishlistAdapter;
    ProgressDialog progressDialog;
    WebserviceManager _webserviceManager;
    DatabaseManager dbManager;
    JsonParser jsonParser;
    public static ArrayList<CartItem> whishListArrayList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public MyWhishlistFragment() {
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

        View view = inflater.inflate(R.layout.fragment_mywhishlist, container, false);
        setHasOptionsMenu(true);
        whishListArrayList = new ArrayList<>();
        context = super.getContext();
        dbManager = new DatabaseManager(context);
        helper = new Helper(context,this);
        _webserviceManager = new WebserviceManager(context);
        jsonParser = new JsonParser(context);

        whisListView = (ListView) view.findViewById(R.id.whishlist);
        emptyWhishList = (LinearLayout) view.findViewById(R.id.layoutEmptyWhisList);
        whishList = (LinearLayout) view.findViewById(R.id.whishlistLayout);
        totalPrice = (TextView) view.findViewById(R.id.whishlist_total_price);
        checkout = (Button) view.findViewById(R.id.button_checkout_whishlist);
        explore = (Button) view.findViewById(R.id.button_explore_products_whish);
        ImageView followFb = (ImageView) view.findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) view.findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) view.findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) view.findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) view.findViewById(R.id.follow_cam);
        ImageView followPin = (ImageView) view.findViewById(R.id.follow_pintrest);

        sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
//        setCartItem();

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean session = sharedPref.getBoolean(Constants.SESSION, false);
                if (session) {
                    if (whishListArrayList.size() > 0) {
//                    String grandTotal = totalPrice.getText().toString();
//                    editor.putString(Constants.TOTAL_AMOUNT, grandTotal);
//                    editor.apply();
                        Intent intent = new Intent(getActivity(), SelectAddressActivity.class);
                        CreditCardActivity.fromActivity = "Wishlist";
                        ReviewActivity.fromActivity = "Wishlist";
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), SigninActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
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
            mListener.onFragmentInteractionWhishList();
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
    public void OnFavoriteDeleted(String productID, String color, String size, String md5) {
        String orderId = sharedPref.getString(Constants.ORDER_PLACED_ID, "");
        String userId = sharedPref.getString(Constants.USER_ID, "");

        if (orderId.equalsIgnoreCase("")) {
            setCartItem();
        } else {
            RequestBody formBody = new FormBody.Builder()
                    .add("line_color", color)
                    .add("line_size", size)
                    .add("line_status", "Deleted")
                    .add("row_id", md5)
                    .add("user_id", userId)
                    .add("ord_id", orderId)
                    .add("prod_id", productID)
                    .build();

            new MyWhishlistFragment.DeleteOrderline().execute(this, "post", formBody);
        }
    }

    @Override
    public void OnChangeTotal(String price, boolean isPlus) {
        double currentGrandTotal = Double.parseDouble(totalPrice.getText().toString().replaceAll(",",""));
        if (isPlus) {
            currentGrandTotal = currentGrandTotal + Double.parseDouble(price.replaceAll(",",""));
        } else {
            currentGrandTotal = currentGrandTotal - Double.parseDouble(price.replaceAll(",",""));
        }
        double total = Double.parseDouble(String.valueOf(currentGrandTotal).replaceAll(",",""));
        DecimalFormat precision = new DecimalFormat("0.00");
        totalPrice.setText(String.valueOf(precision.format(total)));
//        totalPrice.setText(String.valueOf(currentGrandTotal));
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
        public void onFragmentInteractionWhishList();
    }

    @Override
    public void onResume() {
        setCartItem();
        super.onResume();
    }

    private void setCartItem() {
        int totalPriceCalculated = 0;
        whishListArrayList = new ArrayList<>();
        ArrayList<CartItem> cartItemArrayList = dbManager.getCartItem();

        for (int i = 0; i < cartItemArrayList.size(); i++) {
            boolean isFavorite = Boolean.parseBoolean(cartItemArrayList.get(i).getIsFavorite());
            if (isFavorite) {
                int price = Integer.parseInt(cartItemArrayList.get(i).getPrice().replaceAll(",",""));
                int qty = Integer.parseInt(cartItemArrayList.get(i).getQuantity());
                price = price * qty;
                totalPriceCalculated = totalPriceCalculated + price;
                whishListArrayList.add(cartItemArrayList.get(i));
            }
        }

        if (whishListArrayList.size() > 0) {
            whishList.setVisibility(View.VISIBLE);
            emptyWhishList.setVisibility(View.GONE);
            myWhishlistAdapter = new MyWhishlistAdapter(getContext(), whishListArrayList);
            myWhishlistAdapter.setOnFavoriteDeletedListener(this);
            myWhishlistAdapter.setOnChangeTotalListener(this);
            whisListView.setAdapter(myWhishlistAdapter);
        } else {
            whishList.setVisibility(View.GONE);
            emptyWhishList.setVisibility(View.VISIBLE);
        }

        double total = Double.parseDouble(String.valueOf(totalPriceCalculated).replaceAll(",",""));
        DecimalFormat precision = new DecimalFormat("0.00");
        totalPrice.setText(String.valueOf(precision.format(total)));
//        totalPrice.setText(String.valueOf(totalPriceCalculated));
    }

    private class DeleteOrderline extends AsyncTask<Object, Void, Object> {

        private String TAG = MyWhishlistFragment.DeleteOrderline.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.processing));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ORDER_REMOVE;
            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result != null) {
                String parseUpdate = jsonParser.parseSaveOrder(result.toString(), "cart");
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    helper.dismissProgressDialog(progressDialog);
                    setCartItem();
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

}
