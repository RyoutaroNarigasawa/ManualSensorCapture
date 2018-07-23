package com.research.pengin.manualsensorcapture;

/**
 * Created by ryoutaro on 2017/01/17.
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteCSV {
    String filename;

    public WriteCSV(String filename){
        this.filename = filename;
    }

    public int csv(ArrayList<Float>Accel_X,ArrayList<Float> Accel_Y,ArrayList<Float> Accel_Z,ArrayList<Float> Gyro_X,ArrayList<Float> Gyro_Y,ArrayList<Float> Gyro_Z){
        try{
            File csv = new File(filename+".csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));

            for(int i = 0; i < Gyro_Z.size(); i++){
                //センサデータ記録
                if(Accel_X.get(i) != null && Accel_Y.get(i) != null && Accel_Z.get(i) != null && Gyro_X.get(i) != null && Gyro_Y.get(i) != null &&Gyro_Z.get(i) != null) {
                    bw.write(Accel_X.get(i) + "," + Accel_Y.get(i) + "," + Accel_Z.get(i) + "," + Gyro_X.get(i) + "," + Gyro_Y.get(i) + "," + Gyro_Z.get(i));
                    System.out.println(Accel_Y.get(i) + "," + Accel_Z.get(i) + "," + Gyro_X.get(i) + "," + Gyro_Y.get(i) + "," + Gyro_Z.get(i));
                    bw.newLine();
                }
            }
            bw.newLine();
            bw.close();

        }catch (FileNotFoundException e){
            //Fileオブジェクト生成時の例外補足
            e.printStackTrace();
            return -1;
        }catch (IOException e){
            //BufferedWritetterオブジェクトのクローズ時の例外補足
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
    public void Time(long time){
        try{
            File times = new File(filename+"-time.csv");
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(times, true));
            bw1.write(String.valueOf(time));
            bw1.newLine();
            bw1.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
