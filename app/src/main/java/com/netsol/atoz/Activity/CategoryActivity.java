package com.netsol.atoz.Activity;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Adapter.CategoryAdapter;
import com.netsol.atoz.Adapter.CategoryFilterAdapter;
import com.netsol.atoz.Adapter.CategoryListAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Fragment.HomeFragment;
import com.netsol.atoz.Model.Category;
import com.netsol.atoz.Model.FilterChild;
import com.netsol.atoz.Model.FilterGroup;
import com.netsol.atoz.Model.HomeProduct;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by macmini on 11/6/17.
 */

public class CategoryActivity extends AppCompatActivity implements AlertAction, CategoryAdapter.OnCartAdded, CategoryFilterAdapter.OnCheckBoxChanged {

    int maxRange = 0;
    int minRange = -1;
    int lowestPrice = 0;
    int highestPrice = 10000;
    boolean isListView = false;
    Context context;
    String isHome;
    String isSubCat;
    String heading;
    String categoryId;
    String headingValue = "";
    Button btnBottomSheet;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    GridView categoryGrid;
    GridView categoriesGrid;
    ListView categoryList;
    LinearLayout gridChange;
    Button priceChange;
    RangeSeekBar<Integer> seekBar;
    Button catChange;
    ImageView gridChangeIcon;
    LinearLayout filterLayout;
    LinearLayout gridLayout;
    LinearLayout categoryGridLayout;
    LinearLayout listLayout;
    LayerDrawable icon;
    private Helper helper;
    JsonParser jsonParser;
    ProgressDialog progressDialog;
    CategoryAdapter categoryAdapter;
    BottomSheetDialog dialog;
    BottomSheetDialog bottomSheetDialog;
    ArrayList<Category> categoryArrayList;
    static ArrayList<Product> productArrayList;
    ArrayList<Product> productAdapterArrayList;
    ArrayList<Product> filterProductArrayList;
    CategoryListAdapter categoryListAdapter;
    ArrayList<HomeProduct> filterHomeProductArrayList;
    ArrayList<HomeProduct> homeAdapterProductArrayList;
    static ArrayList<HomeProduct> homeProductArrayList;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    public static ArrayList<FilterGroup> bottomSheetGroup;
    public static ArrayList<FilterGroup> filteredBottomSheetGroup;

    TextView priceMin;
    TextView priceMax;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        context = CategoryActivity.this;
        helper = new Helper(this, this);
        jsonParser = new JsonParser(context);
        databaseManager = new DatabaseManager(this);
        progressDialog = new ProgressDialog(this);
        _webserviceManager = new WebserviceManager(this);
        bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this);
        filteredBottomSheetGroup = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isHome = b.getString("IS_HOME");
            isSubCat = b.getString("IS_SUB_CAT");
            heading = b.getString("HEADING");
            categoryId = b.getString("ID");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Html.fromHtml(heading));
        }

        btnBottomSheet = (Button) findViewById(R.id.button_bottom_sheet_done);
        categoryGrid = (GridView) findViewById(R.id.categoryGridview);
        categoriesGrid = (GridView) findViewById(R.id.categoriesGridview);
        categoryList = (ListView) findViewById(R.id.categoryListView);
        gridChange = (LinearLayout) findViewById(R.id.category_grid_change);
        priceChange = (Button) findViewById(R.id.category_price_change);
        catChange = (Button) findViewById(R.id.category_cat_change);
        gridChangeIcon = (ImageView) findViewById(R.id.grid_change);
        filterLayout = (LinearLayout) findViewById(R.id.category_filter_layout);
        gridLayout = (LinearLayout) findViewById(R.id.categoryGridLayout);
        categoryGridLayout = (LinearLayout) findViewById(R.id.categoriesGridLayout);
        listLayout = (LinearLayout) findViewById(R.id.categoryListLayout);
        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        gridLayout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
        isListView = false;

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        categoriesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                categoryId = categoryArrayList.get(position).getCategoryID();
                heading = categoryArrayList.get(position).getCategoryName();
                new CategoryActivity.GetProducts().execute(categoryId, "post", "");

            }
        });

        categoryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String categoryID = "";
                String productID = "";
                if (!heading.equalsIgnoreCase("Categories")) {
                    if (productAdapterArrayList.size() > 0 && !heading.equalsIgnoreCase("Search Result")) {
                        categoryID = productAdapterArrayList.get(position).getCategoryId();
                        productID = productAdapterArrayList.get(position).getProductId();
                        Intent intent = new Intent(CategoryActivity.this, ProductDeatils.class);
                        intent.putExtra("CategoryID", categoryID);
                        intent.putExtra("ProductID", productID);
                        startActivity(intent);
                    } else if (productAdapterArrayList.size() > 0 && heading.equalsIgnoreCase("Search Result")) {
                        productID = productAdapterArrayList.get(position).getProductId();
                        new CategoryActivity.GetSingleProduct().execute(productID, "post", "");
                    } else {
                        productID = homeAdapterProductArrayList.get(position).getProductId();
                        new CategoryActivity.GetSingleProduct().execute(productID, "post", "");
                    }
                }

            }
        });
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String categoryID = "";
                String productID = "";
                if (productAdapterArrayList.size() > 0 && !heading.equalsIgnoreCase("Search Result")) {
                    categoryID = productAdapterArrayList.get(position).getCategoryId();
                    productID = productAdapterArrayList.get(position).getProductId();
                    Intent intent = new Intent(CategoryActivity.this, ProductDeatils.class);
                    intent.putExtra("CategoryID", categoryID);
                    intent.putExtra("ProductID", productID);
                    startActivity(intent);
                } else if (productAdapterArrayList.size() > 0 && heading.equalsIgnoreCase("Search Result")) {
                    productID = productAdapterArrayList.get(position).getProductId();
                    new CategoryActivity.GetSingleProduct().execute(productID, "post", "");
                } else {
                    productID = homeAdapterProductArrayList.get(position).getProductId();
                    new CategoryActivity.GetSingleProduct().execute(productID, "post", "");
                }
            }
        });

        gridChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    gridLayout.setVisibility(View.VISIBLE);
                    listLayout.setVisibility(View.GONE);
                    isListView = false;
                    gridChangeIcon.setBackground(context.getDrawable(R.drawable.top_grid));
                } else {
                    gridLayout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                    isListView = true;
                    gridChangeIcon.setBackground(context.getDrawable(R.drawable.list_icon));
                }

            }
        });

        priceChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headingValue = "Price";
                showBottomSheetDialog();
            }
        });

        catChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headingValue = "Category";
                showBottomSheetDialog();
            }
        });

        setCategory();
        setFilterArray();
        setFooter();
        setFloatingMenu();

        if (heading.equalsIgnoreCase("Search Result")) {
            catChange.setVisibility(View.VISIBLE);
        } else {
            catChange.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isHome.equalsIgnoreCase("false") && isSubCat.equalsIgnoreCase("false")) {
                    listLayout.setVisibility(View.GONE);
                    gridLayout.setVisibility(View.GONE);
                    categoryGridLayout.setVisibility(View.VISIBLE);
                    categoryId = "0";
                    heading = "Categories";
                    isHome = "true";

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(Html.fromHtml(heading));
                    }

                    setCategory();
                    setFilterArray();
                } else {
                    Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.cart:
                startActivity(new Intent(CategoryActivity.this, CartActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isHome.equalsIgnoreCase("false") && isSubCat.equalsIgnoreCase("false")) {
            listLayout.setVisibility(View.GONE);
            gridLayout.setVisibility(View.GONE);
            categoryGridLayout.setVisibility(View.VISIBLE);
            categoryId = "0";
            heading = "Categories";
            isHome = "true";

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Html.fromHtml(heading));
            }

            setCategory();
            setFilterArray();
        } else {
            Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem cart = menu.findItem(R.id.cart);
        MenuItem favorite = menu.findItem(R.id.favorite);
        MenuItem search = menu.findItem(R.id.action_search);
        favorite.setVisible(false);
        search.setVisible(false);

        cart.setIcon(context.getResources().getDrawable(R.drawable.menu_cart_white));

        icon = (LayerDrawable) cart.getIcon();
        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        helper.setBadgeCount(this, icon, badgeCount);
        return true;
    }

    @Override
    public void onResume() {
        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        if (icon != null)
            helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
        super.onResume();
    }

    @Override
    public void OnCartItemAdded() {
        int badgeCount = databaseManager.getBadgeCount();
        editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
        editor.apply();
        helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    @Override
    public void OnCheckBoxChangedListener(ArrayList<FilterChild> listStateSelected) {
        if (heading.equalsIgnoreCase("Search Result")) {
            if (headingValue.equalsIgnoreCase("Category")) {
                bottomSheetGroup.get(0).setFilterChildArray(listStateSelected);
            } else if (headingValue.equalsIgnoreCase("Price")) {
                bottomSheetGroup.get(1).setFilterChildArray(listStateSelected);
            }
        } else {
            if (headingValue.equalsIgnoreCase("Price")) {
                bottomSheetGroup.get(0).setFilterChildArray(listStateSelected);
            }
        }
        filteredBottomSheetGroup.clear();
        filteredBottomSheetGroup.addAll(bottomSheetGroup);
        if (filteredBottomSheetGroup != null && filteredBottomSheetGroup.size() > 0) {
            setFilterCategory();
            Log.d("FilteredData",filteredBottomSheetGroup.toString());
        }
    }

    private void setFilterCategory() {
        boolean isProductArray = false;
        boolean isCategorySelected = false;
        boolean isHomeProductSelected = false;
        filterProductArrayList = new ArrayList<>();
        filterHomeProductArrayList = new ArrayList<>();
        isProductArray = productArrayList.size() > 0;
        if (filteredBottomSheetGroup.size() > 0) {
            if (isProductArray) {
                for (int i = 0; i < filteredBottomSheetGroup.size(); i++) {
                    String groupId = filteredBottomSheetGroup.get(i).getId();
                    String groupTitle = filteredBottomSheetGroup.get(i).getTitle();
                    ArrayList<FilterChild> filterChildArrayList = new ArrayList<>();
                    filterChildArrayList = filteredBottomSheetGroup.get(i).getFilterChildArray();
                    for (int j = 0; j < filterChildArrayList.size(); j++) {
                        String isSelected = filterChildArrayList.get(j).getIsSelected();
                        if (isSelected.equalsIgnoreCase("true")) {
                            if (groupId.equalsIgnoreCase("1")) {
                                isCategorySelected = true;
                                String categoryId = filterChildArrayList.get(j).getId();
                                ArrayList<Product> productArrayList = new ArrayList<>();
                                if (heading.equalsIgnoreCase("Search Result")) {
                                    productArrayList = databaseManager.getProduct(categoryId, "true");
                                } else {
                                    productArrayList = databaseManager.getProduct(categoryId);
                                }
                                filterProductArrayList.addAll(productArrayList);
                            } else if (groupTitle.equalsIgnoreCase("Price")) {
                                if (minRange == -1) {
                                    minRange = lowestPrice;
                                }
                                if (maxRange == 0) {
                                    maxRange = highestPrice;
                                }
                                if (isCategorySelected) {
                                    ArrayList<Product> newProductList = new ArrayList<>();
                                    for (int k = 0; k < filterProductArrayList.size(); k++) {
                                        int sellingPrice = Integer.parseInt(filterProductArrayList.get(k).getSellingPrice().replace(",",""));
                                        if ((sellingPrice >= minRange) && (sellingPrice <= maxRange)) {
                                            newProductList.add(filterProductArrayList.get(k));
                                        }
                                    }
                                    filterProductArrayList = newProductList;
                                } else {
                                    for (int k = 0; k < productArrayList.size(); k++) {
                                        int sellingPrice = Integer.parseInt(productArrayList.get(k).getSellingPrice().replace(",",""));
                                        if ((sellingPrice >= minRange) && (sellingPrice <= maxRange)) {
                                            filterProductArrayList.add(productArrayList.get(k));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < filteredBottomSheetGroup.size(); i++) {
                    String groupId = filteredBottomSheetGroup.get(i).getId();
                    String groupTitle = filteredBottomSheetGroup.get(i).getTitle();
                    ArrayList<FilterChild> filterChildArrayList = new ArrayList<>();
                    filterChildArrayList = filteredBottomSheetGroup.get(i).getFilterChildArray();
                    for (int j = 0; j < filterChildArrayList.size(); j++) {
                        String isSelected = filterChildArrayList.get(j).getIsSelected();
                        if (isSelected.equalsIgnoreCase("true")) {
                            isHomeProductSelected = true;
                            if (groupTitle.equalsIgnoreCase("Price")) {
                                if (minRange == -1) {
                                    minRange = lowestPrice;
                                }
                                if (maxRange == 0) {
                                    maxRange = highestPrice;
                                }
                                for (int k = 0; k < homeProductArrayList.size(); k++) {
                                    int sellingPrice = Integer.parseInt(homeProductArrayList.get(k).getSelling_price().replace(",",""));
                                    if ((sellingPrice >= minRange) && (sellingPrice <= maxRange)) {
                                        filterHomeProductArrayList.add(homeProductArrayList.get(k));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            categoryAdapter.notifyDataSetChanged();
            categoryListAdapter.notifyDataSetChanged();

            if (isCategorySelected || (minRange != -1 && maxRange !=0)) {
                productAdapterArrayList = filterProductArrayList;
            } else {
                productAdapterArrayList = productArrayList;
            }

            if (isHomeProductSelected && (minRange != -1 && maxRange !=0)) {
                homeAdapterProductArrayList = filterHomeProductArrayList;
            } else {
                homeAdapterProductArrayList = homeProductArrayList;
            }

            setCategoryAdapter();

        } else {
            productAdapterArrayList.clear();
            homeAdapterProductArrayList.clear();
            categoryAdapter.notifyDataSetChanged();
            categoryListAdapter.notifyDataSetChanged();

            productAdapterArrayList = productArrayList;
            homeAdapterProductArrayList = homeProductArrayList;

            setCategoryAdapter();
        }
    }

    private void setCategory() {
        productArrayList = new ArrayList<>();
        productAdapterArrayList = new ArrayList<>();
        homeProductArrayList = new ArrayList<>();
        homeAdapterProductArrayList = new ArrayList<>();

        if (isHome.equalsIgnoreCase("true")) {
            if (heading.equalsIgnoreCase("Categories")) {
                categoryArrayList = databaseManager.getCategory();
            } if (heading.equalsIgnoreCase("Search Result")) {
                productArrayList = databaseManager.getAllSearchProduct();
                productAdapterArrayList = databaseManager.getAllSearchProduct();
                Collections.sort(productArrayList, new Comparator<Product>() {
                    @Override
                    public int compare(Product lhs, Product rhs) {
                        int lhsint = Integer.parseInt(lhs.getSellingPrice().replaceAll(",",""));
                        int rhsint = Integer.parseInt(rhs.getSellingPrice().replaceAll(",",""));
                        return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
                    }
                });
                Collections.sort(productAdapterArrayList, new Comparator<Product>() {
                    @Override
                    public int compare(Product lhs, Product rhs) {
                        int lhsint = Integer.parseInt(lhs.getSellingPrice().replaceAll(",",""));
                        int rhsint = Integer.parseInt(rhs.getSellingPrice().replaceAll(",",""));
                        return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
                    }
                });
            } else {
                homeProductArrayList = databaseManager.getHomeProductByGroupId(categoryId);
                homeAdapterProductArrayList = databaseManager.getHomeProductByGroupId(categoryId);
                Collections.sort(homeProductArrayList, new Comparator<HomeProduct>() {
                    @Override
                    public int compare(HomeProduct lhs, HomeProduct rhs) {
                        int lhsint = Integer.parseInt(lhs.getSelling_price().replaceAll(",",""));
                        int rhsint = Integer.parseInt(rhs.getSelling_price().replaceAll(",",""));
                        return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
                    }
                });
                Collections.sort(homeAdapterProductArrayList, new Comparator<HomeProduct>() {
                    @Override
                    public int compare(HomeProduct lhs, HomeProduct rhs) {
                        int lhsint = Integer.parseInt(lhs.getSelling_price().replaceAll(",",""));
                        int rhsint = Integer.parseInt(rhs.getSelling_price().replaceAll(",",""));
                        return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
                    }
                });
            }
        } else {
            productArrayList = databaseManager.getProduct(categoryId);
            productAdapterArrayList = databaseManager.getProduct(categoryId);
            Collections.sort(productArrayList, new Comparator<Product>() {
                @Override
                public int compare(Product lhs, Product rhs) {
                    int lhsint = Integer.parseInt(lhs.getSellingPrice().replaceAll(",",""));
                    int rhsint = Integer.parseInt(rhs.getSellingPrice().replaceAll(",",""));
                    return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
                }
            });
            Collections.sort(productAdapterArrayList, new Comparator<Product>() {
                @Override
                public int compare(Product lhs, Product rhs) {
                    int lhsint = Integer.parseInt(lhs.getSellingPrice().replaceAll(",",""));
                    int rhsint = Integer.parseInt(rhs.getSellingPrice().replaceAll(",",""));
                    return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
                }
            });
        }
        setCategoryAdapter();
    }

    private void setCategoryAdapter() {

        if (heading.equalsIgnoreCase("Categories")) {
            gridLayout.setVisibility(View.GONE);
            categoryGridLayout.setVisibility(View.VISIBLE);

            categoryAdapter = new CategoryAdapter(this, productAdapterArrayList, homeAdapterProductArrayList, categoryArrayList, true);
            categoryAdapter.setOnCartAddedListener(this);
            categoriesGrid.setAdapter(categoryAdapter);

            filterLayout.setVisibility(View.GONE);

        } else {
            gridLayout.setVisibility(View.VISIBLE);
            categoryGridLayout.setVisibility(View.GONE);
            categoryAdapter = new CategoryAdapter(this, productAdapterArrayList, homeAdapterProductArrayList, categoryArrayList, false);
            categoryAdapter.setOnCartAddedListener(this);
            categoryGrid.setAdapter(categoryAdapter);

            categoryListAdapter = new CategoryListAdapter(this, productAdapterArrayList, homeAdapterProductArrayList);
            categoryList.setAdapter(categoryListAdapter);
            filterLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setFilterArray() {
        bottomSheetGroup = new ArrayList<>();
        ArrayList<FilterChild> filterChildList = new ArrayList<>();
        if (heading.equalsIgnoreCase("Search Result")) {
            ArrayList<Category> categoryList = databaseManager.getCategory();
            for (int i = 0; i < categoryList.size(); i++) {
                FilterChild filterChild = new FilterChild();
                filterChild.setId(categoryList.get(i).getCategoryID());
                filterChild.setTitle(categoryList.get(i).getCategoryName());
                filterChild.setIsSelected("false");
                filterChildList.add(filterChild);
            }
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.setId("1");
            filterGroup.setTitle("Category");
            filterGroup.setFilterChildArray(filterChildList);
            bottomSheetGroup.add(filterGroup);
        }

        filterChildList = new ArrayList<>();
        ArrayList<Product> allProductArrayList = databaseManager.getAllProduct();
//        int allProductSize = allProductArrayList.size();
        Collections.sort(allProductArrayList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                int lhsint = Integer.parseInt(lhs.getSellingPrice().replaceAll(",",""));
                int rhsint = Integer.parseInt(rhs.getSellingPrice().replaceAll(",",""));
                return (lhsint < rhsint) ? -1: (lhsint > rhsint) ? 1:0 ;
            }
        });

//        lowestPrice = Integer.parseInt(allProductArrayList.get(0).getSellingPrice().replaceAll(",",""));
//        highestPrice = Integer.parseInt(allProductArrayList.get(allProductSize-1).getSellingPrice().replaceAll(",",""));

        FilterChild filterChild = new FilterChild();
        filterChild.setId(String.valueOf(lowestPrice));
        filterChild.setTitle(String.valueOf(lowestPrice) + " AED - " + String.valueOf(highestPrice) + " AED");
        filterChild.setIsSelected("true");

        filterChildList.add(filterChild);

        FilterGroup filterGroup = new FilterGroup();
        filterGroup.setId("2");
        filterGroup.setTitle("Price");
        filterGroup.setFilterChildArray(filterChildList);
        bottomSheetGroup.add(filterGroup);

        filterChildList = databaseManager.getDistinctColorName();
        FilterGroup filterGroupColor = new FilterGroup();
        filterGroupColor.setId("3");
        filterGroupColor.setTitle("Color");
        filterGroupColor.setFilterChildArray(filterChildList);
        bottomSheetGroup.add(filterGroupColor);

    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        TextView heading1 = (TextView) view.findViewById(R.id.bottom_sheet_heading);
        priceMin = (TextView) view.findViewById(R.id.price_min);
        priceMax = (TextView) view.findViewById(R.id.price_max);
        Button done = (Button) view.findViewById(R.id.button_bottom_sheet_done);
        ListView filterList = (ListView) view.findViewById(R.id.bottom_sheet_list);
        LinearLayout sneekBarLayout = (LinearLayout) view.findViewById(R.id.rangeSeekbar);
        LinearLayout sneekBarLl = (LinearLayout) view.findViewById(R.id.sneek_bar_layout);
        seekBar = new RangeSeekBar<Integer>(CategoryActivity.this);
//        seekBar.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);
        sneekBarLl.addView(seekBar);
        // Get noticed while dragging
        seekBar.setNotifyWhileDragging(true);

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Now you have the minValue and maxValue of your RangeSeekbar
                minRange = minValue;
                maxRange = maxValue;
                priceMin.setText(String.valueOf(minValue));
                priceMax.setText(String.valueOf(maxValue));
            }
        });

        filterList.invalidate();
        ArrayList<FilterChild> filterChildArrayList = new ArrayList<>();

        if (bottomSheetGroup.size() == 3) {
            if (headingValue.equalsIgnoreCase("Category")) {
                sneekBarLayout.setVisibility(View.GONE);
                filterList.setVisibility(View.VISIBLE);
                filterChildArrayList = bottomSheetGroup.get(0).getFilterChildArray();
            } else if (headingValue.equalsIgnoreCase("Price")) {
                sneekBarLayout.setVisibility(View.VISIBLE);
                filterList.setVisibility(View.GONE);
                filterChildArrayList = bottomSheetGroup.get(1).getFilterChildArray();
                seekBar.setRangeValues(lowestPrice, highestPrice);
            }
        } else {
            if (headingValue.equalsIgnoreCase("Price")) {
                sneekBarLayout.setVisibility(View.VISIBLE);
                filterList.setVisibility(View.GONE);
                filterChildArrayList = bottomSheetGroup.get(0).getFilterChildArray();
                seekBar.setRangeValues(lowestPrice, highestPrice);
            }
        }

        if (minRange != -1 && maxRange != 0 ) {
            priceMin.setText(String.valueOf(minRange));
            priceMax.setText(String.valueOf(maxRange));
            seekBar.setSelectedMaxValue(maxRange);
            seekBar.setSelectedMinValue(minRange);
        } else {
            priceMin.setText(String.valueOf(lowestPrice));
            priceMax.setText(String.valueOf(highestPrice));
            seekBar.setSelectedMaxValue(highestPrice);
            seekBar.setSelectedMinValue(lowestPrice);
        }

        if (!headingValue.equalsIgnoreCase("Price")) {
            CategoryFilterAdapter filterAdapter = new CategoryFilterAdapter(CategoryActivity.this, filterChildArrayList);
            filterList.setAdapter(filterAdapter);
            filterAdapter.setOnAddressEditListener(this);
        }

        heading1.setText(headingValue);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headingValue.equalsIgnoreCase("Price") && maxRange != 0 ) {
                    filteredBottomSheetGroup.clear();
                    filteredBottomSheetGroup.addAll(bottomSheetGroup);
                    if (filteredBottomSheetGroup != null && filteredBottomSheetGroup.size() > 0) {
                        setFilterCategory();
                        Log.d("FilteredData",filteredBottomSheetGroup.toString());
                    }
                }
                dialog.dismiss();
            }
        });

        dialog = new BottomSheetDialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        dialog.show();
    }

    public void setFooter() {

        ImageView followFb = (ImageView) findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) findViewById(R.id.follow_cam);
        ImageView followPin = (ImageView) findViewById(R.id.follow_pintrest);

        /* Footer Action
         */
        followFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String socailLink=context.getString(R.string.follow_fb);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = helper.getFacebookUrl(CategoryActivity.this, socailLink);
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
                        Toast.makeText(CategoryActivity.this, "No application can handle this request."
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
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity( intent );
                } else {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_google)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(CategoryActivity.this, "No application can handle this request."
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
                        Toast.makeText(CategoryActivity.this, "No application can handle this request."
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
                        Toast.makeText(CategoryActivity.this, "No application can handle this request."
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
                        Toast.makeText(CategoryActivity.this, "No application can handle this request."
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
                        Toast.makeText(CategoryActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public void setFloatingMenu() {

        transparentLayout = (LinearLayout) findViewById(R.id.transparent_layout);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionCall = (FloatingActionButton) findViewById(R.id.floating_call);
        floatingActionChat = (FloatingActionButton) findViewById(R.id.floating_chat);
        floatingActionFaq = (FloatingActionButton) findViewById(R.id.floating_faq);
        // slide-up animation
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        materialDesignFAM.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    materialDesignFAM.setMenuButtonColorNormal(context.getResources().getColor(R.color.cart));
                    materialDesignFAM.setMenuButtonColorPressed(context.getResources().getColor(R.color.cart));
                    transparentLayout.setVisibility(View.VISIBLE);
                    transparentLayout.startAnimation(fadeIn);
                } else {
                    transparentLayout.startAnimation(fadeOut);
                    materialDesignFAM.setMenuButtonColorNormal(context.getResources().getColor(R.color.colorPrimary));
                    materialDesignFAM.setMenuButtonColorPressed(context.getResources().getColor(R.color.colorPrimary));
                    transparentLayout.setVisibility(View.GONE);

                }
            }
        });

        transparentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transparentLayout.getVisibility() == View.VISIBLE) {
                    materialDesignFAM.close(true);
                }
            }
        });

        floatingActionCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                materialDesignFAM.close(true);
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission()) {
                        requestPermission();
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+971509941516"));
                        startActivity(callIntent);
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+971509941516"));
                    startActivity(callIntent);
                }
            }
        });
        floatingActionChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

            }
        });
        floatingActionFaq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                materialDesignFAM.close(true);
                HomeActivity.congragTrack = 8;
                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission) {
                    Toast.makeText(CategoryActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(CategoryActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+971509941516"));
                    startActivity(intent);
                }
                break;

            default:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:+971509941516"));
                startActivity(intent);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private class GetSingleProduct extends AsyncTask<String, Void, Object> {

        String productID = "";
        private String TAG = CategoryActivity.GetSingleProduct.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CategoryActivity.this,
                    CategoryActivity.this.getResources().getString(
                            R.string.getting_products),
                    CategoryActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
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

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    String categoryId = databaseManager.getCategoryId(productID);
                    helper.dismissProgressDialog(progressDialog);
                    Intent intent = new Intent(CategoryActivity.this, ProductDeatils.class);
                    intent.putExtra("CategoryID", categoryId);
                    intent.putExtra("ProductID", productID);
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

    private class GetProducts extends AsyncTask<String, Void, Object> {
        String categoryID = "";
        private ProgressDialog progressDialog;
        private String TAG = CategoryActivity.GetProducts.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CategoryActivity.this, R.style.MyAlertDialogStyle);
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
                isHome = "false";
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(Html.fromHtml(heading));
                }
                setCategory();
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}
