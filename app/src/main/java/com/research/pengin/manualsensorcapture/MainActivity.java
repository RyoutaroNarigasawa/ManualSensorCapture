package com.research.pengin.manualsensorcapture;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textView, textInfo;
    private EditText tester;
    private Button start, stop;
    private long time1 = 0,time2 = 0;

    //フラグ
    int state = 0;//記録状態
    int savestate_sensor = -1; //ファイル記録結果
    //SDカードのマウント先記録
    String sdCardState = Environment.getExternalStorageState();

    View decor;

    //センサデータ記録用可変長配列
    ArrayList<Float> Accel_X = new ArrayList<Float>(200);
    ArrayList<Float> Accel_Y = new ArrayList<Float>(200);
    ArrayList<Float> Accel_Z = new ArrayList<Float>(200);
    ArrayList<Float> Gyro_X  = new ArrayList<Float>(200);
    ArrayList<Float> Gyro_Y  = new ArrayList<Float>(200);
    ArrayList<Float> Gyro_Z  = new ArrayList<Float>(200);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        decor = this.getWindow().getDecorView();

        start  = (Button)findViewById(R.id.start);
        stop   = (Button)findViewById(R.id.stop);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        textInfo = (TextView) findViewById(R.id.text_info);

        // Get an instance of the TextView
        textView = (TextView) findViewById(R.id.text_view);

        //input tester name of the EditText
        tester = (EditText)findViewById(R.id.tester);

        start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                time1 = System.nanoTime();
                if (state == 0) {
                    state = 1;
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    start.setFocusable(false);
                    tester.setEnabled(false);
                    Toast.makeText(MainActivity.this, "記録を開始します", Toast.LENGTH_SHORT).show();
                } else if (state == 1) {
                    Toast.makeText(MainActivity.this, "すでに記録は開始されています", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {//記録終了処理
            @Override
            public void onClick(View v) {
                time2 = System.nanoTime();
                if (sdCardState.equals(Environment.MEDIA_MOUNTED)) {
                    if(state == 1){
                        state = 0;
                        start.setClickable(true);
                        start.setFocusableInTouchMode(true);
                        tester.setFocusable(true);
                        tester.setEnabled(true);
                        WriteCSV wcsv = new WriteCSV(getExternalFilesDirs(null)[1]+"/"+tester.getText().toString());
                        savestate_sensor = wcsv.csv(Accel_X,Accel_Y,Accel_Z,Gyro_X,Gyro_Y,Gyro_Z);
                        wcsv.Time(time2-time1);
                        if(savestate_sensor == 0){
                            Toast.makeText(MainActivity.this,"記録が正常に行われました",Toast.LENGTH_SHORT).show();
                        }else if(savestate_sensor == 1){
                            Toast.makeText(MainActivity.this,"ファイルが正常に作成されませんでした(error code 1)",Toast.LENGTH_SHORT).show();
                        }else if(savestate_sensor == -1){
                            Toast.makeText(MainActivity.this,"クローズ処理が正常におこなわれませんでした(error code -1)",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"想定外の異常が発生しました",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (sdCardState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    Toast.makeText(MainActivity.this, "このSDカードは読み取り専用です", Toast.LENGTH_SHORT).show();
                } else if (sdCardState.equals(Environment.MEDIA_REMOVED)) {
                    Toast.makeText(MainActivity.this, "SDカードが挿入されていません", Toast.LENGTH_SHORT).show();
                } else if (sdCardState.equals(Environment.MEDIA_UNMOUNTED)) {
                    Toast.makeText(MainActivity.this, "SDカードがマウントされていません", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "再度SDカードを確認してください", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        Sensor accel = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        Sensor gyro = sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this,accel,20000);
        sensorManager.registerListener(this,gyro,20000);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
    }

    // 解除するコードも入れる!
    @Override
    protected void onPause() {
        super.onPause();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float AsensorX = 0.0f, AsensorY = 0.0f, AsensorZ = 0.0f;
        float GsensorX = 0.0f, GsensorY = 0.0f, GsensorZ = 0.0f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            AsensorX = event.values[0];
            AsensorY = event.values[1];
            AsensorZ = event.values[2];

            String strTmp = "加速度センサー\n"
                    + " X: " + AsensorX + "\n"
                    + " Y: " + AsensorY + "\n"
                    + " Z: " + AsensorZ;
            textView.setText(strTmp);
            if(state == 1) {
                Accel_X.add(AsensorX);
                Accel_Y.add(AsensorY);
                Accel_Z.add(AsensorZ);
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            GsensorX = event.values[0];
            GsensorY = event.values[1];
            GsensorZ = event.values[2];
            String strTmp = "センサー\n"
                    + " X: " + GsensorX + "\n"
                    + " Y: " + GsensorY + "\n"
                    + " Z: " + GsensorZ;
            textView.setText(strTmp);
            if(state == 1) {
                Gyro_X.add(GsensorX);
                Gyro_Y.add(GsensorY);
                Gyro_Z.add(GsensorZ);
            }
        }
            showInfo(event);
        }

    // （お好みで）加速度センサーの各種情報を表示
    private void showInfo(SensorEvent event){
        // センサー名
        StringBuffer info = new StringBuffer("Name: ");
        info.append(event.sensor.getName());
        info.append("\n");

        // ベンダー名
        info.append("Vendor: ");
        info.append(event.sensor.getVendor());
        info.append("\n");

        // 型番
        info.append("Type: ");
        info.append(event.sensor.getType());
        info.append("\n");

        // 最小遅れ
        int data = event.sensor.getMinDelay();
        info.append("Mindelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // 最大遅れ
        data = event.sensor.getMaxDelay();
        info.append("Maxdelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // レポートモード
        data = event.sensor.getReportingMode();
        String stinfo = "unknown";
        if(data == 0){
            stinfo = "REPORTING_MODE_CONTINUOUS";
        }else if(data == 1){
            stinfo = "REPORTING_MODE_ON_CHANGE";
        }else if(data == 2){
            stinfo = "REPORTING_MODE_ONE_SHOT";
        }
        info.append("ReportingMode: ");
        info.append(stinfo);
        info.append("\n");

        // 最大レンジ
        info.append("MaxRange: ");
        float fData = event.sensor.getMaximumRange();
        info.append(String.valueOf(fData));
        info.append("\n");

        // 分解能
        info.append("Resolution: ");
        fData = event.sensor.getResolution();
        info.append(String.valueOf(fData));
        info.append(" m/s^2\n");

        // 消費電流
        info.append("Power: ");
        fData = event.sensor.getPower();
        info.append(String.valueOf(fData));
        info.append(" mA\n");

        textInfo.setText(info);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
