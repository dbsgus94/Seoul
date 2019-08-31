package com.e.codingmon;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> posterID = new ArrayList<String>();
    static ArrayList<String> imageindexlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.enableDefaults();
        imageParsing();
        Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        MyGalleryAdapter galAdapter = new MyGalleryAdapter(this);
        gallery.setAdapter(galAdapter);
    }

    public class MyGalleryAdapter extends BaseAdapter {

        Context context;

        public MyGalleryAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return posterID.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview = new ImageView(context);
            imageview.setLayoutParams(new Gallery.LayoutParams(300, 400));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview.setPadding(5, 5, 5, 5);
            imageview.setImageDrawable(LoadImageFromWebOperations(posterID.get(position)));

            final int pos = position;
            imageview.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Intent intent = new Intent(MainActivity.this, ParkInfoActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("position", imageindexlist.get(pos));
                    intent.putExtras(extras);
                    startActivity(intent);
                    return false;
                }
            });

            return imageview;
        }
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
}

