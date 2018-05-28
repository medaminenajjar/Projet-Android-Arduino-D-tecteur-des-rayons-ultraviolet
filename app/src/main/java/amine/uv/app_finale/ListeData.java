package amine.uv.app_finale;

/**
 * Created by amine on 12/07/2017.
 */

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;


public class ListeData extends ListActivity {

    private BluetoothAdapter monBluetooth2 = null;
    private ArrayList<String> results = new ArrayList<String>();
    private String tableName = DatabaseHelper.TABLE_NAME;
    private SQLiteDatabase newDB;
    static String mac = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*ArrayAdapter<String> arraydata  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

                String uv = "fffff";
                String datee = "rrrrrrr";
                arraydata.add(uv +"\n" + datee);

        setListAdapter(arraydata);*/
        openAndQueryDatabase();
        displayResultList();
    }
    private void displayResultList() {
        TextView tView = new TextView(this);
        //tView.setText("This data is retrieved from the database ");
        getListView().addHeaderView(tView);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, results));
        getListView().setTextFilterEnabled(true);

    }
    private void openAndQueryDatabase() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
            newDB = dbHelper.getWritableDatabase();
            Cursor c = newDB.rawQuery("SELECT * FROM " +
                    tableName , null);

            if (c != null ) {
                if (c.moveToFirst()) {
                    do {
                        String uv = c.getString(c.getColumnIndex("uv"));
                        String date = c.getString(c.getColumnIndex("date"));
                        results.add("Indice UV : " + uv + "\nDate : " + date);
                    }while (c.moveToNext());
                }
            }
        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
            //if (newDB != null)
              //  newDB.execSQL("DELETE FROM " + tableName);
            newDB.close();
        }

    }

}

