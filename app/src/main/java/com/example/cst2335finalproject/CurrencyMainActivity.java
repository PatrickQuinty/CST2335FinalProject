package com.example.cst2335finalproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CurrencyMainActivity extends AppCompatActivity {

    Button buttonCurrencyHome, buttonConversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_main);

        buttonCurrencyHome = findViewById(R.id.buttonCurrencyHome);

        buttonConversion = findViewById(R.id.buttonConversion);

        buttonConversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CurrencyMainActivity.this, CurrencyConversionActivity.class);

                startActivity(intent);
            }
        });

    }

    //inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_help:
                final Dialog helpMessage = new Dialog(CurrencyMainActivity.this);
                helpMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpMessage.setContentView(R.layout.dialog_currency);
                Button buttonOk = helpMessage.findViewById(R.id.buttonOk);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helpMessage.dismiss();
                    }
                });

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(helpMessage.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER;
                helpMessage.getWindow().setAttributes(layoutParams);
                helpMessage.setCancelable(true);
                helpMessage.show();
                break;

        }
        return (super.onOptionsItemSelected(item));
    }
}
