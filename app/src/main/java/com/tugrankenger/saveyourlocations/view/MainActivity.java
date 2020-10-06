package com.tugrankenger.saveyourlocations.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tugrankenger.saveyourlocations.R;
import com.tugrankenger.saveyourlocations.adapter.CustomAdapter;
import com.tugrankenger.saveyourlocations.model.Place;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database;
    ArrayList<Place> placeList = new ArrayList<>();
    ListView listView;
    CustomAdapter customAdapter;

    //menu connection(invoke): -> onCreateOptionsMenu and onOptionsItemSelected

    @Override //menu connection:
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //if the menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_place){
            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("info","new"); // -> if you click the menu, it means you want to add new place
            startActivity(intent);
        }else if(item.getItemId()==R.id.about_menu){
            Toast toast= Toast.makeText(getApplicationContext(),"Coded By Tugran Kenger",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= findViewById(R.id.listView);
        getData();


    }

    public void getData(){

        customAdapter= new CustomAdapter(this,placeList);
        try {
            database= this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT*FROM places",null);

            int nameIx= cursor.getColumnIndex("name");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx= cursor.getColumnIndex("longitude");

            while(cursor.moveToNext()){
                String nameFromDatabase= cursor.getString(nameIx);
                String latitudeFromDatabase= cursor.getString(latitudeIx);
                String longitudeFromDatabase= cursor.getString(longitudeIx);

                Double latitude = Double.parseDouble(latitudeFromDatabase);
                Double longitude = Double.parseDouble(longitudeFromDatabase);

                Place place= new Place(nameFromDatabase,latitude,longitude);
                System.out.println(place.name);
                placeList.add(place);
            }
            customAdapter.notifyDataSetChanged();
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("place",placeList.get(position));
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String name= Objects.requireNonNull(customAdapter.getItem(position)).name;
                            database.execSQL("DELETE FROM places WHERE name =?",new String[] {name});
                            placeList.remove(position);
                            customAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Not Changed",Toast.LENGTH_LONG).show();
                    }
                });
                alert.show();
                return false;
            }
        });
    }

}