package com.example.voicecontrolforwheelchair;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class BlankFragment extends Fragment {
    Button f,r,l,b,s,dis;
    ListView listView;
    ImageView mic;
    BluetoothAdapter adapter;
    private Set<BluetoothDevice> paireddevices;
    private String address;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private ProgressDialog progress;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_blank,container,false);
        f=(Button)view.findViewById(R.id.forward);
        r=(Button)view.findViewById(R.id.right);
        l=(Button)view.findViewById(R.id.left);
        b=(Button)view.findViewById(R.id.backward);
        s=(Button)view.findViewById(R.id.stop);
        dis=(Button)view.findViewById(R.id.discover);
        mic=(ImageView)view.findViewById(R.id.mic);
        listView=(ListView)view.findViewById(R.id.listview);
        f.setEnabled(false);
        r.setEnabled(false);
        l.setEnabled(false);
        b.setEnabled(false);
        s.setEnabled(false);
        mic.setEnabled(false);
        adapter=BluetoothAdapter.getDefaultAdapter();
        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPairedDevices();
            }
        });
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward();
            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right();
            }
        });
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backward();
            }
        });
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceInput();
            }
        });
        return view;
    }
    public void ShowPairedDevices()
    {
        paireddevices=adapter.getBondedDevices();
        ArrayList list = new ArrayList();
        if (paireddevices.size()>0)
        {
            for(BluetoothDevice bt : paireddevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(getContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter=new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(myclicklistner);
    }


    private AdapterView.OnItemClickListener myclicklistner= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            new  connectbt().execute();

        }
    };


    private class connectbt extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(getActivity(), "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    adapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = adapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(getActivity().getApplicationContext(),"Connection Failed Please Try again later",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Successfully Connected ", Toast.LENGTH_SHORT).show();
                f.setEnabled(true);
                r.setEnabled(true);
                l.setEnabled(true);
                b.setEnabled(true);
                s.setEnabled(true);
                mic.setEnabled(true);
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }


    public void toast(String message)
    {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void forward()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("front".toString().getBytes());
            }
            catch (IOException e)
            {
                toast("Error");
            }
        }
    }

    private void left()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("left".toString().getBytes());
            }
            catch (IOException e)
            {
                toast("Error");
            }
        }
    }

    private void right()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("right".toString().getBytes());
            }
            catch (IOException e)
            {
                toast("Error");
            }
        }
    }
    private void backward()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("back".toString().getBytes());
            }
            catch (IOException e)
            {
                toast("Error");
            }
        }
    }
    private void stop()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("stop".toString().getBytes());
            }
            catch (IOException e)
            {
                toast("Error");
            }
        }
    }


    public void VoiceInput()
    {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak Command for Robot");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode  == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (btSocket!=null)
                    {
                        try
                        {
                            btSocket.getOutputStream().write(result.get(0).toString().getBytes());
                        }
                        catch (IOException e)
                        {
                            toast("Error");
                        }
                    }


                }
                break;
            }
        }
    }
}
