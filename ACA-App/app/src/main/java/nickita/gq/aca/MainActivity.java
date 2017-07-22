package nickita.gq.aca;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Physicaloid mPhysicaloid;
    private Button mSendButton;
    private Button mCloseButton;
    private Button mOpenButton;
    private Context mContext;
    private SeekBar mSeekBar;
    private TextView mMotorValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        setUpArduinoConnection();
    }

    private void setUpArduinoConnection(){
        mContext = this;
        mPhysicaloid  = new Physicaloid(this);
        mPhysicaloid.setBaudrate(9600);
        if(mPhysicaloid.open()) {
            Toast.makeText(this, "Connection established :P", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Cannot open", Toast.LENGTH_LONG).show();
        }
    }

    private void initialize(){
        mMotorValue = (TextView)findViewById(R.id.motor_value);
        mSeekBar = (SeekBar) findViewById(R.id.motor_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                byte[] buf = String.valueOf(i).getBytes();
                mPhysicaloid.write(buf, buf.length);
                mMotorValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mSendButton = (Button)findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] buf = "HELLO".getBytes();
                mPhysicaloid.write(buf, buf.length);
            }
        });
        mCloseButton = (Button)findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPhysicaloid.close()) {
                    Toast.makeText(view.getContext(), "Connection closed", Toast.LENGTH_LONG).show();
                    mPhysicaloid.clearReadListener();	//clear read listener
                }
            }
        });
        mOpenButton = (Button)findViewById(R.id.open_button);
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpArduinoConnection();
            }
        });

    }
}