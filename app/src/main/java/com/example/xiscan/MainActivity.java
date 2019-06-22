package com.example.xiscan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private FileOutputStream file;
    private FileInputStream fileIn;
    private String data="";
    private StringBuffer sb;
    public TextView textView1;
    private ListView mListView;
    private Context mContext;
    private String award="";
    private EditText spec,sp,head1,head2,head3,six1,six2;
    String url="http://www.google.com";
    Thread th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1= (TextView) findViewById(R.id.text);
        spec=findViewById(R.id.editText);
        sp=findViewById(R.id.editText2);
        head1=findViewById(R.id.editText3);
        head2=findViewById(R.id.editText4);
        head3=findViewById(R.id.editText5);
        six1=findViewById(R.id.editText6);
        six2=findViewById(R.id.editText7);
        textView1.setMovementMethod(new ScrollingMovementMethod());
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
        //clearData(null);
        showData(null);
        String []data=new String[7];
        award="";
        if(sb==null||sb.toString().equals("")){
            Toast.makeText(this, "尚未掃描任何資料!", Toast.LENGTH_LONG).show();
            return;
        }
        data[0]=spec.getText().toString();data[1]=sp.getText().toString();data[2]=head1.getText().toString();
        data[3]=head2.getText().toString();data[4]=head3.getText().toString();data[5]=six1.getText().toString();
        data[6]=six2.getText().toString();
        String []dataInBase=(sb.toString()).split("\n");//7~length
        for(int i=0;i<dataInBase.length;i++){
            if(dataInBase[i].substring(9,17).equals(data[0])) {
                award += "不得了啦!!您" + dataInBase[i].substring(0, 7) + "的發票 " + dataInBase[i].substring(9, 17) + "中了特別獎1000萬啦!!!!\n";
                break;
            }
            else if(dataInBase[i].substring(9,17).equals(data[1])){
                award+="挖賽!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了特獎200萬啦!!!!\n";
                break;
            }
            else if(true){
                for(int k=2;k<=4;k++){
                    if(dataInBase[i].substring(9,17).equals(data[k])){
                        award+="挖!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了頭獎20萬啦!!!!\n";
                        break;
                    }
                    else if(dataInBase[i].substring(10,17).equals(data[k].substring(1,data[k].length()))){
                        award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了二獎4萬啦!!!!\n";
                        break;
                    }
                    else if(dataInBase[i].substring(11,17).equals(data[k].substring(2,data[k].length()))){
                        award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了三獎1萬啦!!!!\n";
                        break;
                    }
                    else if(dataInBase[i].substring(12,17).equals(data[k].substring(3,data[k].length()))){
                        award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了四獎4000元啦!!!!\n";
                        break;
                    }
                    else if(dataInBase[i].substring(13,17).equals(data[k].substring(4,data[k].length()))){
                        award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了五獎1000元啦!!!!\n";
                        break;
                    }
                    else if(dataInBase[i].substring(14,17).equals(data[k].substring(5,data[k].length()))){
                        award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了五獎1000元啦!!!!\n";
                        break;
                    }
                    else if(dataInBase[i].substring(15,17).equals(data[k].substring(6,data[k].length()))){
                        award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了六獎200元啦!!!!\n";
                        break;
                    }
                }
            }
            else if(dataInBase[i].substring(15,17).equals(data[5])||dataInBase[i].substring(15,17).equals(data[6])){
                award+="恭喜!!!您"+dataInBase[i].substring(0,7)+"的發票 "+dataInBase[i].substring(9,17)+"中了六獎200元啦!!!!\n";
                break;
            }
            //else
        }
        if(award.equals(""))
            award="真可惜，這期您的發票都沒有中獎QAQ";
        textView1.setText(award);
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
    public String GetURLData() {

        String urlData = null;

        String decodedString;

        try {
            HttpURLConnection hc = null;

            //建立網址物件

            URL url2 = new URL(url);

            //連線

            hc = (HttpURLConnection) url2.openConnection();

            //hc.setRequestMethod("GET");

            hc.setDoInput(true);

            hc.setDoOutput(true);

            hc.connect();

            //用BufferedReader讀回來

            BufferedReader in = new BufferedReader(new InputStreamReader(

                    hc.getInputStream()));

            while ((decodedString = in.readLine()) != null) {

                urlData += decodedString;

            }

            in.close();

        } catch (Exception e) {
            Toast.makeText(this, "資料庫初始化失敗", Toast.LENGTH_LONG).show();
            Log.e("ERROR", e.toString());

        }

        return urlData;

    }
}