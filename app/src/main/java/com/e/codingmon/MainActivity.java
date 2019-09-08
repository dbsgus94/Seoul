package com.e.codingmon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> posterID = new ArrayList<String>();
    static ArrayList<String> imageindexlist = new ArrayList<>();
    static ArrayList<String> parkList = new ArrayList<>();
    static ArrayList<String> latitudeList = new ArrayList<>();
    static ArrayList<String> longtitudeList = new ArrayList<>();
    static ArrayList<Place> places = new ArrayList<Place>();

    LinearLayout linearLayout;
    Location location;
    LatLng latLng;

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

        location = new Location("initialLocation");
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //SharedPreferences에 저장한 위도와 경도값 확인
        //이거 나중에 다시 확인
        SharedPreferences sharedPreferences = getSharedPreferences("latlng", MODE_PRIVATE);
        String curLatText = sharedPreferences.getString("lat",  ""); //현재 위도
        String curLngText = sharedPreferences.getString("lng", ""); // 현재 경도

        Toast.makeText(getApplicationContext(), curLatText, Toast.LENGTH_SHORT).show(); //test용
        Toast.makeText(getApplicationContext(), curLngText, Toast.LENGTH_SHORT).show(); //test용

        //이거는 위치가 바뀌는것
        //LatLng curLatLng = new LatLng(Double.parseDouble(curLatText), Double.parseDouble(curLngText));

        LatLng curLatLng = latLng;

        for(int i = 0; i < 131; i++) {
            places.add(new Place(parkList.get(i), new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longtitudeList.get(i))), posterID.get(i)));
        }

        /*for(Place p:places) {
            Log.i("Places before sorting", "Place: " + p.name);

        }*/

        Collections.sort(places, new SortPlaces(curLatLng));

        /*for(Place p:places) {
            Log.i("Places after sorting", "Place: " + p.name);
        }*/

        for(int i = 0; i < 50; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 400));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
            //imageView.setImageDrawable(LoadImageFromWebOperations(posterID.get(i)));
            imageView.setImageDrawable(LoadImageFromWebOperations(places.get(i).imageurl)); //sorting된 후
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

        boolean in_p_img = false, in_p_idx = false, inrow = false, in_p_park = false, in_longitude = false, in_latitude = false;

        String p_img = null, p_idx = null, p_park = null,longitude = null, latitude = null;

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
                        if(parser.getName().equals("P_PARK")) {
                            in_p_park = true;
                        }
                        if(parser.getName().equals("LONGITUDE")) {
                            in_longitude = true;
                        }
                        if(parser.getName().equals("LATITUDE")) {
                            in_latitude = true;
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
                        if(in_p_park) {
                            p_park = parser.getText();
                            in_p_park = false;
                        }
                        if(in_longitude) {
                            longitude = parser.getText();
                            in_longitude = false;
                        }
                        if(in_latitude) {
                            latitude = parser.getText();
                            in_latitude = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("row")) {
                            posterID.add(p_img);
                            imageindexlist.add(p_idx);
                            parkList.add(p_park);
                            longtitudeList.add(longitude);
                            latitudeList.add(latitude);
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

    public class Place {
        public String name;
        public LatLng latlng;
        public String imageurl;

        public Place(String name, LatLng latlng, String imageurl) {
            this.name = name;
            this.latlng = latlng;
            this.imageurl = imageurl;
        }
    }

    public class SortPlaces implements Comparator<Place> {
        LatLng currentLoc;

        public SortPlaces(LatLng current) {
            currentLoc = current;
        }

        @Override
        public int compare(final Place place1, final Place place2) {
            double lat1 = place1.latlng.latitude;
            double lng1 = place1.latlng.longitude;
            double lat2 = place2.latlng.latitude;
            double lng2 = place2.latlng.longitude;

            double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lng1);
            double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lng2);
            return (int) (distanceToPlace1 - distanceToPlace2);
        }

        public double distance(double fromLat, double fromLng, double toLat, double toLng) {
            double radius = 6378137; //대략적인 지구의 반지름(단위:미터)
            double deltaLat = toLat - fromLat;
            double deltaLng = toLng - fromLng;
            double angle = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat/2), 2) + Math.cos(fromLat) * Math.cos(toLat) * Math.pow(Math.sin(deltaLng/2), 2)));
            return radius * angle;
        }
    }
}

