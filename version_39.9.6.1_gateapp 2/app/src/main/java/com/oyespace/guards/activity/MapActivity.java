package com.oyespace.guards.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.oyespace.guards.R;
import com.oyespace.guards.network.ResponseHandler;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ResponseHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onSuccess(String response, Object data, int urlId, int position) {

    }

    @Override
    public void onFailure(Exception e, int urlId) {

    }
}
