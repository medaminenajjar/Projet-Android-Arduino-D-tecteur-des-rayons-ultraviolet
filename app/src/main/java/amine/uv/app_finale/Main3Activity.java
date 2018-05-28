package amine.uv.app_finale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Main3Activity extends AppCompatActivity {


    BluetoothAdapter monBluetooth = null;
    BluetoothDevice monDevice = null;
    BluetoothSocket monSocket = null;
    Button btn,btn2 ;

    UUID monUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int bluetooth_activee = 1;
    private static final int bluetooth_cnx = 2;
    MenuItem connect;
    boolean cnx = false;
    String macc = null;
    TextView tv ;
    TextView tv2 ;
    TextView tv3 ;
    Integer uv ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        connect = (MenuItem) findViewById(R.id.menu_connect);
        btn = (Button)findViewById(R.id.button);
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






    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater Mi = getMenuInflater();

        Mi.inflate(R.menu.mon_menu,menu);
        getMenuInflater().inflate(R.menu.mon_menu, menu);
        menu.findItem(R.id.menu_connect).setVisible(false);
        menu.findItem(R.id.menu_about).setVisible(false);




        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_about){

            Intent i = new Intent(this,Main2Activity.class);
            startActivity(i);

        }
        else if (item.getItemId() == R.id.menu_connect){


            /**
             * connect the bluetooth
             *
             *
             */




/*
            connect.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {*/
            if (cnx){


                try {
                    monSocket.close();
                    Toast.makeText(getApplicationContext(), "Bluetooth Déconnecté", Toast.LENGTH_LONG).show();
                    cnx = false;
                }catch (IOException ee){
                    Toast.makeText(getApplicationContext(), "erreur : " + ee, Toast.LENGTH_LONG).show();


                }


            }else {
                Intent liste = new Intent(Main3Activity.this, ListeBluetooth.class);
                startActivityForResult(liste,bluetooth_cnx);

            }

            // return false;
            //   }
            // });










        }





        return super.onOptionsItemSelected(item);
    }








    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case bluetooth_activee:
                if (resultCode == Activity.RESULT_OK) {

                    Toast.makeText(getApplicationContext(), "Bluetoooth Activé", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "nooo :/", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case bluetooth_cnx:
                if (resultCode == Activity.RESULT_OK){
                    macc = data.getExtras().getString(ListeBluetooth.mac);
                    Toast.makeText(getApplicationContext(), "Mac ok "+macc, Toast.LENGTH_LONG).show();
                    monDevice = monBluetooth.getRemoteDevice(macc);

                    try {
                        monSocket = monDevice.createInsecureRfcommSocketToServiceRecord(monUUID);
                        monSocket.connect();
                        //connect.setText("DECONNECT");
                        cnx = true;
                    }catch (IOException e){
                        Toast.makeText(getApplicationContext(), "erreurrr : " + e, Toast.LENGTH_LONG).show();
                        cnx = false;


                    }




                }else{
                    Toast.makeText(getApplicationContext(), "cannot connect :/", Toast.LENGTH_LONG).show();




                }




        }

    }


    public void changeUV (View view){

        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        //tv3 = (TextView) findViewById(R.id.textView8);
        btn = (Button)findViewById(R.id.button);
        btn.setVisibility(View.VISIBLE);

        btn2 = (Button)findViewById(R.id.button2);
        btn2.setVisibility(View.INVISIBLE);
        uv = 1;
        if (uv <= 2 && uv >= 1){

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main3Activity.this);
                    builder.setMessage("* Port de lunettes de soleil en cas de journées ensoleillées.").setPositiveButton("OK",null);
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
            /*tv3.setText("Port de lunettes de soleil en cas de journées ensoleillées.");*/

        }/*else if (uv <= 5 && uv >= 3){
            tv.setText(""+uv);
            tv2.setText("Modéré");
            tv2.setTextColor(Color.parseColor("#0000ff"));
            tv3.setText("   Couvrez-vous, portez un chapeau et des lunettes de soleil. Appliquez un écran solaire de protection moyenne (indice de 15 à 29) surtout si vous êtes à l’extérieur pendant plus de 30 minutes. Cherchez l’ombre quand le soleil est au méridien.");

        }else if (uv <= 7 && uv >= 6){
            tv.setText(""+uv);
            tv2.setText("Élevé");
            tv2.setTextColor(Color.parseColor("#0000ff"));
            tv3.setText("Réduisez l’exposition entre 12 h et 16 h. Appliquez un écran solaire de haute protection (indice de 30 à 50), portez un chapeau et des lunettes de soleil et placez-vous à l’ombre.");

        }else if (uv <= 10 && uv >= 8){
            tv.setText(""+uv);
            tv2.setText("Très élevé");
            tv2.setTextColor(Color.parseColor("#0000ff"));
            tv3.setText("Sans protection, la peau sera endommagée et peut brûler. Évitez l’exposition au soleil entre 12 h et 16 h. Recherchez l’ombre, couvrez-vous, portez un chapeau et des lunettes de soleil, et appliquez un écran solaire de très haute protection (indice + 50).");

        }else if (uv >= 11){
            tv.setText(""+uv);
            tv2.setText("Extrême");
            tv2.setTextColor(Color.parseColor("#0000ff"));
            tv3.setText("La peau non protégée sera endommagée et peut brûler en quelques minutes. Évitez toute exposition au Soleil et, si ce n’est pas possible, couvrez-vous absolument, portez un chapeau et des lunettes de soleil et appliquez un écran solaire de très haute protection (indice + 50).");

        }*/

    }






  /*  public void gotoSA(View v) {

        Intent i = new Intent(this,Main2Activity.class);
        startActivity(i);
    }
*/
}
