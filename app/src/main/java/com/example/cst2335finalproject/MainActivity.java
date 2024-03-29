package com.example.cst2335finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // test push
        Button patrickBtn = findViewById(R.id.landingPatrick);

        Button saraBtn =findViewById(R.id.landingSaranja);

        Button kiranBtn = findViewById(R.id.landingKiran);

        Button nickBtn = findViewById(R.id.landingNick);

        if(patrickBtn != null)
        {
            patrickBtn.setOnClickListener(
                    v -> {
                        Intent carChargerIntent = new Intent(MainActivity.this, FindCarCharger.class);
                        startActivity(carChargerIntent);}
            );
        }
        if(kiranBtn != null) {
            kiranBtn.setOnClickListener(v -> {
                        Intent i = new Intent(MainActivity.this, Recipe.class);
                        startActivity(i);
            });
        }

        if(saraBtn != null)
        {
            saraBtn.setOnClickListener(
                    v -> {
                        Intent newsIntent = new Intent(MainActivity.this, NewsMainActivity.class);
                        startActivity(newsIntent);}
            );
        }

        if(nickBtn != null)
        {
            nickBtn.setOnClickListener(
                    v -> {
                        Intent currencyIntent = new Intent(MainActivity.this, CurrencyMainActivity.class);
                        startActivity(currencyIntent);}
            );
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(item.getItemId() == R.id.recipeItem){
            Intent i = new Intent(MainActivity.this, Recipe.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.chargingItem)
        {
            Intent i = new Intent(MainActivity.this, FindCarCharger.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.newsItem)
        {
            Intent i = new Intent(MainActivity.this, NewsMainActivity.class);
            startActivity(i);
        }
        else if (item.getItemId() == R.id.currencyItem)
        {
            Intent i = new Intent(MainActivity.this, CurrencyMainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
