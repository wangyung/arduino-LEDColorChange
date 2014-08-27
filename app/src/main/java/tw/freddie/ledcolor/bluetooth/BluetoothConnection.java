package tw.freddie.ledcolor.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import tw.freddie.ledcolor.commands.*;

public class BluetoothConnection {

    private static final String TAG = "BluetoothConnection";

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private Thread mCommunicationThread;
    private ConnectionStatus mConnectionStatus;
    private BlockingQueue<Command> mCommandQueue;

    public enum ConnectionStatus {
        Disconnected,
        Connecting,
        Connected
    }

    public BluetoothConnection(BluetoothDevice device) throws IOException {
        mDevice = device;
        mSocket = mDevice.createRfcommSocketToServiceRecord(BluetoothManager.uuid);
        mConnectionStatus = ConnectionStatus.Disconnected;
        mCommandQueue = new ArrayBlockingQueue<Command>(50);
    }

    public void connect() {
        mCommunicationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //TODO: only support send data now
                    mConnectionStatus = ConnectionStatus.Connecting;
                    mSocket.connect();
                    mConnectionStatus = ConnectionStatus.Connected;
                    OutputStream outputStream = mSocket.getOutputStream();
                    while (mSocket.isConnected()) {
                        try {
                            Command command = mCommandQueue.take();
                            outputStream.write(command.getRawData());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    mConnectionStatus = ConnectionStatus.Disconnected;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mCommunicationThread.start();
    }

    public void addCommand(Command command) {
        if (mCommandQueue.remainingCapacity() == 0) {
            Log.w(TAG, "command queue is full");
        } else {
            mCommandQueue.add(command);
        }
    }

    public void disconnect() {
        try {
            Command command = new LEDColorCommand(0);
            mSocket.getOutputStream().write(command.getRawData());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConnectionStatus getConnectionStatus() {
        return mConnectionStatus;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }
}
