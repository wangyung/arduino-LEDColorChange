package tw.freddie.ledcolor.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BTDeviceAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mBTDevices;
    private TextView mDeviceName;
    private Context mContext;

    public BTDeviceAdapter(Context context, ArrayList<BluetoothDevice> bTDevices) {
        mBTDevices = bTDevices;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mBTDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mBTDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        mDeviceName = (TextView)convertView.findViewById(android.R.id.text1);
        mDeviceName.setText(mBTDevices.get(i).getName());

        return convertView;
    }
}
