package nickita.gq.aca;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

public class MainActivity extends AppCompatActivity {
    private Physicaloid mPhysicaloid;
    private Button mSendButton;
    private Button mCloseButton;
    private Button mOpenButton;
    private Context mContext;
    private Spinner mBpSpinner;
    private EditText mMsgInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void setUpArduinoConnection(int rate) {
        mContext = this;
        mPhysicaloid = new Physicaloid(this);
        mPhysicaloid.setBaudrate(rate);
        if (mPhysicaloid.open()) {
            Toast.makeText(this, "Connected on " + rate, Toast.LENGTH_LONG).show();
            mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    //Toast.makeText(mContext, "Received data of size:"+size, Toast.LENGTH_LONG).show();
                    byte[] buf = new byte[size];
                    mPhysicaloid.read(buf, size);
                    mSendButton.setText(String.valueOf(size));
                }
            });
        } else {
            //Error while connecting
            Toast.makeText(this, "Cannot open " + rate, Toast.LENGTH_LONG).show();
        }
    }

    private void initialize() {
        mMsgInput = (EditText) findViewById(R.id.msg_input);
        mBpSpinner = (Spinner) findViewById(R.id.data_rate_spinner);
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mMsgInput.getText().toString();
                byte[] buf = msg.getBytes();
                if (mPhysicaloid != null) {
                    mPhysicaloid.write(buf, buf.length);
                    Toast.makeText(view.getContext(), "SENT: "+msg, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "PHYSICALOID == NULL", Toast.LENGTH_LONG).show();
                }
            }
        });
        mCloseButton = (Button) findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhysicaloid.close()) {
                    Toast.makeText(view.getContext(), "Connection closed", Toast.LENGTH_LONG).show();
                    mPhysicaloid.clearReadListener();    //clear read listener
                }
            }
        });
        mOpenButton = (Button) findViewById(R.id.open_button);
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rate = Integer.valueOf(String.valueOf(mBpSpinner.getSelectedItem()));
                setUpArduinoConnection(rate);
                Toast.makeText(view.getContext(), "Listening on " + rate, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Deprecated
    public byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }
}
