package com.netsol.atoz.Fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netsol.atoz.Activity.OrderDetailActivity;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.OrderGroup;
import com.netsol.atoz.Model.OrderProduct;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 9/26/17.
 */

public class MyOrderFragment extends Fragment implements AlertAction, View.OnClickListener {
    Context context;
    LayerDrawable icon;
    LinearLayout orderGroupLayout;
    LinearLayout orderGroupLl;
    LinearLayout orderEmptyLayout;
    private Helper helper;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPref;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    public ArrayList<OrderGroup> orderGroupArrayList;
    public ArrayList<OrderProduct> orderGroupProductArrayList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyOrderFragment() {
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

        View view = inflater.inflate(R.layout.fragment_myorders, container, false);
        setHasOptionsMenu(true);

        context = getContext();
        helper = new Helper(context, this);
        databaseManager = new DatabaseManager(context);
        _webserviceManager = new WebserviceManager(context);

        orderGroupLayout = (LinearLayout) view.findViewById(R.id.order_group_layout);
        orderGroupLl = (LinearLayout) view.findViewById(R.id.order_group_ll);
        orderEmptyLayout = (LinearLayout) view.findViewById(R.id.layoutEmptyOrder);
        ImageView followFb = (ImageView) view.findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) view.findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) view.findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) view.findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) view.findViewById(R.id.follow_cam);
        ImageView followPin = (ImageView) view.findViewById(R.id.follow_pintrest);
        sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        orderGroupLl.setVisibility(View.INVISIBLE);
        String userId = sharedPref.getString(Constants.USER_ID, "");
        new MyOrderFragment.OrderCall().execute(userId, "post", "");

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
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    private void SetStandardOrderDetails() {

        //Horizontal Layout Start
        orderGroupArrayList = new ArrayList<>();
        orderGroupArrayList = databaseManager.getOrderGroup();

        if (orderGroupLayout.getChildCount() > 0)
            orderGroupLayout.removeAllViews();
        for(int i = 0; i < orderGroupArrayList.size(); i++) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            View order_group_view = mInflater.inflate(R.layout.order_group_view, orderGroupLayout, false);
            TextView orderGroupNo = (TextView) order_group_view.findViewById(R.id.order_group_no);
            TextView orderGroupDate = (TextView) order_group_view.findViewById(R.id.order_group_date);
            TextView orderGroupTotal = (TextView) order_group_view.findViewById(R.id.order_group_total);
            TextView orderGroupCancelled = (TextView) order_group_view.findViewById(R.id.order_group_cancelled);
            Button orderGroupDetail = (Button) order_group_view.findViewById(R.id.button_view_order_details);
            ImageView orderGroupProcessing = (ImageView) order_group_view.findViewById(R.id.order_group_processing_image);
            ImageView orderGroupShipped = (ImageView) order_group_view.findViewById(R.id.order_group_shipped_image);
            ImageView orderGroupDelivered = (ImageView) order_group_view.findViewById(R.id.order_group_delivered_image);
            View orderGroupView1 = (View) order_group_view.findViewById(R.id.order_group_view1);
            View orderGroupView2 = (View) order_group_view.findViewById(R.id.order_group_view2);
            View orderGroupView3 = (View) order_group_view.findViewById(R.id.order_group_view3);
            View orderGroupView4 = (View) order_group_view.findViewById(R.id.order_group_view4);
            LinearLayout paidStatusLayout = (LinearLayout) order_group_view.findViewById(R.id.order_group_paid_status);
            LinearLayout expDeliveryDate = (LinearLayout) order_group_view.findViewById(R.id.expectedLayout);
            expDeliveryDate.setVisibility(View.INVISIBLE);

            orderGroupNo.setText(orderGroupArrayList.get(i).getNo());
            orderGroupDate.setText(orderGroupArrayList.get(i).getDate());
            orderGroupTotal.setText(orderGroupArrayList.get(i).getTotal());

            String levelCompleted = orderGroupArrayList.get(i).getLevelCompleted();

            if (levelCompleted.equalsIgnoreCase("0")) {
                paidStatusLayout.setVisibility(View.VISIBLE);
                String orderStatus = orderGroupArrayList.get(i).getStatus();
                orderGroupCancelled.setText(orderStatus);
                if (orderStatus.equalsIgnoreCase("Cancelled") || orderStatus.equalsIgnoreCase("Void")
                        || orderStatus.equalsIgnoreCase("Return")) {
                    orderGroupCancelled.setTextColor(context.getResources().getColor(R.color.cart_total));
                } else {
                    orderGroupCancelled.setTextColor(context.getResources().getColor(R.color.blackText));
                }
                orderGroupProcessing.setBackground(context.getDrawable(R.drawable.order_tick));
                orderGroupShipped.setBackground(context.getDrawable(R.drawable.order_shipped));
                orderGroupDelivered.setBackground(context.getDrawable(R.drawable.order_out));
                orderGroupView1.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
                orderGroupView2.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
                orderGroupView3.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
                orderGroupView4.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
            } else if (levelCompleted.equalsIgnoreCase("1")) {
                paidStatusLayout.setVisibility(View.INVISIBLE);
                orderGroupProcessing.setBackground(context.getDrawable(R.drawable.order_tick_processed));
                orderGroupShipped.setBackground(context.getDrawable(R.drawable.order_shipped));
                orderGroupDelivered.setBackground(context.getDrawable(R.drawable.order_out));
                orderGroupView1.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView2.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
                orderGroupView3.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
                orderGroupView4.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
            } else if (levelCompleted.equalsIgnoreCase("2")) {
                paidStatusLayout.setVisibility(View.INVISIBLE);
                orderGroupProcessing.setBackground(context.getDrawable(R.drawable.order_tick_processed));
                orderGroupShipped.setBackground(context.getDrawable(R.drawable.order_shipped_processed));
                orderGroupDelivered.setBackground(context.getDrawable(R.drawable.order_out));
                orderGroupView1.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView3.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView4.setBackgroundColor(context.getResources().getColor(R.color.orderLine));
            } else if (levelCompleted.equalsIgnoreCase("3")) {
                paidStatusLayout.setVisibility(View.INVISIBLE);
                orderGroupProcessing.setBackground(context.getDrawable(R.drawable.order_tick_processed));
                orderGroupShipped.setBackground(context.getDrawable(R.drawable.order_shipped_processed));
                orderGroupDelivered.setBackground(context.getDrawable(R.drawable.order_out_processed));
                orderGroupView1.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView3.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                orderGroupView4.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }

            orderGroupProductArrayList = new ArrayList<>();
            orderGroupProductArrayList = databaseManager.getOrderProducts(orderGroupArrayList.get(i).getNo());
//            if (orderGroupProductView.getChildCount() > 0)
//                orderGroupProductView.removeAllViews();

            orderGroupDetail.setTag(orderGroupArrayList.get(i).getNo());
            orderGroupDetail.setOnClickListener(this);

//            if (orderGroupProductArrayList.size() > 0) {
//                for(int j = 0; j < orderGroupProductArrayList.size(); j++)  {
//                    LayoutInflater mInflater1 = LayoutInflater.from(context);
//                    View order_group_product_view = mInflater1.inflate(R.layout.order_group_product_view, orderGroupProductView, false);
//                    ImageView productImage = (ImageView) order_group_product_view.findViewById(R.id.order_group_product_image);
//                    TextView productName = (TextView) order_group_product_view.findViewById(R.id.order_group_product_name);
//                    TextView productColor = (TextView) order_group_product_view.findViewById(R.id.order_group_product_color);
//                    TextView productQuantity = (TextView) order_group_product_view.findViewById(R.id.order_group_quantity);
//                    TextView productSize = (TextView) order_group_product_view.findViewById(R.id.order_group_size);
//                    TextView productPrice = (TextView) order_group_product_view.findViewById(R.id.order_group_price);
//                    TextView productSubTotal = (TextView) order_group_product_view.findViewById(R.id.order_group_sub_total_price);
//                    View productDivider = (View) order_group_product_view.findViewById(R.id.order_group_product_divider);
//
//                    if (j == orderGroupProductArrayList.size()-1) {
//                        productDivider.setVisibility(View.GONE);
//                    } else {
//                        productDivider.setVisibility(View.VISIBLE);
//                    }
//                    Glide.with(context).load(orderGroupProductArrayList.get(j).getImage())
//                            .fitCenter()
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .into(productImage);
//
//                    productName.setText(orderGroupProductArrayList.get(j).getName());
//                    productColor.setText(String.format("Color: %s", orderGroupProductArrayList.get(j).getColor()));
//                    productQuantity.setText(String.format("Quantity: %s", orderGroupProductArrayList.get(j).getQty()));
//                    productSize.setText(String.format("Size: %s", orderGroupProductArrayList.get(j).getSize()));
//                    productPrice.setText(String.format(" %s", orderGroupProductArrayList.get(j).getPrice()));
//                    productSubTotal.setText(String.format(" %s", orderGroupProductArrayList.get(j).getSubTotal()));
//
//                    orderGroupProductView.addView(order_group_product_view);
//                }
//            }
            orderGroupLayout.addView(order_group_view);
        }
        orderGroupLl.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        String Id = String.valueOf(v.getTag());
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", Id);
        startActivity(intent);

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

    private class OrderCall extends AsyncTask<String, Void, Object> {
        private String TAG = MyOrderFragment.OrderCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_order));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
//            String urlString = Constants.BASE_URL + Constants.END_POINT_ORDER + "1";
            String urlString = Constants.BASE_URL + Constants.END_POINT_ORDER + params[0];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    new MyOrderFragment.AreaCall().execute("", "post", "");
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

    private class AreaCall extends AsyncTask<Object, Void, Object> {

        private String TAG = MyOrderFragment.AreaCall.class.getSimpleName();

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
                    new MyOrderFragment.AddressCall().execute(userId, "post", "");
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

        private String TAG = MyOrderFragment.AddressCall.class.getSimpleName();

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
                    ArrayList<OrderGroup> orderList = databaseManager.getOrderGroup();
                    if (orderList.size() > 0) {
                        orderEmptyLayout.setVisibility(View.GONE);
                        SetStandardOrderDetails();
                    } else {
                        orderEmptyLayout.setVisibility(View.VISIBLE);
                        orderGroupLl.setVisibility(View.VISIBLE);
                    }
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

}
