package tw.freddie.ledcolor;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import tw.freddie.ledcolor.adapter.BTDeviceAdapter;
import tw.freddie.ledcolor.bluetooth.*;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainFragment mFragment;
    private ColorPickerFragment mColorPickerFragment;
    private BluetoothManager mBTManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mColorPickerFragment = new ColorPickerFragment();
        mFragment = new MainFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment, MainFragment.FRAGMENT_TAG)
                    .commit();
        }

        mFragment.setItemClickDelegate(mItemClickListener);
        mBTManager = new BluetoothManager();
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            BluetoothDevice device = (BluetoothDevice)mFragment.getAdapter().getItem(position);
            try {
                showColorPickerFragment(new BluetoothConnection(device));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, getString(R.string.cannot_connect_device),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showColorPickerFragment(BluetoothConnection connection) {
        mColorPickerFragment.setupBTConnection(connection);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.color_fragment_slide_in, R.animator.device_fragment_slide_out,
                R.animator.device_fragment_slide_in, R.animator.color_fragment_slide_out)
                .replace(R.id.container, mColorPickerFragment, mColorPickerFragment.FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFragment = (MainFragment)getFragmentManager().findFragmentByTag(MainFragment.FRAGMENT_TAG);
        if (mBTManager.hasBluetooth()) {
            initBluetooth();
        } else {
            Log.e(TAG, "This device doesn't have Bluetooth support");
        }
    }

    private void initBluetooth() {
        if (!mBTManager.isBluetoothEnabled()) {
            boolean canEnabled = mBTManager.doBTAction(this, BluetoothManager.ENABLE_BLUETOOTH);
        } else {
            findBTDevices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothManager.ENABLE_BLUETOOTH:
                if (requestCode == RESULT_OK) {
                    findBTDevices();
                }
                break;
            default:
                break;
        }
    }

    private void findBTDevices() {
        Set<BluetoothDevice> btDevices = mBTManager.getBondedDevices();
        ArrayList<BluetoothDevice> deviceArray =
                new ArrayList<BluetoothDevice>(btDevices);

        mFragment.setBTDeviceAdapter(new BTDeviceAdapter(this, deviceArray));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
