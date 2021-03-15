package edu.mtu.HIDE.pillowtalkmobile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private final static UUID PILLOWTALK_UUID = UUID.fromString("79bf39f7-54a4-4015-b27e-0b4be44b506d");
    private static ConnectedThread mConnectedThread;
    private static BluetoothSocket mmSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final Context mContext;
    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "bluetoothStateReceiver: bluetooth disabled");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "bluetoothStateReceiver: bluetooth enabled");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "bluetoothStateReceiver: disabling bluetooth");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "bluetoothStateReceiver: enabling bluetooth");
                        break;
                }
            }
        }
    };

    /**
     * BluetoothService constructor
     * @param c Context of the mainActivity
     */
    public BluetoothService(Context c) {
        mContext = c;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * connect to the device with the given name
     * @param name String
     */
    public void connect(String name) {
        Log.d(TAG, "connect: getting bonded devices");
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

        Log.d(TAG, "connect: searching for device with name \"" + name + "\"");
        for (BluetoothDevice bluetoothDevice : devices) {
            if (bluetoothDevice.getName().equals(name)) {
                Log.d(TAG, "connect: found device with address " + bluetoothDevice.getAddress() + " starting connection");
                ConnectThread mConnectThread = new ConnectThread(bluetoothDevice);
                mConnectThread.start();
                break;
            }
        }
    }

    /**
     * connects to the given bluetooth device
     * @param bluetoothDevice BluetoothDevice
     */
    public void connect(BluetoothDevice bluetoothDevice) {
        ConnectThread mConnectThread = new ConnectThread(bluetoothDevice);
        mConnectThread.start();
    }

    /**
     * Send message to connect device
     * @param input the message to be sent
     */
    public void write(String input) {
        if (mConnectedThread != null) {
            byte[] bytes = input.getBytes();
            mConnectedThread.write(bytes);
        }
    }

    /**
     * returns true if device supports bluetooth
     * @return boolean
     */
    public boolean supportsBluetooth() {
        return bluetoothAdapter != null;
    }

    /**
     * enables bluetooth
     */
    public void enableBluetooth() {
        if (!supportsBluetooth()) {
            Log.d(TAG, "enableBluetooth: device doesn't support bluetooth");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "enableBluetooth: requesting bluetooth on");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableBtIntent);

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                mContext.registerReceiver(bluetoothStateReceiver, BTIntent);
            }
        }
    }

    /**
     * disconnects from other device
     */
    public void disconnect() {
        Log.d(TAG, "disconnect: closing ConnectedThread");
        mConnectedThread.cancel();
    }

    /**
     * attempts to make a connection with other device. if it fails the thread closes
     */
    private static class ConnectThread extends Thread {
        public ConnectThread(BluetoothDevice bluetoothDevice) {
            BluetoothSocket tmp = null;
            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(PILLOWTALK_UUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: connection failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d(TAG, "ConnectThread: Device connected");

            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                    Log.e(TAG, "ConnectThread: Cannot connect to device");
                } catch (IOException closeException) {
                    Log.e(TAG, "ConnectThread: Could not close the client socket", closeException);
                }
                return;
            }
            Log.d(TAG, "ConnectThread: Starting.");
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not close the client socket", e);
            }
        }
    }

    /**
     * Maintains the connection with other device. sends and receives data
     */
    private static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "run: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}