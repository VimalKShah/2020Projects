package com.borqs.clientaidl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.borqs.serveraidl.IAdd;
import com.borqs.serveraidl.Person;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected IAdd mIAddService;
    private EditText mNumber1, mNumber2;
    private Button mAddition, mPlaceCall, mPrintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNumber1 = (EditText) findViewById(R.id.number1);
        mNumber2 = (EditText) findViewById(R.id.number2);
        mAddition = (Button) findViewById(R.id.Addition);
        mPlaceCall = (Button) findViewById(R.id.place_call);
        mPrintText = (Button) findViewById(R.id.printText);
        mAddition.setOnClickListener(this);
        mPlaceCall.setOnClickListener(this);
        mPrintText.setOnClickListener(this);
        initConnection();
    }

    private ServiceConnection mAidlConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("vimal", "componentName : " + componentName);
            mIAddService = IAdd.Stub.asInterface(iBinder);
            Log.d("vimal", "mIAddService : " + mIAddService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("vimal", "componentName : " + componentName);
            mIAddService = null;
        }
    };

    private void initConnection() {
        Log.d("vimal", "initConnection mIAddService : " + mIAddService);
        if(mIAddService == null) {
            Intent intent = new Intent(IAdd.class.getName());
            intent.setAction("service.calc");
            intent.setPackage("com.borqs.serveraidl");
            bindService(intent, mAidlConnection, Service.BIND_AUTO_CREATE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.Addition) {
            if(mIAddService != null) {
                try {
                    int output = mIAddService.add(Integer.parseInt(mNumber1.getText().toString()),Integer.parseInt(mNumber2.getText().toString()));
                    Log.d("vimal", "output : " + output);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if(view.getId() == R.id.place_call) {
            try {
                mIAddService.placeCall("9021592596");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if(view.getId() == R.id.printText) {
            TextView textView = findViewById(R.id.text);
            try {
                List<String> list = mIAddService.getStringList();
                textView.setText("\n" + "Custom Object Data");
                for (int i = 0; i < list.size(); i++) {
                    textView.append("\n" + list.get(i));
                }

                List<Person> personList = mIAddService.getPersonList();
                textView.append("\n" + "Custom Object Data");
                for (int i = 0; i < personList.size(); i++) {
                    Person person = personList.get(i);
                    textView.append(
                            "\n" + "Person Data: " + "Name:" + person.name + " Age:" + person.age);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d("vimal", "Connection cannot be establish");
            }
        }
    }
}
