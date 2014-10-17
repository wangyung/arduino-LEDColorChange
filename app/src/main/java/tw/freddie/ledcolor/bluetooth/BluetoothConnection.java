package tw.freddie.ledcolor.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import tw.freddie.ledcolor.bluetooth.le.BluetoothLeService;
import tw.freddie.ledcolor.commands.*;

public class BluetoothConnection {

    private static final String TAG = "BluetoothConnection";

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothLeService mBLEService;
    private Thread mCommunicationThread;
    private ConnectionStatus mConnectionStatus;
    private BlockingQueue<Command> mCommandQueue;

    public enum ConnectionStatus {
        Disconnected,
        Connecting,
        Connected
    }

    public BluetoothConnection(BluetoothDevice device, BluetoothLeService bleService) throws IOException {
        mDevice = device;
        mSocket = mDevice.createRfcommSocketToServiceRecord(BluetoothManager.uuid);
        mConnectionStatus = ConnectionStatus.Disconnected;
        mCommandQueue = new ArrayBlockingQueue<Command>(50);
        mBLEService = bleService;
    }

    public void connect() {
        mCommunicationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mConnectionStatus = ConnectionStatus.Connecting;
                mBLEService.connect(mDevice.getAddress());
                mConnectionStatus = ConnectionStatus.Connected;

                while (mConnectionStatus != ConnectionStatus.Disconnected) {
                    try {
                        Command command = mCommandQueue.take();
                        BluetoothGattCharacteristic tx = mBLEService.getCharacteristicTX();
                        if (tx != null) {
                            tx.setValue(command.getRawData());
                            mBLEService.writeCharacteristic(tx);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mCommunicationThread.start();
    }

    public void addCommand(Command command) {
        if (mCommandQueue.remainingCapacity() == 0) {
            Log.w(TAG, "command queue is full");
            mCommandQueue.clear();
        } else {
            mCommandQueue.add(command);
        }
    }

    public void disconnect() {
        if (mSocket != null) {
            try {
                Command command = new LEDColorCommand(0);
                mSocket.getOutputStream().write(command.getRawData());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mConnectionStatus = ConnectionStatus.Disconnected;
    }

    public ConnectionStatus getConnectionStatus() {
        return mConnectionStatus;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }
}
