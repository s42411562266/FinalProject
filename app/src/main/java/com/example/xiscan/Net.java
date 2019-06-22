package com.example.xiscan;

import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Net implements Runnable{
    MainActivity main;
    public Net(MainActivity main){
        this.main=main;
    }
    String url="http://www.twitch.tv";
    //TextView textView1= (TextView) findViewById(R.id.text);
    @Override
    public void run() {
        Connection connect= Jsoup.connect(url);
        connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
        try{
            Document document=connect.get();
            main.textView1.setText(document.head().select("title").text());
        }
        catch(IOException e){
            main.textView1.setText("幹你娘");
        }
    }
}
