package com.netsol.atoz.Fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.netsol.atoz.Activity.CategoryActivity;
import com.netsol.atoz.Activity.ProductDeatils;
import com.netsol.atoz.Adapter.CategorySearchAdapter;
import com.netsol.atoz.Adapter.SliderAdapter;
import com.netsol.atoz.Controller.AsyncServiceCall;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.Banner;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.Model.Category;
import com.netsol.atoz.Model.HomeGroup;
import com.netsol.atoz.Model.HomeProduct;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;
import com.netsol.atoz.Util.NetworkManager;
import com.netsol.atoz.Util.ViewPagerCustomDuration;
import com.netsol.atoz.Util.ZoomOutPageTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by macmini on 11/1/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, AlertAction, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    String searchText = "";
    Button searchViewAll;
    Context context;
    ListView searchList;
    TabLayout indicator;
    JsonParser jsonParser;
    SearchView searchView;
    ScrollView homeScrollLayout;
    SliderTimer sliderTimer;
    LinearLayout homeGroup;
    List<String> HashMapForURL;
    private Helper helper;
    LinearLayout homeSearchLayout;
    DatabaseManager dbManager;
    ViewPagerCustomDuration viewPager;
    WebserviceManager _webserviceManager;
    AsyncServiceCall _searchQueryCall;
    ArrayList<String> searchQueryList;
    public static ArrayList<Product> productSearchArrayList;
    public static ArrayList<Category> categoryList = new ArrayList<>();
    ArrayList<HomeProduct> trendingArrayList = new ArrayList<>();
    ArrayList<HomeGroup> homeGroupArrayList = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        categoryList = new ArrayList<>();
        context = super.getContext();
        _webserviceManager = new WebserviceManager(context);
        helper = new Helper(context, this);
        jsonParser = new JsonParser(context);
        dbManager = new DatabaseManager(context);

        searchView = (SearchView)view.findViewById(R.id.home_search);
        searchViewAll = (Button) view.findViewById(R.id.button_search_view_all);
        homeGroup = (LinearLayout)view.findViewById(R.id.home_group);
        viewPager=(ViewPagerCustomDuration)view.findViewById(R.id.viewPager);
        indicator=(TabLayout)view.findViewById(R.id.indicator);
        searchList = (ListView) view.findViewById(R.id.home_search_list);
        homeSearchLayout = (LinearLayout) view.findViewById(R.id.home_search_layout);
        homeScrollLayout = (ScrollView) view.findViewById(R.id.home_scroll_layout);
        ImageView followFb = (ImageView) view.findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) view.findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) view.findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) view.findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) view.findViewById(R.id.follow_cam);
        ImageView followPin = (ImageView) view.findViewById(R.id.follow_pintrest);

        int textViewID = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null, null);
        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        categoryList = dbManager.getCategory();

        //View Pager Start
        AddImagesUrlOnline();
        viewPager.setAdapter(new SliderAdapter(context, HashMapForURL));
        indicator.setupWithViewPager(viewPager, true);
//        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        sliderTimer = new SliderTimer();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(sliderTimer, 4000, 4000);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String productId = productSearchArrayList.get(position).getProductId();
//                String categoryId = productSearchArrayList.get(position).getCategoryId();
//
//                homeSearchLayout.setVisibility(View.GONE);
//                homeScrollLayout.setVisibility(View.VISIBLE);
//                searchList.clearTextFilter();
//                searchView.clearFocus();
//                productSearchArrayList.clear();
//
//                new HomeFragment.GetSingleProduct().execute(productId, "post", "");

                String searchSelected = searchQueryList.get(position);
                homeSearchLayout.setVisibility(View.GONE);
                homeScrollLayout.setVisibility(View.VISIBLE);
                searchList.clearTextFilter();
                searchView.clearFocus();

                new HomeFragment.GetSearchProduct().execute(searchSelected, "Search Result", "");

            }
        });

        searchViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                homeSearchLayout.setVisibility(View.GONE);
                homeScrollLayout.setVisibility(View.VISIBLE);
                searchList.clearTextFilter();
                searchView.clearFocus();

                new HomeFragment.GetSearchProduct().execute(searchText, "Search Result", "");
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
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (sliderTimer != null)
            sliderTimer.cancel();
    }

    @Override
    public void onResume() {
        setTrendingNow();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString();
        String Id = String.valueOf(v.getId());
        if (tag.equalsIgnoreCase("Category")) {
            new HomeFragment.GetProducts().execute(Id, "post", "");
//            Toast.makeText(context, String.valueOf(v.getId()), Toast.LENGTH_LONG).show();
        } else if (tag.equalsIgnoreCase("Home Product")) {
            new HomeFragment.GetSingleProduct().execute(Id, "post", "");
        } else if (tag.equalsIgnoreCase("Categories")) {
            new HomeFragment.GetCategory().execute(tag, "post", "");
        } else {
            Intent intent = new Intent(getActivity(), CategoryActivity.class);
            intent.putExtra("IS_HOME", "true");
            intent.putExtra("HEADING", tag);
            intent.putExtra("IS_SUB_CAT", "false");
            intent.putExtra("ID", Id);
            startActivity(intent);
        }
    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        homeSearchLayout.setVisibility(View.GONE);
        homeScrollLayout.setVisibility(View.VISIBLE);
        searchList.clearTextFilter();
        searchView.clearFocus();

        new HomeFragment.GetSearchProduct().execute(searchText, "Search Result", "");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchText = newText;
        if (newText.equalsIgnoreCase("")) {
            homeSearchLayout.setVisibility(View.GONE);
            homeScrollLayout.setVisibility(View.VISIBLE);
        } else {
//            productSearchArrayList = new ArrayList<>();
            homeSearchLayout.setVisibility(View.VISIBLE);
            homeScrollLayout.setVisibility(View.GONE);
//            productSearchArrayList = dbManager.getSearchProduct(newText);
            if (_searchQueryCall != null && (_searchQueryCall.getStatus() == AsyncTask.Status.RUNNING || _searchQueryCall.getStatus() == AsyncTask.Status.PENDING)) {
                _searchQueryCall.cancel(true);
            }
            GetSearchQuery ();
        }
        return false;
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
        public void onFragmentInteraction();
    }

    public void AddImagesUrlOnline(){
        HashMapForURL = new ArrayList<>();
        ArrayList<Banner> bannerArrayList = dbManager.getTopBanner();

        for (int i = 0; i < bannerArrayList.size(); i++) {
            String url = bannerArrayList.get(i).getBanner();
            HashMapForURL.add(url);
        }
    }

    private void setTrendingNow() {

        //Horizontal Layout Start
        homeGroupArrayList = new ArrayList<>();
        homeGroupArrayList = dbManager.getHomeGroup();

        if (homeGroup.getChildCount() > 0)
            homeGroup.removeAllViews();

        for(int i = 0; i < homeGroupArrayList.size(); i++) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            View slider_group = mInflater.inflate(R.layout.home_slider_group, homeGroup, false);
            TextView groupHeading = (TextView) slider_group.findViewById(R.id.home_trending_now);
            TextView groupViewAll = (TextView) slider_group.findViewById(R.id.home_view_all);
            LinearLayout dealsView = (LinearLayout) slider_group.findViewById(R.id.dealsView);
            trendingArrayList = new ArrayList<>();
            trendingArrayList = dbManager.getHomeProductByGroupId(homeGroupArrayList.get(i).getGroupId());
            if (dealsView.getChildCount() > 0)
                dealsView.removeAllViews();

            String title = homeGroupArrayList.get(i).getGroupTitle();
            groupViewAll.setTag(title);
            groupViewAll.setId(Integer.parseInt(homeGroupArrayList.get(i).getGroupId()));
            groupViewAll.setOnClickListener(this);

            groupHeading.setText(title);
            if (title.equalsIgnoreCase("Categories")) {
                setCategory(homeGroupArrayList.get(i));
            } else {
                if (trendingArrayList.size() > 0) {
                    for(int j = 0; j < trendingArrayList.size(); j++)  {
                        LayoutInflater mInflater1 = LayoutInflater.from(context);
                        View slider_view = mInflater1.inflate(R.layout.home_slider_view, dealsView, false);
                        ImageView productImage = (ImageView) slider_view.findViewById(R.id.slider_product_image);
                        TextView deprecatedPrice = (TextView) slider_view.findViewById(R.id.slider_deprecated_price);
                        TextView originalPrice = (TextView) slider_view.findViewById(R.id.slider_price);
                        TextView discountPerceent = (TextView) slider_view.findViewById(R.id.slider_discount_perxent);
                        TextView productTitle = (TextView) slider_view.findViewById(R.id.slider_product_name);

                        Glide.with(context).load(trendingArrayList.get(j).getImage())
                                .fitCenter()
//                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(null)
                                .into(productImage);

                        discountPerceent.setText("-" + trendingArrayList.get(j).getDiscount_percent() + "%");
                        productTitle.setText(trendingArrayList.get(j).getProductTitle());
                        originalPrice.setText(String.format("%s AED", trendingArrayList.get(j).getSelling_price()));
                        deprecatedPrice.setText(String.format("%s AED", trendingArrayList.get(j).getActual_price()));
                        deprecatedPrice.setPaintFlags(deprecatedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        slider_view.setTag("Home Product");
                        slider_view.setId(Integer.parseInt(trendingArrayList.get(j).getProductId()));
                        slider_view.setOnClickListener(this);

                        dealsView.addView(slider_view);
                    }
                    homeGroup.addView(slider_group);
                }
            }
        }
    }

    private void setCategory(HomeGroup homeGroupArrayList) {

        LayoutInflater mInflater2 = LayoutInflater.from(context);
        View category_group_view1 = mInflater2.inflate(R.layout.home_silde_image, homeGroup, false);
        homeGroup.addView(category_group_view1);

        LayoutInflater mInflater = LayoutInflater.from(context);
        View slider_group = mInflater.inflate(R.layout.home_category_group, homeGroup, false);
        TextView categoryGroupHeading = (TextView) slider_group.findViewById(R.id.home_category_group_heading);
        TextView categoryGroupViewAll = (TextView) slider_group.findViewById(R.id.home_category_group_view_all);
        LinearLayout categoryMainVerticalView = (LinearLayout) slider_group.findViewById(R.id.category_main_vertical_view);

        categoryGroupViewAll.setTag(homeGroupArrayList.getGroupTitle());
        categoryGroupViewAll.setId(Integer.parseInt(homeGroupArrayList.getGroupId()));
        categoryGroupViewAll.setOnClickListener(this);

        categoryGroupHeading.setText(homeGroupArrayList.getGroupTitle());
        ArrayList<Category> categoryForHomeArrayList = new ArrayList<>();
        categoryForHomeArrayList = dbManager.getCategoryForHome();
        if (categoryMainVerticalView.getChildCount() > 0)
            categoryMainVerticalView.removeAllViews();
        for(int i = 0; i < categoryForHomeArrayList.size(); i++) {
            LayoutInflater mInflater1 = LayoutInflater.from(context);
            View category_group_view = mInflater1.inflate(R.layout.home_category_group_view, categoryMainVerticalView, false);
            RelativeLayout dealsView = (RelativeLayout) category_group_view.findViewById(R.id.category_main_view);
            ImageView categoryImage = (ImageView) category_group_view.findViewById(R.id.home_category_image);
            TextView largeHeading = (TextView) category_group_view.findViewById(R.id.home_category_heading);
            Category category = categoryForHomeArrayList.get(i);
            largeHeading.setText(Html.fromHtml(category.getCategoryName()));

            Glide.with(context).load(category.getCategoryImage())
                    .fitCenter()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(null)
                    .into(categoryImage);

            dealsView.setTag("Category");
            dealsView.setId(Integer.parseInt(category.getCategoryID()));
            dealsView.setOnClickListener(this);
            categoryMainVerticalView.addView(category_group_view);
        }
        homeGroup.addView(slider_group);

    }

    public class SliderTimer extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < HashMapForURL.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    } else {
                        viewPager.setCurrentItem(0, true);
                    }
                }
            });
        }
    }

    private class GetProducts extends AsyncTask<String, Void, Object> {
        String categoryID = "";
        private ProgressDialog progressDialog;
        private String TAG = HomeFragment.GetProducts.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_products));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            categoryID = params[0];
            String urlString = Constants.BASE_URL + Constants.END_POINT_PRODUCTS + categoryID;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
//                if (result.toString().equalsIgnoreCase("Updated")) {
//                    String categoryHeading = dbManager.getCategoryName(categoryID);
//                    Intent intent = new Intent(getActivity(), CategoryActivity.class);
//                    intent.putExtra("IS_HOME", "false");
//                    intent.putExtra("HEADING", categoryHeading);
//                    intent.putExtra("ID", categoryID);
//                    startActivity(intent);
//                } else {
//                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
//                }
                String categoryHeading = dbManager.getCategoryName(categoryID);
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("IS_HOME", "false");
                intent.putExtra("IS_SUB_CAT", "true");
                intent.putExtra("HEADING", categoryHeading);
                intent.putExtra("ID", categoryID);
                startActivity(intent);
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class GetSingleProduct extends AsyncTask<String, Void, Object> {
        String productID = "";
        private ProgressDialog progressDialog;
        private String TAG = HomeFragment.GetSingleProduct.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_products));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            productID = params[0];
            String urlString = Constants.BASE_URL + Constants.END_POINT_PRODUCT + productID;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    String categoryId = dbManager.getCategoryId(productID);
                    Intent intent = new Intent(getActivity(), ProductDeatils.class);
                    intent.putExtra("CategoryID", categoryId);
                    intent.putExtra("ProductID", productID);
                    startActivity(intent);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class GetCategory extends AsyncTask<Object, Void, Object> {

        private String TAG = HomeFragment.GetCategory.class.getSimpleName();
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_products));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_CATEGORIES;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    Intent intent = new Intent(getActivity(), CategoryActivity.class);
                    intent.putExtra("IS_HOME", "true");
                    intent.putExtra("HEADING", "Categories");
                    intent.putExtra("IS_SUB_CAT", "false");
                    intent.putExtra("ID", "0");
                    startActivity(intent);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class GetSearchProduct extends AsyncTask<String, Void, Object> {
        String searchText = "";
        private ProgressDialog progressDialog;
        private String TAG = HomeFragment.GetSearchProduct.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_products));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            searchText = params[0];
            String urlString = Constants.BASE_URL + Constants.END_POINT_SEARCH_RESULT + searchText;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    helper.dismissProgressDialog(progressDialog);
                    Intent intent = new Intent(getActivity(), CategoryActivity.class);
                    intent.putExtra("IS_HOME", "true");
                    intent.putExtra("HEADING", "Search Result");
                    intent.putExtra("IS_SUB_CAT", "false");
                    intent.putExtra("ID", "0");
                    startActivity(intent);
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

    public void GetSearchQuery () {
        try {
            _searchQueryCall = new AsyncServiceCall() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Object doInBackground(Integer... params) {
                    String urlString = Constants.BASE_URL + Constants.END_POINT_PRODUCT_SEARCH + searchText;

                    Log.e("GetSearchQuery", "processing http request in async task");
                    return _webserviceManager.getHttpResponse(urlString, false);
                }

                @Override
                protected void onPostExecute(Object resultObj) {
                    String result = (String) resultObj;

                    if (result != null) {
                        String response = result.toString();
                        try {
                            JSONObject searchObject = new JSONObject(response);
                            if (!searchObject.isNull("suggestions")) {
                                searchQueryList = new ArrayList<>();
                                JSONArray searchArray = searchObject.getJSONArray("suggestions");
                                for (int j = 0; j < searchArray.length(); j++) {
                                    JSONObject productObject = searchArray
                                            .getJSONObject(j);
                                    String value = productObject
                                            .getString("value");
                                    searchQueryList.add(value);
                                }
                                CategorySearchAdapter categorySearchAdapter = new CategorySearchAdapter(getActivity(),searchQueryList);
                                searchList.setAdapter(categorySearchAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    super.onPostExecute(result);
                }
            };
            try {
                if (NetworkManager.isNetAvailable(getActivity())) {
                    _searchQueryCall.execute(0);
                }
//                else {
//                    helper.cancelableAlertDialog("", context.getString(R.string.network_not_available), 1);
//                }
            } catch (Exception ex) {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        } catch (Exception e) {
            helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
        }
    }

}