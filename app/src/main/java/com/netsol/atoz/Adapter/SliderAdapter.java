package com.netsol.atoz.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.netsol.atoz.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by macmini on 12/13/17.
 */

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private List<String> HashMapForURL;

    public SliderAdapter(Context context, List<String> HashMapForURL) {
        this.context = context;
        this.HashMapForURL = HashMapForURL;
    }

    @Override
    public int getCount() {
        return HashMapForURL.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);

        ImageView sliderImage = (ImageView) view.findViewById(R.id.slider_image);

        Glide.with(context).load(HashMapForURL.get(position))
                .thumbnail(0.5f)
                .fitCenter()
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(null)
                .into(sliderImage);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}