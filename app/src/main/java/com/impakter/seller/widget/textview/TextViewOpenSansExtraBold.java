package com.impakter.seller.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by NaPro on 12/29/2015.
 */
public class TextViewOpenSansExtraBold extends TextView {

    public TextViewOpenSansExtraBold(Context context) {
        super(context);

        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-ExtraBold.ttf");
        this.setTypeface(face);
    }

    public TextViewOpenSansExtraBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-ExtraBold.ttf");
        this.setTypeface(face);
    }

    public TextViewOpenSansExtraBold(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-ExtraBold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
