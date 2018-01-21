package com.netsol.atoz.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.netsol.atoz.R;

/**
 * Created by macmini on 12/10/17.
 */

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;

    public TextDrawable(String text, Context context) {
        this.text = text;
        this.paint = new Paint();
        paint.setColor(Color.BLACK);
        int scaledSize = context.getResources().getDimensionPixelSize(R.dimen.dimen_14s);
        paint.setTextSize(scaledSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect r = new Rect();
        paint.getTextBounds(text, 0, text.length(), r);
        int yPos = (Math.abs(r.height()))/2;
        int xPos = (Math.abs(r.width()))/2;
        canvas.drawText(text, xPos, yPos, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
