package com.example.dinesh.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
     int choosenCelbs = 0;
     ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int locationOfCorrectAnswer = 0;
    String[] answer = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celbChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong! It was "+celebNames.get(choosenCelbs),Toast.LENGTH_LONG).show();
        }
        newQuestion();
    }

    public void newQuestion(){
        Random random = new Random();
        choosenCelbs = random.nextInt(celebUrls.size());

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage;
        try {

            celebImage = imageTask.execute(celebUrls.get(choosenCelbs)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answer[i] = celebNames.get(choosenCelbs);
                } else {
                    incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    while (incorrectAnswerLocation == choosenCelbs) {
                        incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    }
                    answer[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answer[0]);
            button1.setText(answer[1]);
            button2.setText(answer[2]);
            button3.setText(answer[3]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>

    {
        URL url;
        String result = "" ;
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... urls)
        {
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageview);
        DownloadTask task = new DownloadTask();

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){
                celebNames.add(m.group(1));
            }



        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newQuestion();
    }
}
