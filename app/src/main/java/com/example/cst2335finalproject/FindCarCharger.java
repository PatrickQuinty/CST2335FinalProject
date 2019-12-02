package com.example.cst2335finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        EditText latitudeIn = findViewById(R.id.latInput),
        longitudeIn = findViewById(R.id.longInput);
        String latitude = latitudeIn.getText().toString(),
        longitude = longitudeIn.getText().toString();

        if(search != null)
        {
            search.setOnClickListener(
                    v -> {
                        //TODO add code to launch search
                        stationView.setVisibility(View.VISIBLE);
                        new ChargingStationQuery().execute();
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
    private class ChargingStationQuery extends AsyncTask<String, Integer, String[]> {

        private String[] id = new String[10],
                title = new String[10],
                latitude = new String[10],
                longitude = new String[10],
                phone = new String[10];

        @Override
        protected String[] doInBackground(String... strings) {


            try {
                String chargeQuery = "https://api.openchargemap.io/v3/poi/?output=json&latitude=" +
                        strings[0] + "&longitude=" + //should be the latitude
                        strings[1] + "&maxresults=10"; //should be the longitude
                URL chargingURL = new URL(chargeQuery);
                HttpURLConnection chargingConnect = (HttpURLConnection)chargingURL.openConnection();
                InputStream chargeInpStream = chargingConnect.getInputStream();
                BufferedReader read =  new BufferedReader(
                        new InputStreamReader(chargeInpStream, "UTF-8"), 8);
                StringBuilder builder =new StringBuilder();
                String line;

                while((line = read.readLine())  != null)
                {
                    builder.append(line + "\n");
                }

                String result = builder.toString();

                JSONArray jsonArr = new JSONArray(result);

                for (int i = 0; i<10; i++)
                {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    JSONObject address = jsonObj.getJSONObject("AddressInfo");
                    title[i] = address.getString("Title");
                    latitude[i] = String.valueOf(address.getDouble("Latitude"));
                    longitude[i] = String.valueOf(address.getDouble("Longitude"));
                    phone[i] = jsonObj.getString("ContactTelephone1");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new String[0];
        }
        //TODO fill in the class


        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            for (int i = 0; i < 10; i++)
            { //loops through array of search results and adds each of them to arrayList as a new station object
                stations.add(new ChargingStation(id[i], title[i], latitude[i], longitude[i], phone[i]));
            }
        }
    }

    private class ChargingStation
    {

        public ChargingStation(String id, String title, String latitude, String longitude, String phone)
        {

        }
    }
}
