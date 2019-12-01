package com.example.cst2335finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FindCarCharger extends AppCompatActivity {

    ArrayList<ChargingStation> stations = new ArrayList<>();
    CarDBOpenHelper dbOpener = new CarDBOpenHelper(this);
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_charger_landing);

        //TODO uncomment this once you are fairly confident the database has what it needs in it
        //db = dbOpener.getWritableDatabase();


        ListView stationView = findViewById(R.id.stationList);
        Button search = findViewById(R.id.statnSrch);

        if(search != null)
        {
            search.setOnClickListener(
                    v -> {
                        //TODO add code to launch search
                        stationView.setVisibility(View.VISIBLE);
                    }
            );
        }
    }

    private class StationAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return stations.size();
        }

        @Override
        public Object getItem(int i) {
            return stations.get(i); //TODO might need updating
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View thisRow = null; //TODO remove null statement

                //TODO make a layout for a single station to show
                //thisRow = getLayoutInflater().inflate(R.layout.lab4_msg_receive_relative, null);

            return thisRow;
        }
    }
    private class ChargingStation {
        //TODO fill in the class
    }
}
