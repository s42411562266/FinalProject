package com.example.xiscan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private FileOutputStream file;
    private FileInputStream fileIn;
    private String data="";
    private StringBuffer sb;
    private TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Document data = Jsoup.parse(is, "UTF-8", "");
        textView1= (TextView) findViewById(R.id.text);
        try{
            file=this.openFileOutput("database.txt", Context.MODE_APPEND);
        }
        catch(FileNotFoundException e){
            Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
        }
        try{
            file.write("".getBytes());
        }
        catch(IOException e){
            Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
        }
        try{
            FileInputStream input=openFileInput("database.txt");
            byte []bytes=new byte[1024];
            sb=new StringBuffer();
            while(input.read(bytes)!=-1){
                sb.append(new String(bytes));
                bytes=new byte[1024];
            }
        }
        catch(Exception e){
            Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
        }
    }

    public void onButton1Click(View v) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        if(getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size()==0) {
            Toast.makeText(this, "請至 Play 商店安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show();
        }
        else {
            intent.putExtra("SCAN_MODE", "SCAN_MODE");
            startActivityForResult(intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode==1) {
            if(resultCode==RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT").toString();
                String num=contents.substring(0,10),date=contents.substring(10,17);
                if(!checkVaild(num,date)){
                    Toast.makeText(this, "掃描條碼非發票序號資訊，請重新掃描!", Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    file.write((date+num+"\n").getBytes());//無空格要重新讀取計算
                    //file.close();
                    Toast.makeText(this, "發票資料儲存成功!", Toast.LENGTH_LONG).show();
                }
                catch(IOException e){
                    Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
                }

                textView1.setText(num+"\n"+date);
            }
            else if(resultCode==RESULT_CANCELED) {
                Toast.makeText(this, "取消掃描", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showData(View v){
        try{
            FileInputStream input=openFileInput("database.txt");
            byte []bytes=new byte[1024];
            sb=new StringBuffer();
            while(input.read(bytes)!=-1){
                sb.append(new String(bytes));
                bytes=new byte[1024];
            }
        }
        catch(Exception e){
            Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
        }
        textView1.setText(sb);
    }

    public void checkAward(View v){

    }

    public void clearData(View v){
        textView1.setText("");
        try{
            file=this.openFileOutput("database.txt", Context.MODE_PRIVATE);
            file.write("".getBytes());
            file=this.openFileOutput("database.txt", Context.MODE_APPEND);
        }
        catch(Exception e){
            Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy(){
        try{
            file.close();
        }
        catch(IOException e){
            Toast.makeText(this, "關閉檔案失敗", Toast.LENGTH_LONG).show();
        }
        super.onDestroy();
    }

    private boolean checkVaild(String num,String date){
        if((num.charAt(0)<65||num.charAt(0)>90)||(num.charAt(1)<65||num.charAt(1)>90))
            return false;
        for(int i=2;i<num.length();i++){
            if(!Character.isDigit(num.charAt(i)))
                return false;
        }
        for(int i=0;i<date.length();i++){
            if(!Character.isDigit(date.charAt(i)))
                return false;
        }
        return true;
    }
}