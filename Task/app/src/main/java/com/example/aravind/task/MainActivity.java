package com.example.aravind.task;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView hostels,departments,canteens,messes;
    RequestQueue queue;
    String url = "https://spider.nitt.edu/lateral/appdev";
    Intent mapIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapIntent = new Intent(this,MapActivity.class);
        hostels = (TextView) findViewById(R.id.hostels);
        departments = (TextView) findViewById(R.id.departments);
        canteens = (TextView) findViewById(R.id.canteens);
        messes = (TextView) findViewById(R.id.messes);

        queue = FetchCategories.getInstance().getRequestQueue();
        JsonObjectRequest obj = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("categories");
                    hostels.setText(jsonArray.get(0).toString());
                    departments.setText(jsonArray.get(1).toString());
                    canteens.setText(jsonArray.get(2).toString());
                    messes.setText(jsonArray.get(3).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayError();
            }
        });
        queue.add(obj);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
    public void displayError(){
        Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
    }
    public void onHostelsClick(View v){
        mapIntent.putExtra("CATEGORY","hostels");
        startActivity(mapIntent);
    }
    public void onDepartmentsClick(View v){
        mapIntent.putExtra("CATEGORY","departments");
        startActivity(mapIntent);
    }
    public void onCanteensClick(View v){
        mapIntent.putExtra("CATEGORY","canteens");
        startActivity(mapIntent);
    }
    public void onMessesClick(View v){
        mapIntent.putExtra("CATEGORY","messes");
        startActivity(mapIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
