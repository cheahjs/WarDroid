package com.deathsnacks.wardroid.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.deathsnacks.wardroid.R;

/**
 * Created by Admin on 23/02/14.
 */
public class Utils {
    public static Spannable getImageSpannable(Context context, String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        for (int i = 0; i < builder.length(); i++) {
            if (Character.toString(builder.charAt(i)).equals("*")) {
                if (Character.toString(builder.charAt(i + 1)).equals("c")) {
                    builder.setSpan(new ImageSpan(context, R.drawable.credits), i, i + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i++;
                }
            }
        }
        return builder;
    }
}
