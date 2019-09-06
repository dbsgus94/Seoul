package com.e.codingmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> posterID = new ArrayList<String>();
    static ArrayList<String> imageindexlist = new ArrayList<>();

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        imageParsing();

        for(int i = 0; i < 50; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 400));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setImageDrawable(LoadImageFromWebOperations(posterID.get(i)));
            linearLayout.addView(imageView);
            final int num = i;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ParkInfoActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("position", imageindexlist.get(num));
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }
        //SharedPreferences에 저장한 위도와 경도값 확인
        SharedPreferences sharedPreferences = getSharedPreferences("latlng", MODE_PRIVATE);
        String latText = sharedPreferences.getString("lat",  "");
        String lngText = sharedPreferences.getString("lng", "");
        //Toast.makeText(MainActivity.this, latText+" "+lngText, Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, lngText, Toast.LENGTH_SHORT).show();

        //System.out.println(latText);
       //System.out.println(lngText);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, null);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void imageParsing() {

        boolean in_p_img = false, in_p_idx = false, inrow = false;

        String p_img = null, p_idx = null;

        try {
            URL url = new URL("http://openapi.seoul.go.kr:8088/72784c79446a697739384452505171/xml/SearchParkInfoService/1/132/");

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱 시작합니다.");

            while(parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals("P_IMG")) {
                            in_p_img = true;
                        }
                        if(parser.getName().equals("P_IDX")) {
                            in_p_idx = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(in_p_img) {
                            p_img = parser.getText();
                            in_p_img = false;
                        }
                        if(in_p_idx) {
                            p_idx = parser.getText();
                            in_p_idx = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("row")) {
                            posterID.add(p_img);
                            imageindexlist.add(p_idx);
                            inrow = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }

        } catch(Exception e) {
            System.out.println("에러 발생");
        }
    }

    public void gotoMaps(View view){
        if(view.getId() == R.id.toMapsbutton){
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        }
    }
}

