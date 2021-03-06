package com.simon.vpohode;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {
   // private final String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    private final String weatherURL2 = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=8e923e31bdf57632b77f12106cf7f3ee&lang=ru&units=metric";
    private TextView textViewWeather,listofitems;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    ListView userList,userList2;
    Double term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewWeather = findViewById(R.id.textViewWeather);
        userList = (ListView)findViewById(R.id.list);
        userList2 = (ListView)findViewById(R.id.list2);
        listofitems = (findViewById(R.id.listofitems));
        databaseHelper = new DatabaseHelper(getApplicationContext());
        //show the weather
        //String city = editTextCity.getText().toString().trim();
        DownloadTask task = new DownloadTask();
        //String url = String.format(weatherURL, city);
        String url2 = String.format(weatherURL2, "Brno");
        task.execute(url2);
       // task.onPostExecute(url,1);
    }
    @Override
    public void onResume() {
        super.onResume();
        // open connection

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // close connection
        db.close();
        userCursor.close();
    }

    public void wardrobe(View view){
        Intent intent = new Intent(this, Wardrobe.class);
        startActivity(intent);
    }

    public void onClickShowItems(View view) {

        double result = 0;
        double result2 = 0;
        // connection to DB
        db = databaseHelper.getReadableDatabase();
        //get cursor from db to have list of termindexes
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_STYLE, DatabaseHelper.COLUMN_TOP,};
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 1", null);
        if (userCursor.moveToFirst()){
            double min = Integer.MAX_VALUE;
            do {
                if (min > Math.abs((30 - term)/9 - userCursor.getInt(4)/100)) {
                    min = Math.abs((30 - term)/9 - userCursor.getInt(4)/100);
                    result = userCursor.getInt(4);
                }
            }
            while (userCursor.moveToNext());
        }
        double min = Integer.MAX_VALUE;
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_TOP + " = 0", null);
        if (userCursor.moveToFirst()){
            do {
                if (min > Math.abs((30 - term)/9 - userCursor.getDouble(4))) {
                    min = Math.abs((30 - term)/9 - userCursor.getDouble(4));
                    result2 = userCursor.getDouble(4);
                    Log.i("Test result2 ","Result2 = " + userCursor.getInt(4));
                }
            }
            while (userCursor.moveToNext());
        }


        userAdapter = new SimpleCursorAdapter(this, R.layout.two_line_list_item, userCursor, headers, new int[]{R.id.text1, R.id.text2, R.id.text3}, 0);
        //db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " + DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
        userList.setAdapter(userAdapter);
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        String names = "";
        String names1 = "";
        if (userCursor.moveToFirst()) {
            do {
                if((userCursor.getInt(3) == 1)&& (userCursor.getDouble(4) == result)) {
                    names += userCursor.getString(1) + " или\n";
                }else if((userCursor.getInt(3) == 0)&& (userCursor.getDouble(4) == result2)){
                    names1 += userCursor.getString(1) + " или\n";
                }
            } while (userCursor.moveToNext());
        }
        Log.i("Why shorty?", "because = names 1 " + names + " result - " + result + " result2 - " + result2);
        listofitems.setText("На низ вам рекомендую: " + names1.substring(0,names1.length() - 5));

    }

    private class DownloadTask extends AsyncTask <String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override

        protected void onPostExecute (String s){
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    JSONObject list0 = jsonArray.getJSONObject(0);
                    JSONObject list1 = jsonArray.getJSONObject(1);
                    JSONArray forcast = list1.getJSONArray("weather");
                    JSONObject weather = forcast.getJSONObject(0);
                    String description = weather.getString("description");

                    JSONObject main0 = list0.getJSONObject("main");
                    String mainTem0 = main0.getString("feels_like");
                    JSONObject main1 = list1.getJSONObject("main");
                    String mainTem1 = main1.getString("feels_like");

                    // save the temperature
                    term = (Double.parseDouble(mainTem0) + Double.parseDouble(mainTem1))/2;

                    if (term >= 0 ) {
                        textViewWeather.setText("Сейчас: +" + (int)Double.parseDouble(mainTem0) + "\u2103 " + description);
                    } else {
                        textViewWeather.setText("Сейчас: " + (int)Double.parseDouble(mainTem0) + "\u2103 " + description);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



/*
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                JSONObject list = jsonArray.getJSONObject(1);

                JSONArray forcast1 = list.getJSONArray("weather");
                JSONObject weather = forcast1.getJSONObject(0);
                String description = weather.getString("description");

                JSONObject main = list.getJSONObject("main");
                String mainTem = main.getString("feels_like");

                // save the temperature
                term = Double.parseDouble(mainTem);

                String result = mainTem + " " + description;
                Double Temp = Double.parseDouble(mainTem);
                if (Temp >= 0 ) {
                    textViewWeather.setText("Погода в Брно: +" + result);
                } else {
                    textViewWeather.setText("Погода в Брно: " + result);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
*/
    }

}