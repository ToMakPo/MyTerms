package com.example.myterms.application;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public abstract class MyFunctions {
    private static final String TAG = "app: MyFunctions";

    public static int randBetween(int min, int max) {
        return (int)((Math.random() * (max - min + 1) + min));
    }

    public static String repeat(String str, int count) {
        String s = "";

        for (int i = 0; i < count; i++) {
            s += str;
        }

        return s;
    }
    public static String cropBegaining(String str, int length) {
        if (str.length() > length) {
            return "\u2026" + str.substring(1);
        } else {
            return str;
        }
    }
    public static String cropMiddle(String str, int maxLength) {
        if (str.length() > maxLength) {
            int f = (int)Math.ceil((maxLength - 1) / 2);
            int b = maxLength - f - 1;
            return str.substring(0, f) + "\u2026" + str.substring(b);
        } else {
            return str;
        }
    }
    public static String cropEnd(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length - 1) + "\u2026";
        } else {
            return str;
        }
    }
    public static String alignLeft(Object value, int width) {
        return alignLeft(value, width, "%s");
    }
    public static String alignLeft(Object value, int width, String format) {
        if (value == null) format = "%s";

        String str = String.format(format, value);

        if (str.length() > width) {
            return cropBegaining(str, width);
        } else if (str.length() == width) {
            return str;
        } else {
            return String.format(format.replace("%", "%" + width), value);
        }
    }
    public static String alignCenter(Object value, int width) {
        return alignCenter(value, width, "%s");
    }
    public static String alignCenter(Object value, int width, String format) {
        if (value == null) format = "%s";

        String str = String.format(format, value);

        if (str.length() > width) {
            return cropMiddle(str, width);
        } else if (str.length() == width) {
            return str;
        } else {
            int b = width - (int) Math.ceil((width - str.length()) / 2);

            return String.format("%" + width + "s", String.format("%-" + b + "s", str));
        }
    }
    public static String alignRight(Object value, int width) {
        return alignRight(value, width, "%s");
    }
    public static String alignRight(Object value, int width, String format) {
        if (value == null) format = "%s";

        String str = String.format(format, value);

        if (str.length() > width) {
            return cropEnd(str, width);
        } else if (str.length() == width) {
            return str;
        } else {
             return String.format(format.replace("%", "%" + width), value);
        }
    }

    public static void showToast(Context context, String message) {
        Toast toast=Toast.makeText(context,message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(Color.DKGRAY);
        TextView toastMessage = toast.getView().findViewById(android.R.id.message);
        toastMessage.setTextColor(Color.LTGRAY);
        toast.show();
    }
}
