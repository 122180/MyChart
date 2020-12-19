package com.example.mychart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mychart.common.Util;

public class NetworkMessage extends AppCompatActivity {
private ProgressBar mProgressBar;
private TextView tvMessage;
private Button btnClose,btnRetry;
private ConnectivityManager.NetworkCallback mNetworkCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_message);
        mProgressBar=findViewById(R.id.pbMessage);
        tvMessage=findViewById(R.id.tvMessage);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            mNetworkCallback=new ConnectivityManager.NetworkCallback()
            {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    tvMessage.setText(R.string.newtwork_info);
                }
            };

            ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),mNetworkCallback);
        }

    }

    public void btnRetry (View view)
    {
        mProgressBar.setVisibility(View.VISIBLE);
        if(Util.ConnectionAvailable(this))
        {
            finish();
        }
        else
        {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                mProgressBar.setVisibility(View.GONE);
                }
            },1000);
        }
    }
    public void btnClose(View view)
    {
        finishAffinity();
    }
}