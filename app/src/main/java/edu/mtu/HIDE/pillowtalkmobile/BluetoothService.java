package edu.mtu.HIDE.pillowtalkmobile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
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
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private static final String TAG = "BluetoothService";
    private final static UUID PILLOWTALK_UUID = UUID.fromString("79bf39f7-54a4-4015-b27e-0b4be44b506d");
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
    private ConnectedThread mConnectedThread;
    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;
    private int mState;

    /**
     * BluetoothService constructor
     * @param c Context of the mainActivity
     */
    public BluetoothService(Context c) {
        mContext = c;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start AcceptThread to begin session in listening (server) mode.
     */
    public synchronized void listen() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(true);
            mAcceptThread.start();
        }
    }

    /**
     * connect to the device with the given name
     * @param name String
     */
    public BluetoothDevice findDevice(String name) {
        Log.d(TAG, "findDevice: getting bonded devices");
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

        Log.d(TAG, "findDevice: searching for device with name \"" + name + "\"");
        for (BluetoothDevice bluetoothDevice : devices) {
            if (bluetoothDevice.getName().equals(name)) {
                Log.d(TAG, "findDevice: found device with address " + bluetoothDevice.getAddress());
                return bluetoothDevice;
            }
        }
        Log.d(TAG, "findDevice: no device with name \"" + name + "\" found");
        return null;
    }

    /**
     * connects to the given bluetooth device
     * @param bluetoothDevice BluetoothDevice
     */
    public synchronized void connect(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            // Cancel any thread attempting to make a connection
            if (mState == STATE_CONNECTING) {
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                    mConnectThread = null;
                }
            }

            // Cancel any thread currently running a connection
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }


            ConnectThread mConnectThread = new ConnectThread(bluetoothDevice);
            mConnectThread.start();
        } else {
            Log.e(TAG, "bluetoothDevice is null");
        }
    }


    /**
     * Send message to connect device
     *
     * @param input the message to be sent
     */
    public void write(String input) {
        byte[] bytes = input.getBytes();
        ConnectedThread r;
        synchronized (this) {
            if (mConnectedThread == null) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(bytes);

    }

    /**
     * returns true if device supports bluetooth
     *
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
    public void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mState = STATE_NONE;
    }

    public synchronized void connected(BluetoothSocket socket) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        Log.d(TAG, "connected: Starting.");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("PILLOWTALKMOBAL",
                        PILLOWTALK_UUID);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: listen() failed", e);
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                    if (socket != null) {
                        synchronized (BluetoothService.this) {
                            switch (mState) {
                                case STATE_LISTEN:
                                    // Situation normal. Start the connected thread.
                                    Log.d(TAG,"state: STATE_LISTEN ");
                                    connected(socket);
                                    break;
                                case STATE_CONNECTING:
                                    Log.d(TAG,"state: STATE_CONNECTING ");
                                    break;
                                case STATE_NONE:
                                    Log.d(TAG,"state: STATE_NONE ");
                                    break;
                                case STATE_CONNECTED:
                                    // Either not ready or already connected. Terminate new socket.
                                    Log.d(TAG,"state: STATE_CONNECTED ");
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        Log.e(TAG, "Could not close unwanted socket", e);
                                    }
                                    break;
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "AcceptThread: accept() failed", e);
                }
            }
        }

        public void cancel() {
            Log.d(TAG, "AcceptThread: cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: close() of server failed", e);
            }
        }
    }

    /**
     * attempts to make a connection with other device. if it fails the thread closes
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            BluetoothSocket tmp = null;
            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(PILLOWTALK_UUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: connection failed", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:");
            // Always cancel discovery because it will slow down a connection
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
                mState = STATE_NONE;
                return;
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket);
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
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    mState = STATE_NONE;
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            String text = new String(buffer, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}