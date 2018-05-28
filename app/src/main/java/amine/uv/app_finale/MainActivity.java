package amine.uv.app_finale;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter monBluetooth = null;
    BluetoothDevice monDevice = null;
    BluetoothSocket monSocket = null;
    Button btn, btn2;
    UUID monUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int bluetooth_activee = 1;
    private static final int bluetooth_cnx = 2;
    private static final int MESSAGE_READ = 3;
    MenuItem connect;
    boolean cnx = false;
    String macc = null;
    String vvv = null;
    TextView tv;
    TextView tv2;
    TextView tv3;
    int uv = 1;
    Intent liste, liste2;
    ConnectedThread connectedThread;
    Handler mHandler;
    private StringBuilder recDataString = new StringBuilder();
    int numBytes;
    DatabaseHelper mDatabaseHelper;
    private static final String TAG = "ListDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mDatabaseHelper = new DatabaseHelper(this);
        connect = (MenuItem) findViewById(R.id.menu_connect);
        btn = (Button) findViewById(R.id.button);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tl = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tl);

        monBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (monBluetooth == null) {

            Toast.makeText(getApplicationContext(), "Erreur", Toast.LENGTH_LONG).show();

        } else if (!monBluetooth.isEnabled()) {
            Intent activer_blue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activer_blue, bluetooth_activee);
        }

      /*  LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater Mi = getMenuInflater();

        Mi.inflate(R.menu.mon_menu, menu);
        getMenuInflater().inflate(R.menu.mon_menu, menu);
        menu.findItem(R.id.menu_connect).setVisible(false);
        menu.findItem(R.id.menu_about).setVisible(false);
        menu.findItem(R.id.menu_data).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_about) {

            Intent i = new Intent(this, Main2Activity.class);
            startActivity(i);

        } else if (item.getItemId() == R.id.menu_connect) {

            if (cnx) {


                try {
                    monSocket.close();
                    Toast.makeText(getApplicationContext(), "Bluetooth Déconnecté", Toast.LENGTH_LONG).show();
                    cnx = false;
                } catch (IOException ee) {
                    Toast.makeText(getApplicationContext(), "erreur : " + ee, Toast.LENGTH_LONG).show();


                }


            } else {
                liste = new Intent(MainActivity.this, ListeBluetooth.class);
                startActivityForResult(liste, bluetooth_cnx);

            }

        } else if (item.getItemId() == R.id.menu_data) {
            liste2 = new Intent(MainActivity.this, ListeData.class);
            startActivityForResult(liste2, 99);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case bluetooth_activee:
                if (resultCode == Activity.RESULT_OK) {

                    Toast.makeText(getApplicationContext(), "Bluetooth Activé", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Bluetooth n'est pas activé", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case bluetooth_cnx:
                if (resultCode == Activity.RESULT_OK) {
                    macc = data.getExtras().getString(ListeBluetooth.mac);
                    monDevice = monBluetooth.getRemoteDevice(macc);

                    try {
                        monSocket = monDevice.createInsecureRfcommSocketToServiceRecord(monUUID);
                        monSocket.connect();
                        Toast.makeText(getApplicationContext(), "Connecter avec succès", Toast.LENGTH_LONG).show();

                        connectedThread = new ConnectedThread(monSocket);
                        connectedThread.start();


                        cnx = true;
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "erreur Socket: " + e, Toast.LENGTH_LONG).show();
                        cnx = false;


                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Connexion interrompue", Toast.LENGTH_LONG).show();

                }
            case 99:
                if (resultCode == Activity.RESULT_OK) {

            }

        }
    }

    public void changeUV (View view){
        if (cnx){
        tv = (TextView) findViewById(R.id.textView);
            final View main = findViewById(R.id.activity_main);

            tv2 = (TextView) findViewById(R.id.textView2);
        //tv3 = (TextView) findViewById(R.id.textView8);
        btn = (Button)findViewById(R.id.button);
        btn.setVisibility(View.VISIBLE);

        btn2 = (Button)findViewById(R.id.button2);
        btn2.setVisibility(View.INVISIBLE);

        connectedThread.write("1");
        //uv = connectedThread.read();

        //uv = uv + 1;
            mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) { //android.os.
                    //tv = (TextView) findViewById(R.id.textView);

                    String writeBuf = (String) msg.obj;
                    int begin = (int)msg.arg1;
                    int end = (int)msg.arg2;
                    switch(msg.what) {
                        case MESSAGE_READ:
                            String writeMessage = new String(writeBuf);
                            recDataString.append(writeBuf);

                            int a = recDataString.indexOf("}");
                            String writeMessageee = "1";
                            String writeMessagee = "";
                            if (a>0){

                                writeMessagee = recDataString.substring(0, a);
                                int taille = writeMessagee.length();
                                if (recDataString.charAt(0) == '{'){

                                    writeMessageee = recDataString.substring(1,taille);
                                    Log.d("Resultat",writeMessageee);
                                }

                                recDataString.delete(0,recDataString.length());
                                try {
                                    uv = Integer.parseInt(writeMessageee);
                                    /*java.sql.Timestamp timestamp= null;
                                    java.util.Date date= new java.util.Date(timestamp.getTime());
                                    String sToday = date.toString();*/
                                    Calendar c = Calendar.getInstance();
                                    System.out.println("Current time => " + c.getTime());

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    String formattedDate = df.format(c.getTime());

                                    String datee =  formattedDate;



                                    AddData(writeMessageee,datee);
                                    //mDatabaseHelper.getData().getString(1);
                                    //Log.d("Resultattt",mDatabaseHelper.getData().getString(0));
                                    Log.d(TAG, "populateListView: Displaying data in the ListView.");

                                    Cursor data = mDatabaseHelper.getData();
                                    ArrayList<String> listData = new ArrayList<>();
                                    while(data.moveToNext()){
                                        //get the value from the database in column 1
                                        //then add it to the ArrayList
                                        listData.add(data.getString(1) +" " +data.getString(2));
                                    }
                                    for (int i = 0; i<listData.size();i++)
                                    Log.d("Enfin", listData.get(i));
                                    /*for (String message : listData)
                                    {
                                    }*/

                                    //listData.get(1);




                                    //changeUV(findViewById(R.id.activity_main));

                                    if (uv <= 2 && uv >= 1){

                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("\n Port de lunettes de soleil en cas de journées ensoleillées.").setPositiveButton("OK",null);
                                                //.setNegativeButton("Cancel", null);
                                                AlertDialog alt = builder.create();

                                                alt.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
                                                alt.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                alt.show();
                                            }
                                        });
                                        tv.setText(""+uv);
                                        tv2.setText("Faible");
                                        tv.setTextColor(Color.parseColor("#0000ff"));
                                        tv2.setTextColor(Color.parseColor("#0000ff"));
                                        //main.setBackgroundColor(Color.parseColor("#0000ff"));

                                    }else if (uv <= 5 && uv >= 3){
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("Couvrez-vous, portez un chapeau et des lunettes de soleil. Appliquez un écran solaire de protection moyenne (indice de 15 à 29) surtout si vous êtes à l’extérieur pendant plus de 30 minutes. Cherchez l’ombre quand le soleil est au méridien.").setPositiveButton("OK",null);
                                                AlertDialog alt = builder.create();

                                                alt.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
                                                alt.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                alt.show();
                                            }
                                        });
                                        tv.setText(""+uv);
                                        tv2.setText("Modéré");
                                        tv.setTextColor(Color.parseColor("#f0573b"));
                                        tv2.setTextColor(Color.parseColor("#f0573b"));


                                    }else if (uv <= 7 && uv >= 6){
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("Réduisez l’exposition entre 12 h et 16 h. Appliquez un écran solaire de haute protection (indice de 30 à 50), portez un chapeau et des lunettes de soleil et placez-vous à l’ombre.").setPositiveButton("OK",null);
                                                AlertDialog alt = builder.create();

                                                alt.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
                                                alt.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                alt.show();
                                            }
                                        });
                                        tv.setText(""+uv);
                                        tv2.setText("Élevé");
                                        tv.setTextColor(Color.parseColor("#ba0303"));
                                        tv2.setTextColor(Color.parseColor("#ba0303"));
                                    }else if (uv <= 10 && uv >= 8){
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("Sans protection, la peau sera endommagée et peut brûler. Évitez l’exposition au soleil entre 12 h et 16 h. Recherchez l’ombre, couvrez-vous, portez un chapeau et des lunettes de soleil, et appliquez un écran solaire de très haute protection (indice + 50).").setPositiveButton("OK",null);
                                                AlertDialog alt = builder.create();

                                                alt.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
                                                alt.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                alt.show();
                                            }
                                        });
                                        tv.setText(""+uv);
                                        tv2.setText("Très élevé");
                                        tv.setTextColor(Color.parseColor("#bf0c0c"));
                                        tv2.setTextColor(Color.parseColor("#bf0c0c"));
                                    }else if (uv >= 11){
                                        btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setMessage("La peau non protégée sera endommagée et peut brûler en quelques minutes. Évitez toute exposition au Soleil et, si ce n’est pas possible, couvrez-vous absolument, portez un chapeau et des lunettes de soleil et appliquez un écran solaire de très haute protection (indice + 50).").setPositiveButton("OK",null);
                                                AlertDialog alt = builder.create();

                                                alt.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
                                                alt.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                alt.show();
                                            }
                                        });
                                        tv.setText(""+uv);
                                        tv2.setText("Extrême");
                                        tv.setTextColor(Color.parseColor("#990000"));
                                        tv2.setTextColor(Color.parseColor("#990000"));
                                    }

                                }catch (Exception e){

                                    Toast.makeText(getApplicationContext(), "Probleme de conversion", Toast.LENGTH_LONG).show();

                                }
                            }

                            break;
                    }

                }
            };

    }else {

            Toast.makeText(getApplicationContext(), "Vous devez tout d'abord connecter via Bluetooth", Toast.LENGTH_LONG).show();

        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                   // Read from the InputStream.
                    bytes = mmInStream.read(mmBuffer);
                    String Dbt = new String(mmBuffer,0,bytes);
                    // Send the obtained bytes to the UI activity.

                    System.out.print(numBytes);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1,Dbt).sendToTarget();

/*
                    bytes += mmInStream.read(mmBuffer, bytes, mmBuffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if (mmBuffer[i] == "#".getBytes()[0]) {
                            Message readMsg = mHandler.obtainMessage(
                                    MESSAGE_READ, begin, i,
                                    mmBuffer);
                            readMsg.sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }*/
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String s) {
            byte [] bytes = s.getBytes();
                      try {
                mmOutStream.write(bytes);

            } catch (IOException e) {
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    public void AddData(String newEntry,String newEntry2 ) {
        boolean insertData = mDatabaseHelper.addData(newEntry,newEntry2);

        /*if (insertData) {
            Toast.makeText(getApplicationContext(), "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();

        }*/
    }


}
