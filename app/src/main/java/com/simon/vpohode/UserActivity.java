package com.simon.vpohode;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;


public class UserActivity extends AppCompatActivity {

    EditText nameBox;
    EditText termidBox;
    Spinner spinner, spinnerTemplate;
    String[] Style = {"Стиль не выбран", "Кэжуал", "Бизнес", "Элегантный", "Спорт", "Домашнее"};
    String[] Templates = {"Выбери шаблон", "Футболка","Рубашка","Кофта","Штаны","Джинсы","Осеняя куртка","Пальто"};
    Button delButton;
    Button saveButton;
    RadioGroup radGrp,radGrp2;
    RadioButton top,bottom;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        nameBox = (EditText) findViewById(R.id.name);
        termidBox = (EditText) findViewById(R.id.termid);
        spinner = findViewById(R.id.Style);
        spinnerTemplate = findViewById(R.id.Template);
        radGrp = (RadioGroup)findViewById(R.id.radios);
        radGrp2 = (RadioGroup)findViewById(R.id.radios2);
        delButton = (Button) findViewById(R.id.deleteButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        // configure spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> adapterTemplate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Templates);
        adapterTemplate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplate.setAdapter(adapterTemplate);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getLong("id");
        }
        // if 0, add
        if (userId > 0) {
            // get item by id from db
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            termidBox.setText(String.valueOf(userCursor.getInt(4)));
            spinner.setSelection(adapter.getPosition(userCursor.getString(2)));
            if (userCursor.getInt(3) == 1){
                radGrp.check(R.id.top);
            }else{radGrp.check(R.id.bottom);}
            userCursor.close();
        } else {
            // hide button Delete
            delButton.setVisibility(View.GONE);
        }

        // working with Radio buttons

    }
    @Override
    public void onResume() {
        super.onResume();

        spinnerTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (spinnerTemplate.getSelectedItemPosition()){
                    case 1:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("1");
                        spinner.setSelection(1);
                        radGrp.check(R.id.top);
                        radGrp2.check(R.id.layer1);
                        break;
                    case 2:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("2");
                        spinner.setSelection(2);
                        radGrp.check(R.id.top);
                        radGrp2.check(R.id.layer1);
                        break;
                    case 3:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("4");
                        spinner.setSelection(1);
                        radGrp.check(R.id.top);
                        radGrp2.check(R.id.layer2);
                        break;
                    case 4:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("2");
                        spinner.setSelection(3);
                        radGrp.check(R.id.bottom);
                        break;
                    case 5:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("2");
                        spinner.setSelection(1);
                        radGrp.check(R.id.bottom);
                        break;
                    case 6:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("5");
                        spinner.setSelection(1);
                        radGrp.check(R.id.top);
                        radGrp2.check(R.id.layer3);
                        break;
                    case 7:
                        nameBox.setText(spinnerTemplate.getSelectedItem().toString());
                        termidBox.setText("6");
                        spinner.setSelection(1);
                        radGrp.check(R.id.top);
                        radGrp2.check(R.id.layer3);
                        break;
                    // Шаблоны можно добавить тут + добавить имя в spinnerTemplate - --- - - - - --
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton bot = (RadioButton)radioGroup.findViewById(R.id.bottom);
                boolean isChecked = bot.isChecked();
                if(isChecked){
                    radGrp2.setVisibility(View.GONE);
                } else {
                    radGrp2.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void save(View view){
        Log.i("1top is checked?","top is checked? test");
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, nameBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_TERMID, Double.parseDouble(termidBox.getText().toString()));
        cv.put(DatabaseHelper.COLUMN_STYLE, spinner.getSelectedItem().toString());
        Log.i("top is checked?","top is checked? " + radGrp.getCheckedRadioButtonId());
        if (radGrp.getCheckedRadioButtonId() == R.id.top) {
            cv.put(DatabaseHelper.COLUMN_TOP, 1);
        } else {
            cv.put(DatabaseHelper.COLUMN_TOP, 0);
        }
        if (userId > 0) {
            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + userId, null);
        } else {
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
        goHome();
    }
    public void delete(View view){
        db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }
    private void goHome(){
        // close connection
        db.close();
        // move to main activity
        Intent intent = new Intent(this, Wardrobe.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}