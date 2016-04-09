package com.example.aravind.task;

import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by aravind on 4/4/16.
 */
public class FetchCategories {
    private RequestQueue mRequestQueue;
    private static FetchCategories ourInstance = new FetchCategories();

    public static FetchCategories getInstance() {
        return ourInstance;
    }

    private FetchCategories() {
        mRequestQueue = Volley.newRequestQueue(ContextApplication.getAppContext());
    }
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
}
