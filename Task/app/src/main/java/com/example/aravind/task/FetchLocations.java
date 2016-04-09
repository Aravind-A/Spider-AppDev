package com.example.aravind.task;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by aravind on 4/4/16.
 */
public class FetchLocations {
    private RequestQueue mRequestQueue;
    private static FetchLocations ourInstance = new FetchLocations();

    public static FetchLocations getInstance() {
        return ourInstance;
    }

    private FetchLocations() {
        mRequestQueue = Volley.newRequestQueue(ContextApplication.getAppContext());
    }
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
}
