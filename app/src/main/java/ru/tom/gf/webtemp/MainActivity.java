package ru.tom.gf.webtemp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TCPClient client;
    Thread getP;
    static String answer = "T:0:H:0:#";
    String buffer = "T:0:H:0:#";
    Button btnRefresh;
    Button btnSet;
    TextView tv;
    TextView temp;
    TextView hum;
    EditText ip;
    EditText port;
    static boolean giveme = true;

    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences("Option", Context.MODE_PRIVATE);

        btnRefresh = (Button)findViewById(R.id.button);
        btnSet = (Button)findViewById(R.id.button2);
        ip = (EditText)findViewById(R.id.editText_ip);
        port = (EditText)findViewById(R.id.editText_port);
        tv = (TextView)findViewById(R.id.textView);
        temp = (TextView)findViewById(R.id.textView_temp);
        hum = (TextView)findViewById(R.id.textView3_hum);

        ip.setText(mSettings.getString("IP", "127.0.0.1"));
        port.setText(String.valueOf(mSettings.getInt("PORT", 2000)));

        getP = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(giveme && client != null){
                        client.Connect();
                        client.Send("GET");
                        buffer = client.Read();
                        client.Disconnect();
                        giveme = false;
                    }
                }
            }
        });
        getP.start();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new TCPClient(mSettings.getString("IP", "127.0.0.1"), mSettings.getInt("PORT", 2000));
                giveme = true;
                String[] bu = buffer.split(":");
                tv.setText(buffer);
                if (check_p(bu)) {
                    temp.setText("Температура: " + bu[1]);
                    hum.setText("Влажность: " + bu[3]);
                } else {
                    tv.setText("ПНПП");
                }
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("IP", ip.getText().toString());
                editor.putInt("PORT", Integer.parseInt(port.getText().toString()));
                editor.apply();
            }
        });
    }

    private boolean check_p(String[] chk){
        if(chk.length == 5){
            if(chk[0].equals("T")){
                if(chk[2].equals("H")){
                    if(chk[4].equals("#")){
                        answer = buffer;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
