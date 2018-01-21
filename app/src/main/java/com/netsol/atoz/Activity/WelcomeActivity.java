package com.netsol.atoz.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netsol.atoz.AutoViewPager.AutoScrollViewPager;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.util.List;

/**
 * Created by macmini on 9/25/17.
 */

public class WelcomeActivity extends AppCompatActivity implements AlertAction {

    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private AutoScrollViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private Helper helper;
    TextView[] dots;
    private int[] layouts;
    Button buttonNext;
    Button buttonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        context = WelcomeActivity.this;
        helper = new Helper(this,this);
        viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        buttonNext = (Button) findViewById(R.id.button_next);
        buttonSkip = (Button) findViewById(R.id.button_skip);
        ImageView followFb = (ImageView) findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) findViewById(R.id.follow_cam);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3};
        addBottomDots(0);

        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter(layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition == 2) {
                    editor.putString(Constants.IS_FIRST_INSTALL, "false");
                    editor.apply();
                    Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (currentPosition == 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    buttonSkip.setVisibility(View.INVISIBLE);
                    buttonNext.setText(WelcomeActivity.this.getString(R.string.button_get_started));
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    buttonSkip.setVisibility(View.VISIBLE);
                    buttonNext.setText(WelcomeActivity.this.getString(R.string.button_next));
                }
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(Constants.IS_FIRST_INSTALL, "false");
                editor.apply();
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                    String facebookUrl = helper.getFacebookUrl(WelcomeActivity.this, socailLink);
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
                        Toast.makeText(WelcomeActivity.this, "No application can handle this request."
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
                        Toast.makeText(WelcomeActivity.this, "No application can handle this request."
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
                        Toast.makeText(WelcomeActivity.this, "No application can handle this request."
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
                        Toast.makeText(WelcomeActivity.this, "No application can handle this request."
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
                        Toast.makeText(WelcomeActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setGravity(Gravity.TOP);
            dots[i].setTextSize(42);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);

        if (currentPage == 2) {
            buttonSkip.setVisibility(View.INVISIBLE);
            buttonNext.setText(WelcomeActivity.this.getString(R.string.button_get_started));
        } else {
            buttonSkip.setVisibility(View.VISIBLE);
            buttonNext.setText(WelcomeActivity.this.getString(R.string.button_next));
        }
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        int[] layouts1;

        MyViewPagerAdapter(int[] layouts) {
            this.layouts1 = layouts;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts1[position], container, false);
//            ImageView iconbackground;
//            if (position == 0) {
//                iconbackground = (ImageView) view.findViewById(R.id.bg1);
//                Picasso.with(WelcomeActivity.this).load(R.drawable.bg1ss).fit().into(iconbackground);
//                ImageView imageView1_icon_booxtown_intro = (ImageView) view.findViewById(R.id.imageView1_icon_booxtown_intro);
//                Picasso.with(getApplicationContext()).load(R.drawable.icon_booxtown_intro).into(imageView1_icon_booxtown_intro);
//            } else if (position == 1) {
//                iconbackground = (ImageView) view.findViewById(R.id.bg2);
//                Picasso.with(WelcomeActivity.this).load(R.drawable.bg2ss).fit().into(iconbackground);
//
//                ImageView imageView2_icon_booxtown_intro = (ImageView) view.findViewById(R.id.imageView2_icon_booxtown_intro);
//                Picasso.with(getApplicationContext()).load(R.drawable.icon_booxtown_intro).into(imageView2_icon_booxtown_intro);
//            } else if (position == 2) {
//                iconbackground = (ImageView) view.findViewById(R.id.bg3);
//                Picasso.with(WelcomeActivity.this).load(R.drawable.bg3s).fit().into(iconbackground);
//
//                ImageView imageView3_icon_booxtown_intro = (ImageView) view.findViewById(R.id.imageView3_icon_booxtown_intro);
//                Picasso.with(getApplicationContext()).load(R.drawable.icon_booxtown_intro).into(imageView3_icon_booxtown_intro);
//            } else if (position == 3) {
//                iconbackground = (ImageView) view.findViewById(R.id.bg4);
//                Picasso.with(WelcomeActivity.this).load(R.drawable.bg4ss).fit().into(iconbackground);
//
//                ImageView imageView4_icon_booxtown_intro = (ImageView) view.findViewById(R.id.imageView4_icon_booxtown_intro);
//                Picasso.with(getApplicationContext()).load(R.drawable.icon_booxtown_intro).into(imageView4_icon_booxtown_intro);
//            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}

