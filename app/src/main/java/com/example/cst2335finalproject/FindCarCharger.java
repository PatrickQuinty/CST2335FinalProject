package com.example.cst2335finalproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    ListView stationView;
    BaseAdapter stationAdapter;
    CarDBOpenHelper dbOpener = new CarDBOpenHelper(this);
    SQLiteDatabase db;
    String searchLatitude, searchLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_charger_landing);

        //TODO uncomment this once you are fairly confident the database has what it needs in it
        //db = dbOpener.getWritableDatabase();


        stationView = findViewById(R.id.stationList);
        Button search = findViewById(R.id.statnSrch);
        EditText latitudeIn = findViewById(R.id.latInput),
        longitudeIn = findViewById(R.id.longInput);
        latitudeIn.setText("45.347571");
        longitudeIn.setText("-75.756140");
        searchLatitude = latitudeIn.getText().toString();
        searchLongitude = longitudeIn.getText().toString();



        if(search != null)
        {
            search.setOnClickListener(
                    v -> new ChargingStationQuery().execute());
        }
    }

    private class StationAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return stations.size();
        }

        @Override
        public String[] getItem(int i) {
            return stations.get(i).getStationInfo();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View thisRow;
            thisRow = getLayoutInflater().inflate(R.layout.charge_station_row, null);
            TextView title = thisRow.findViewById(R.id.stationLocationTitle),
                    latitude = thisRow.findViewById(R.id.chargingLatitude),
                    longitude = thisRow.findViewById(R.id.chargingLongitude),
                    phone = thisRow.findViewById(R.id.chargeStationPhone);
            String[] data = getItem(i);
            title.setText(data[1]);
            latitude.setText(data[2]);
            longitude.setText(data[3]);
            phone.setText(data[4]);

            Button directions = thisRow.findViewById(R.id.chargingDirectionBtn);

            if(directions != null)
            {
                directions.setOnClickListener(
                        v -> {
                            // Create a Uri from an intent string. Use the result to create an Intent.
                            String mapSearch = "google.navigation:q=" + data[2] + "," + data[3]
                                    + "&mode=d";
                            Uri mapsIntentUri = Uri.parse(mapSearch);

                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
                            // Make the Intent explicit by setting the Google Maps package
                            mapIntent.setPackage("com.google.android.apps.maps");

                            // Attempt to start an activity that can handle the Intent
                            startActivity(mapIntent);

                        }
                );
            }


            return thisRow;
        }
    }
    private class ChargingStationQuery extends AsyncTask<String, Integer, String> {

        private String ret;
        private String[] id = new String[10],
                title = new String[10],
                latitude = new String[10],
                longitude = new String[10],
                phone = new String[10];

        @Override
        protected String doInBackground(String... strings) {


            try {
                String chargeQuery = "https://api.openchargemap.io/v3/poi/?output=json&latitude=" +
                        searchLatitude + "&longitude=" + //should be the latitude
                        searchLongitude + "&maxresults=10"; //should be the longitude
//                chargeQuery = "https://api.openchargemap.io/v3/poi/?output=json&latitude=45.347571&longitude=-75.756140&maxresults=10";
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

                for (int i = 0; i<jsonArr.length(); i++)
                {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    JSONObject address = jsonObj.getJSONObject("AddressInfo");
                    id[i] = String.valueOf(jsonObj.getInt("ID"));
                    title[i] = address.getString("Title");
                    latitude[i] = String.valueOf(address.getDouble("Latitude"));
                    longitude[i] = String.valueOf(address.getDouble("Longitude"));
                    if(!address.isNull("ContactTelephone1"))
                    phone[i] = address.getString("ContactTelephone1");
                    else
                        phone[i] = "No phone number available";
                }

            }
            catch(MalformedURLException mfe){ ret = "Malformed URL exception"; return ret;}
            catch(IOException ioe)          { ret = "IO Exception. Is the Wifi connected?"; return ret;}
            catch (JSONException e) {
                e.printStackTrace();
            }
            return ret;
        }


        @Override
        protected void onPostExecute(String fromDoInBackground) {
            super.onPostExecute(fromDoInBackground);
            for (int i = 0; i < id.length; i++)
            { //loops through array of search results and adds each of them to arrayList as a new station object
                stations.add(new ChargingStation(id[i], title[i], latitude[i], longitude[i], phone[i]));
            }
            stationView.setAdapter(stationAdapter = new StationAdapter());
            stationView.setVisibility(View.VISIBLE);
        }
    }

    private class ChargingStation
    {

        String id, title, latitude, longitude, phone;
        public ChargingStation(String id, String title, String latitude, String longitude, String phone)
        {
            this.id = id;
            this.title = title;
            this.latitude = latitude;
            this.longitude = longitude;
            this.phone = phone;
        }

        public String[] getStationInfo()
        {
            return new String[]{id, title, latitude, longitude, phone};
        }
    }
}
