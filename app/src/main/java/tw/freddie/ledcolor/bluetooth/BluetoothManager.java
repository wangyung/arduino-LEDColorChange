package tw.freddie.ledcolor.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";

    public static final int ENABLE_BLUETOOTH = 1;

    private BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public boolean hasBluetooth() {
        return mBTAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return mBTAdapter != null && mBTAdapter.isEnabled();
    }

    public boolean doBTAction(Activity activity, int action) {
        Intent intent = null;
        switch (action) {
            case ENABLE_BLUETOOTH:
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                break;
            default:
                break;
        }

        if (intent == null) {
            return false;
        } else {
            activity.startActivityForResult(intent, action);
            return true;
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return mBTAdapter.getBondedDevices();
    }
}
