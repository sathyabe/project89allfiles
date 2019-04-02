package com.example.voicecontrolforwheelchair;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);

                return true;
            case R.id.mv:
                Uri mvurl=Uri.parse("geo:13.0827,80.2707");
                Intent intent1=new Intent(Intent.ACTION_VIEW,mvurl);
                intent1.setPackage("com.google.android.apps.maps");
                startActivity(intent1);

            default:

                return super.onOptionsItemSelected(item);
        }
    }
}
