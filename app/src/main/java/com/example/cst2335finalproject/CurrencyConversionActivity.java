package com.example.cst2335finalproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class CurrencyConversionActivity extends AppCompatActivity {

    String foreignSymbol;
    String currentSymbol;
    double foreignRate = 1;
    int spinnerPosition;

    Button buttonConvert, buttonChangeUnit;
    EditText amount;
    TextView textCurrentUnit, textForeignUnit,
            answerCurrentAmount, answerConvertedAmount,
            answerCurrentUnit, answerForeignUnit;

    SharedPreferences sp;
    String previousSearchAmount, spCurrentSymbol, spForeignSymbol;
    boolean isFromPreviousSearch;

    //used to verify that symbols are swapped
    boolean isSwapped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_conversion);
        sp = getSharedPreferences("FOREIGN_CURR_PREF", MODE_PRIVATE);
        previousSearchAmount = sp.getString("LAST_AMOUNT", "0");
        spCurrentSymbol = sp.getString("CURRENT_SYMBOL", "0");
        spForeignSymbol = sp.getString("FOREIGN_SYMBOL", "0");

        currentSymbol = getIntent().getStringExtra("CURRENT_SYMBOL");
        foreignSymbol = getIntent().getStringExtra("FOREIGN_SYMBOL");
        spinnerPosition = getIntent().getIntExtra("CURRENT_POSITION", 1);

        if (spCurrentSymbol.equals(currentSymbol) && spForeignSymbol.equals(foreignSymbol)) {
            isFromPreviousSearch = true;
            isSwapped = sp.getBoolean("isSwapped", false);
        } else {
            isFromPreviousSearch = false;
        }

        new GetRates(currentSymbol, foreignSymbol).execute();


        amount = findViewById(R.id.amount);
        textForeignUnit = findViewById(R.id.textForeignUnit);
        textCurrentUnit = findViewById(R.id.textCurrentUnit);

        answerCurrentUnit = findViewById(R.id.answerCurrentUnit);
        answerForeignUnit = findViewById(R.id.answerForeignUnit);
        answerCurrentAmount = findViewById(R.id.answerCurrentAmount);
        answerConvertedAmount = findViewById(R.id.answerConvertedAmount);
        buttonChangeUnit = findViewById(R.id.buttonChangeUnit);
        buttonConvert = findViewById(R.id.buttonConvert);

        buttonChangeUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSwapped) {
                    textForeignUnit.setText(foreignSymbol);
                    textCurrentUnit.setText(currentSymbol);
                } else {
                    textCurrentUnit.setText(foreignSymbol);
                    textForeignUnit.setText(currentSymbol);
                }
                isSwapped = !isSwapped;
            }
        });

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringAmount = amount.getText().toString();
                if (stringAmount.isEmpty()) {
                    Toast.makeText(CurrencyConversionActivity.this, "Enter a valid number", Toast.LENGTH_LONG).show();
                } else {
                    double newAmount = Double.parseDouble(stringAmount);
                    double answer = 0.0;
                    if (!isSwapped) {
                        answer = newAmount * foreignRate;
                    } else {
                        answer = newAmount / foreignRate;
                    }

                    answerCurrentAmount.setText(stringAmount);
                    String stringAnswer = String.valueOf(answer);
                    answerConvertedAmount.setText(stringAnswer);
                    sp.edit().putInt("CURRENT_POSITION", spinnerPosition)
                            .putString("CURRENT_SYMBOL", currentSymbol)
                            .putString("FOREIGN_SYMBOL", foreignSymbol)
                            .putString("CONVERTED_AMOUNT", stringAmount)
                            .putBoolean("isSwapped", isSwapped)
                            .apply();
                }
            }

        });
    }

        //Async Task
        class GetRates extends AsyncTask<Void, Void, String> {

            String currentSymbol, foreignSymbol;
            ProgressDialog progressDialog;

            GetRates(String currentSymbol, String foreignSymbol) {
                this.currentSymbol = currentSymbol;
                this.foreignSymbol = foreignSymbol;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new ProgressDialog(CurrencyConversionActivity.this);
                String wait = getResources().getString(R.string.wait);
                progressDialog.setMessage(wait);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String json = "";
                String serverURL = "https://api.exchangeratesapi.io/latest?base=" + currentSymbol + "&symbols=" + foreignSymbol;

                try {
                    URL url = new URL(serverURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader streamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        stringBuilder.append(inputLine);
                    }
                    reader.close();
                    streamReader.close();
                    json = stringBuilder.toString();

                } catch (java.io.IOException x) {
                    x.printStackTrace();
                }
                return json;
            }

            @Override
            protected void onPostExecute(String p) {
                super.onPostExecute(p);
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                }
                if (p != "") {
                    try {
                        JSONObject jsonObj = new JSONObject(p);
                        JSONObject rates = jsonObj.getJSONObject("rates");

                        Iterator<String> keys = rates.keys();
                        while (keys.hasNext()) {
                            String stringSymbol = keys.next();
                            foreignRate = rates.getDouble(stringSymbol);
                        }

                        if (isFromPreviousSearch) {
                            amount.setText(currentSymbol);

                            if (isSwapped) {
                                textCurrentUnit.setText(foreignSymbol);
                                textForeignUnit.setText(currentSymbol);
                                answerCurrentUnit.setText(foreignSymbol);
                                answerForeignUnit.setText(currentSymbol);
                            } else {
                                textCurrentUnit.setText(currentSymbol);
                                textForeignUnit.setText(foreignSymbol);
                                answerCurrentUnit.setText(currentSymbol);
                                answerForeignUnit.setText(foreignSymbol);
                            }
                            double newAmount = Double.parseDouble(previousSearchAmount);
                            double answer;
                            if (isSwapped) {
                                answer = newAmount / foreignRate;
                            } else {
                                answer = newAmount * foreignRate;
                            }
                            answerCurrentAmount.setText(previousSearchAmount);
                            String stringAnswer = String.valueOf(answer);
                            answerConvertedAmount.setText(stringAnswer);

                        } else {
                            textCurrentUnit.setText(currentSymbol);
                            textForeignUnit.setText(foreignSymbol);
                            answerCurrentUnit.setText(currentSymbol);
                            answerForeignUnit.setText(foreignSymbol);
                            String stringRate = String.valueOf(foreignRate);
                            answerConvertedAmount.setText(stringRate);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CurrencyConversionActivity.this, "Error processing rates", Toast.LENGTH_LONG).show();
                }
            }
        }


    //inflater menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                final Dialog helpMessage = new Dialog(CurrencyConversionActivity.this);
                helpMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpMessage.setContentView(R.layout.dialog_currency);
                Button buttonOk = helpMessage.findViewById(R.id.buttonOk);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { helpMessage.dismiss();
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

