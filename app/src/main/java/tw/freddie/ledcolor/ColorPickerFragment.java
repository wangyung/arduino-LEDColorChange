package tw.freddie.ledcolor;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.freddie.ledcolor.bluetooth.BluetoothConnection;
import tw.freddie.ledcolor.commands.LEDColorCommand;
import tw.freddie.ledcolor.widget.HSVColorWheel;

public class ColorPickerFragment extends Fragment implements HSVColorWheel.OnColorSelectedListener {

    public static final String FRAGMENT_TAG = "ColorPickerFragment";
    private static final String TAG = FRAGMENT_TAG;
    private BluetoothConnection mBTConnection;
    private ViewGroup mRootView;

    public void setupBTConnection(BluetoothConnection connection) {
        mBTConnection = connection;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);

        HSVColorWheel hsvColorWheel = (HSVColorWheel) mRootView.findViewById(R.id.color_picker);
        hsvColorWheel.setListener(this);

        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBTConnection.connect();
    }

    @Override
    public void onDestroy() {
        mBTConnection.disconnect();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void colorSelected(Integer color) {
        int RGB = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
        Log.d(TAG, "RGB value = " + RGB);
        mBTConnection.addCommand(new LEDColorCommand(RGB));
    }

    public void init(LayoutInflater inflater, ViewGroup container) {
        if (mRootView == null) {
            mRootView = (ViewGroup)inflater.inflate(R.layout.fragment_color_picker, container, false);
        }
    }
}
