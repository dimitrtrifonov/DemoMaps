package com.example.dima.demomaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        Button readyButton = (Button) findViewById(R.id.readyButton);
        View.OnClickListener onClickListener =new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMapsActivity();
            }
        };
        readyButton.setOnClickListener(onClickListener);
    }

    public void callMapsActivity(){
        EditText mLocationFrom = (EditText)findViewById(R.id.destinationFrom);
        EditText mLocationTo = (EditText)findViewById(R.id.destinationTo);

        if(mLocationFrom.getText().toString().length()>0 &&
                mLocationTo.getText().toString().length()>0) {


            Intent intent=new Intent(this,MapsActivity.class);
            intent.putExtra("From", mLocationFrom.getText().toString());

            intent.putExtra("To", mLocationTo.getText().toString());
            startActivity(intent);

            /*
            Bundle bundle = new Bundle();
            Log.v("mLoc ",mLocationFrom.toString());
            bundle.putString("From", mLocationFrom.toString());
            bundle.putString("To", mLocationTo.toString());
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtras(bundle);
            */
            startActivity(intent);
        }
    }

}
