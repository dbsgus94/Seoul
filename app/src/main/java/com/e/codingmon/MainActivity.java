package com.e.codingmon;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView textView;
    TextView textView5;

    static ArrayList<String> imageURLList ; //api에서 받아온 이미지의 url
    static ArrayList<String> imageindexlist; //api에서 받아온 이미지의 index
    static ArrayList<String> parkList ; //api에서 받아온 공원의 이름
    static ArrayList<String> latitudeList; //api에서 받아온 공원 위치의 latitude
    static ArrayList<String> longtitudeList; //api에서 받아온 공원 위치의 longitude
    static ArrayList<Place> places; //공원들의 list

    LinearLayout linearLayout;

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //걸음 수 카운터
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private int mStepOffset;
    public static float progressValue;
    public ProgressBar simpleProgressBar;

    static double latitude;
    static double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageURLList = new ArrayList<String>();
        imageindexlist = new ArrayList<>();
        parkList = new ArrayList<>();
        latitudeList = new ArrayList<>();
        longtitudeList = new ArrayList<>();
        places = new ArrayList<Place>();

        ImageButton refreshButton = (ImageButton) findViewById(R.id.mapsRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        //걸음 수 카운터
      simpleProgressBar=(ProgressBar) findViewById(R.id.progress);
      simpleProgressBar.setMax(8000);
      textView5= (TextView)findViewById(R.id.textView5);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                != null)
        {
            mSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }

        //위치 퍼미션
        if(!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(MainActivity.this);

        //다이어트 자극 명언 보여주기
        /*다음 링크 참고
        https://m.blog.naver.com/PostView.nhn?blogId=dagymdieting&logNo=221306225461&proxyReferer=https%3A%2F%2Fwww.google.com%2F
        https://post.naver.com/viewer/postView.nhn?volumeNo=16844224&memberNo=41829949
        https://1boon.kakao.com/tlxpass/171117
        https://www.facebook.com/diettalk/posts/912318102119245/*/
        String diet[] = {"운동은 당신의 몸을 증오하기 때문이 아니라, 사랑하기 때문에 하는 거예요.",
                "아무것도 바꾸지 않으면 변하는 것은 없다.",
                "당신이 아무리 천천히 걷고 있다고 해도 쇼파에 엉덩이를 붙이고 있는 사람들보다는 낫다!",
                "근육을 붙이려면 몸을 바삐 움직여야지.",
                "사실, 난 할 수 있어.",
                "당신의 몸에 귀를 기울여라.",
                "나약함이 동반되지 않은 강인함이란 없다.",
                "당신이 변화의 주역!",
                "시도하지 않으면, 절대 알 수 없어요.",
                "어제보다 더 멋진 나를 만드세요.",
                "건강한 음식, 더 많은 움직임, 나 스스로를 사랑하는 일.",
                "나에게 집중하세요.",
                "길은 24시간 열려있다. 나가서 뛰어라.",
                "승리는 가장 끈기있는 자에게 돌아간다.",
                "계속 노력해야 해요. 지름길은 없어요.",
                "원하는 몸을 만들기 위해 지금의 몸을 부수자.",
                "포기하지 마세요. 시작은 언제나 힘든 법입니다.",
                "목표까지 아직 멀었을지 몰라도 어제보다 가까워졌어요.",
                "포기하고 도망치거나 더 노력하거나. 내 선택은?",
                "생각이 바뀌면 몸도 변하기 시작한다.",
                "가장 큰 거짓말, 나 내일부터 다이어트 할거야.",
                "당신의 몸은 당신의 라이프스타일을 반영한다.",
                "짧게라도 걷는 것이 아예 안 걷는 것보다 낫다.",
                "바라지만 말고 실천해라! 언제까지 바라기만 할건가!",
                "힘들지 않다면 바뀌지 않는다."};

        //다이어트 자극 명언을 random하게 보여주기
        SharedPreferences sp = getSharedPreferences("diet", MODE_PRIVATE);
        int index = sp.getInt("index", 0);
        textView  = findViewById(R.id.textView2);
        textView.setText(diet[index]);
        SharedPreferences.Editor editor = sp.edit();
        if(index == 24) index = 0;
        else index ++;
        editor.putInt("index", index);
        editor.apply();

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

        //참고한 사이트: https://stackoverflow.com/questions/4298893/android-how-do-i-create-a-function-that-will-be-executed-only-once
        //SharedPreferences settings = getSharedPreferences("settings", 0);
        //boolean firstStart = settings.getBoolean("firstStart", true);

        //if(firstStart) {
        //   SharedPreferences.Editor settingsEditor = settings.edit();
        //   settingsEditor.putBoolean("firstStart", false);
        //   settingsEditor.commit();
        // }

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        LatLng curLatLng = new LatLng(latitude, longitude);

        imageParsing();

        for(int i = 0; i < 131; i++) {
            places.add(new Place(parkList.get(i), new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longtitudeList.get(i))), imageURLList.get(i), imageindexlist.get(i)));
        }

        Collections.sort(places, new SortPlaces(curLatLng)); //공원들을 현재 위치를 기반으로 거리별 sorting

        for(int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 400));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setImageDrawable(LoadImageFromWebOperations(places.get(i).imageurl)); //sorting된 후
            linearLayout.addView(imageView);
            final int num = i;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ParkInfoActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("position", places.get(num).index);
                    intent.putExtras(extras);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorPresent)
        {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSensorPresent)
        {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mStepOffset == 0) {
            mStepOffset = (int) event.values[0];
        }

        simpleProgressBar.setProgress(mStepOffset);
        textView5.setText(""+mStepOffset +" / " +8000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //참고한 사이트: https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, null);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    //참고한 사이트: https://coding-factory.tistory.com/39
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
                            imageURLList.add(p_img);
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
            finish();
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        }
    }



    //참고한 사이트: https://stackoverflow.com/questions/29711728/how-to-sort-geo-points-according-to-the-distance-from-current-location-in-androi
    public class Place {
        public String name;
        public LatLng latlng;
        public String imageurl;
        public String index;

        public Place(String name, LatLng latlng, String imageurl, String index) {
            this.name = name;
            this.latlng = latlng;
            this.imageurl = imageurl;
            this.index = index;
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

    //참고한 사이트: https://webnautes.tistory.com/1315
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) { //요청 코드가 PERMISSONS_REQUEST_CODE이고, 요청한 퍼시면 개수만큼 수신되었다면
            boolean check_result = true;
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if(check_result) {
            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

