package tw.freddie.ledcolor;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import tw.freddie.ledcolor.adapter.BTDeviceAdapter;

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String FRAGMENT_TAG = "MainFragment";
    private ListView mDeviceList;
    private BTDeviceAdapter mAdapter;

    private AdapterView.OnItemClickListener mItemClickDelegate;

    public void setBTDeviceAdapter(BTDeviceAdapter adapter) {
        mAdapter = adapter;
        mDeviceList.setAdapter(adapter);
    }

    public BTDeviceAdapter getAdapter() {
        return (BTDeviceAdapter)mDeviceList.getAdapter();
    }

    public void setItemClickDelegate(AdapterView.OnItemClickListener delegate) {
        mItemClickDelegate = delegate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mDeviceList = (ListView) rootView.findViewById(R.id.device_list);
        mDeviceList.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mDeviceList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mItemClickDelegate != null) {
            mItemClickDelegate.onItemClick(adapterView, view, position, id);
        }
    }
}
