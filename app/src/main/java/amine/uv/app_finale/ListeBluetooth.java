package amine.uv.app_finale;

/**
 * Created by amine on 12/07/2017.
 */
        import android.app.Activity;
        import android.app.ListActivity;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.Set;



public class ListeBluetooth extends ListActivity {

    private BluetoothAdapter monBluetooth2 = null;

    static String mac = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ArrayAdapter<String> arrayBluetooth  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        monBluetooth2 = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devicee = monBluetooth2.getBondedDevices();
        if (devicee.size()>0 ){
            for (BluetoothDevice dis : devicee){
                String nomBt = dis.getName();
                String macBt = dis.getAddress();
                arrayBluetooth.add(nomBt +"\n" + macBt);


            }


        }
        setListAdapter(arrayBluetooth);

    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onListItemClick(l,v,position,id);

        String info = ((TextView) v).getText().toString();
        //Toast.makeText(getApplicationContext(),"Info : "+info, Toast.LENGTH_LONG).show();

        String addmac = info.substring(info.length() - 17);
        //Toast.makeText(getApplicationContext(),"add mac : "+addmac, Toast.LENGTH_LONG).show();
        Intent reternMac = new Intent();
        reternMac.putExtra(mac, addmac);
        setResult(RESULT_OK,reternMac);
        finish();

    }
}

