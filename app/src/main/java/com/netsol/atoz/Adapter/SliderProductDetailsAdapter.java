package com.netsol.atoz.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.netsol.atoz.R;

import java.util.List;

/**
 * Created by macmini on 1/22/18.
 */

public class SliderProductDetailsAdapter extends PagerAdapter {

    private Context context;
    private List<String> HashMapForURL;

    public SliderProductDetailsAdapter(Context context, List<String> HashMapForURL) {
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
        View view = inflater.inflate(R.layout.item_slider_product, null);

        ImageView sliderImage = (ImageView) view.findViewById(R.id.slider_image_product);

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
