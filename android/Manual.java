import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Manual extends AppCompatActivity {
    Handler handler;
    private BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothThread bluetoothThread;
    Utils utils;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:06:66:7D:80:1A");
        utils = new Utils(this);

        try {
            socket = connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect();
            utils.makeToast("Connected to device" + device.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        bluetoothThread = new BluetoothThread(socket,handler);
        bluetoothThread.start();

        Button a = findViewById(R.id.upLeft);
        a.setOnTouchListener(btnOnClick("upLeft"));
        Button b = findViewById(R.id.up);
        b.setOnTouchListener(btnOnClick("up"));
        Button c = findViewById(R.id.upRight);
        c.setOnTouchListener(btnOnClick("upRight"));
        Button d = findViewById(R.id.left);
        d.setOnTouchListener(btnOnClick("left"));
        Button e = findViewById(R.id.tap);
        e.setOnTouchListener(btnOnClick("tap"));
        Button f = findViewById(R.id.right);
        f.setOnTouchListener(btnOnClick("right"));
        Button g = findViewById(R.id.downLeft);
        g.setOnTouchListener(btnOnClick("downLeft"));
        Button h = findViewById(R.id.down);
        h.setOnTouchListener(btnOnClick("down"));
        Button i = findViewById(R.id.downRight);
        i.setOnTouchListener(btnOnClick("downRight"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BluetoothSocket connect(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public View.OnTouchListener btnOnClick(final String cmd){
        View.OnTouchListener ocl = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bluetoothThread.write(cmd + "/");
                        break;
                    case MotionEvent.ACTION_UP:
                        bluetoothThread.write("stop/");
                        break;
                }
                return false;
            }

        };
        return  ocl;
    }
}
