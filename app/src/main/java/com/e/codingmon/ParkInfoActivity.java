package com.e.codingmon;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.StrictMode;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.net.URL;

public class ParkInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinfo);

        StrictMode.enableDefaults();

        infoParsing();
    }

    public void infoParsing() {

        TextView textView1 = (TextView)findViewById(R.id.textview1); //파싱된 결과확인

        Bundle extras = getIntent().getExtras();
        String position = extras.getString("position");

        boolean in_p_idx = false, in_p_park = false, in_p_list_content = false,
                in_area = false, in_open_dt = false, in_main_equip = false,
                in_main_plants = false, in_guidance = false, in_visit_road = false,
                in_use_refer = false, in_p_img = false, in_p_zone = false,
                in_p_addr = false, in_p_name = false, in_p_admintel = false,
                in_g_longitude = false, in_g_latitude = false,
                in_longitude = false, in_latitude = false, in_template_url = false , inrow = false;

        String p_idx = null, p_park = null, p_list_content = null,
                area = null, open_dt = null, main_equip = null,
                main_plants = null, guidance = null, visit_road = null,
                use_refer = null, p_img = null, p_zone = null,
                p_addr = null, p_name = null, p_admintel = null,
                g_longitude = null, g_latitude = null,
                longitude = null, latitude = null, template_url = null;


        try {
            String newurl = "http://openapi.seoul.go.kr:8088/72784c79446a697739384452505171/xml/SearchParkInfoService/1/132/" + position;
            URL url = new URL(newurl);

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱 시작합니다.");

            while(parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals("P_IDX")) {
                            in_p_addr = true;
                        }
                        if(parser.getName().equals("P_PARK")) {
                            in_p_park = true;
                        }
                        if(parser.getName().equals("P_LIST_CONTENT")) {
                            in_p_list_content = true;
                        }
                        if(parser.getName().equals("AREA")) {
                            in_area = true;
                        }
                        if(parser.getName().equals("OPEN_DT")) {
                            in_open_dt = true;
                        }
                        if(parser.getName().equals("MAIN_EQUIP")) {
                            in_main_equip = true;
                        }
                        if(parser.getName().equals("MAIN_PLANTS")) {
                            in_main_plants = true;
                        }
                        if(parser.getName().equals("GUIDANCE")) {
                            in_guidance = true;
                        }
                        if(parser.getName().equals("VISIT_ROAD")) {
                            in_visit_road = true;
                        }
                        if(parser.getName().equals("USE_REFER")) {
                            in_use_refer = true;
                        }
                        if(parser.getName().equals("P_IMG")) {
                            in_p_img = true;
                        }
                        if(parser.getName().equals("P_ZONE")) {
                            in_p_zone = true;
                        }
                        if(parser.getName().equals("P_ADDR")) {
                            in_p_addr = true;
                        }
                        if(parser.getName().equals("P_NAME")) {
                            in_p_name = true;
                        }
                        if(parser.getName().equals("P_ADMINTEL")) {
                            in_p_admintel = true;
                        }
                        if(parser.getName().equals("G_LONGITUDE")) {
                            in_g_longitude = true;
                        }
                        if(parser.getName().equals("G_LATITUDE")) {
                            in_g_latitude = true;
                        }
                        if(parser.getName().equals("LONGITUDE")) {
                            in_longitude = true;
                        }
                        if(parser.getName().equals("LATITUDE")) {
                            in_latitude = true;
                        }
                        if(parser.getName().equals("TEMPLATE_URL")) {
                            in_template_url = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(in_p_idx) {
                            p_idx = parser.getText();
                            in_p_idx = false;
                        }
                        if(in_p_park) {
                            p_park = parser.getText();
                            in_p_park = false;
                        }
                        if(in_p_list_content) {
                            p_list_content = parser.getText();
                            in_p_list_content = false;
                        }
                        if(in_area) {
                            area = parser.getText();
                            in_area = false;
                        }
                        if(in_open_dt) {
                            open_dt = parser.getText();
                            in_open_dt = false;
                        }
                        if(in_main_equip) {
                            main_equip = parser.getText();
                            in_main_equip = false;
                        }
                        if(in_main_plants) {
                            main_plants = parser.getText();
                            in_main_plants = false;
                        }
                        if(in_guidance) {
                            guidance = parser.getText();
                            in_guidance = false;
                        }
                        if(in_visit_road) {
                            visit_road = parser.getText();
                            in_visit_road = false;
                        }
                        if(in_use_refer) {
                            use_refer = parser.getText();
                            in_use_refer = false;
                        }
                        if(in_p_img) {
                            p_img = parser.getText();
                            in_p_img = false;
                        }
                        if(in_p_zone) {
                            p_zone = parser.getText();
                            in_p_zone = false;
                        }
                        if(in_p_addr) {
                            p_addr = parser.getText();
                            in_p_addr = false;
                        }
                        if(in_p_name) {
                            p_name = parser.getText();
                            in_p_name = false;
                        }
                        if(in_p_admintel) {
                            p_admintel = parser.getText();
                            in_p_admintel = false;
                        }
                        if(in_g_longitude) {
                            g_longitude = parser.getText();
                            in_g_longitude = false;
                        }
                        if(in_g_latitude) {
                            g_latitude = parser.getText();
                            in_g_latitude = false;
                        }
                        if(in_longitude) {
                            longitude = parser.getText();
                            in_longitude = false;
                        }
                        if(in_latitude) {
                            latitude = parser.getText();
                            in_latitude = false;
                        }
                        if(in_template_url) {
                            template_url = parser.getText();
                            in_template_url = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("row")) {
                            textView1.setText(p_idx+p_park+p_list_content+area+open_dt+main_equip+main_plants+guidance+visit_road+use_refer+p_img+p_zone+p_addr+p_name+p_admintel+g_longitude+g_latitude+longitude+latitude+template_url);
                            inrow = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }

        } catch(Exception e) {
            textView1.setText("에러발생");
        }
    }
}
