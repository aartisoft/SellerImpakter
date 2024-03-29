package com.impakter.seller.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.impakter.seller.R;

import jp.wasabeef.blurry.Blurry;

public class Demo extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_layout);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                long startMs = System.currentTimeMillis();
                Blurry.with(Demo.this)
                        .radius(25)
                        .sampling(1)
                        .color(Color.argb(66, 0, 255, 255))
                        .async()
                        .capture(findViewById(R.id.right_top))
                        .into((ImageView) findViewById(R.id.right_top));

                Blurry.with(Demo.this)
                        .radius(10)
                        .sampling(8)
                        .async()
                        .capture(findViewById(R.id.right_bottom))
                        .into((ImageView) findViewById(R.id.right_bottom));

                Blurry.with(Demo.this)
                        .radius(25)
                        .sampling(1)
                        .color(Color.argb(66, 255, 255, 0))
                        .async()
                        .capture(findViewById(R.id.left_bottom))
                        .into((ImageView) findViewById(R.id.left_bottom));

                Log.d(getString(R.string.app_name),
                        "TIME " + String.valueOf(System.currentTimeMillis() - startMs) + "ms");
            }
        });

        findViewById(R.id.button).setOnLongClickListener(new View.OnLongClickListener() {

            private boolean blurred = false;

            @Override public boolean onLongClick(View v) {
                if (blurred) {
                    Blurry.delete((ViewGroup) findViewById(R.id.content));
                } else {
                    long startMs = System.currentTimeMillis();
                    Blurry.with(Demo.this)
                            .radius(25)
                            .sampling(2)
                            .async()
                            .animate(500)
                            .onto((ViewGroup) findViewById(R.id.content));
                    Log.d(getString(R.string.app_name),
                            "TIME " + String.valueOf(System.currentTimeMillis() - startMs) + "ms");
                }

                blurred = !blurred;
                return true;
            }
        });
    }
}
