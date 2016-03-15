package vozille.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class compare_home extends AppCompatActivity {

    public String filename = "data.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_home);

        Intent intent = getIntent();
        int pos1 = intent.getIntExtra("player1", 0);
        int pos2 = intent.getIntExtra("player2", 0);
        ArrayList<student> all_students = new ArrayList<>();
        try{
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename)));
            all_students = gson.fromJson(bufferedReader,new TypeToken<ArrayList<student>>(){}.getType());
            bufferedReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
            all_students.clear();
            return;
        }
        //TODO the basic details
        TextView t1 = (TextView) findViewById(R.id.name1);
        TextView t2 = (TextView) findViewById(R.id.name2);
        t1.setText(all_students.get(pos1).get_details().get(0));
        t2.setText(all_students.get(pos2).get_details().get(0));
        t1 = (TextView) findViewById(R.id.roll1);
        t2 = (TextView) findViewById(R.id.roll2);
        t1.setText(all_students.get(pos1).get_details().get(1));
        t2.setText(all_students.get(pos2).get_details().get(1));
        t1 = (TextView) findViewById(R.id.roll1);
        t2 = (TextView) findViewById(R.id.roll2);
        t1.setText(all_students.get(pos1).get_details().get(1));
        t2.setText(all_students.get(pos2).get_details().get(1));
        t1 = (TextView) findViewById(R.id.branch1);
        t2 = (TextView) findViewById(R.id.branch2);
        t1.setText(all_students.get(pos1).get_details().get(2));
        t2.setText(all_students.get(pos2).get_details().get(2));

        //TODO the counting phase
        Map<String,Integer> grade1 = new HashMap<>();
        Map<String,Integer> grade2 = new HashMap<>();
        grade1.put("O", 0);
        grade1.put("E", 0);
        grade1.put("A", 0);
        grade1.put("B", 0);
        grade1.put("C", 0);
        grade1.put("D", 0);
        grade1.put("F", 0);
        grade1.put("S", 0);
        grade1.put("M", 0);
        grade2.put("O", 0);
        grade2.put("E", 0);
        grade2.put("A", 0);
        grade2.put("B", 0);
        grade2.put("C", 0);
        grade2.put("D", 0);
        grade2.put("F", 0);
        grade2.put("S", 0);
        grade2.put("M", 0);
        int cnt_1 = 0, cnt_2 = 0;
        for(ArrayList<String> i : all_students.get(pos1).getSubject_grade()){
            for(String j : i){
                grade1.put(j,grade1.get(j) + 1);
                cnt_1++;
            }
        }
        for(ArrayList<String> i : all_students.get(pos2).getSubject_grade()){
            for(String j : i){
                grade2.put(j,grade2.get(j) + 1);
                cnt_2++;
            }
        }
        if(cnt_1 == 0 || cnt_2 == 0){
            return;
        }
        //TODO calculate sgpa since we dont store sgpa

        //TODO setting the grades
        t1 = (TextView) findViewById(R.id.O_cnt1);
        t2 = (TextView) findViewById(R.id.O_cnt2);
        t1.setText(Integer.toString(grade1.get("O"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("O"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.O_per1);
        t2 = (TextView) findViewById(R.id.O_per2);
        float p1 = 100*((float) grade1.get("O")) /( (float) cnt_1);
        float p2 = 100*((float) grade2.get("O")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("O") != 0 || grade2.get("O") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.higher);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.higher);
                t2.setBackgroundResource(R.color.higher);
            } else {
                t2.setBackgroundResource(R.color.higher);
            }
        }
        // E
        t1 = (TextView) findViewById(R.id.E_cnt1);
        t2 = (TextView) findViewById(R.id.E_cnt2);
        t1.setText(Integer.toString(grade1.get("E"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("E"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.E_per1);
        t2 = (TextView) findViewById(R.id.E_per2);
        p1 = 100*((float) grade1.get("E")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("E")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("E") != 0 || grade2.get("E") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.higher);


            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.higher);
                t2.setBackgroundResource(R.color.higher);
            } else {
                t2.setBackgroundResource(R.color.higher);
            }
        }
        // A
        t1 = (TextView) findViewById(R.id.A_cnt1);
        t2 = (TextView) findViewById(R.id.A_cnt2);
        t1.setText(Integer.toString(grade1.get("A"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("A"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.A_per1);
        t2 = (TextView) findViewById(R.id.A_per2);
        p1 = 100*((float) grade1.get("A")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("A")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("A") != 0 || grade2.get("A") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.high);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.high);
                t2.setBackgroundResource(R.color.high);
            } else {
                t2.setBackgroundResource(R.color.high);
            }
        }
        // B
        t1 = (TextView) findViewById(R.id.B_cnt1);
        t2 = (TextView) findViewById(R.id.B_cnt2);
        t1.setText(Integer.toString(grade1.get("B"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("B"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.B_per1);
        t2 = (TextView) findViewById(R.id.B_per2);
        p1 = 100*((float) grade1.get("B")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("B")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("B") != 0 || grade2.get("B") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.neutral);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.neutral);
                t2.setBackgroundResource(R.color.neutral);
            } else {
                t2.setBackgroundResource(R.color.neutral);
            }
        }
        // C
        t1 = (TextView) findViewById(R.id.C_cnt1);
        t2 = (TextView) findViewById(R.id.C_cnt2);
        t1.setText(Integer.toString(grade1.get("C"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("C"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.C_per1);
        t2 = (TextView) findViewById(R.id.C_per2);
        p1 = 100*((float) grade1.get("C")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("C")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("C") != 0 || grade2.get("C") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.low);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.low);
                t2.setBackgroundResource(R.color.low);
            } else {
                t2.setBackgroundResource(R.color.low);
            }
        }
        // D
        t1 = (TextView) findViewById(R.id.D_cnt1);
        t2 = (TextView) findViewById(R.id.D_cnt2);
        t1.setText(Integer.toString(grade1.get("D"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("D"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.D_per1);
        t2 = (TextView) findViewById(R.id.D_per2);
        p1 = 100*((float) grade1.get("D")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("D")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("D") != 0 || grade2.get("D") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.low);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.low);
                t2.setBackgroundResource(R.color.low);
            } else {
                t2.setBackgroundResource(R.color.low);
            }
        }
        // F
        t1 = (TextView) findViewById(R.id.F_cnt1);
        t2 = (TextView) findViewById(R.id.F_cnt2);
        t1.setText(Integer.toString(grade1.get("F"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("F"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.F_per1);
        t2 = (TextView) findViewById(R.id.F_per2);
        p1 = 100*((float) grade1.get("F")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("F")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("F") != 0 || grade2.get("F") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.lower);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.lower);
                t2.setBackgroundResource(R.color.lower);
            } else {
                t2.setBackgroundResource(R.color.lower);
            }
        }
        // S
        t1 = (TextView) findViewById(R.id.S_cnt1);
        t2 = (TextView) findViewById(R.id.S_cnt2);
        t1.setText(Integer.toString(grade1.get("S"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("S"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.S_per1);
        t2 = (TextView) findViewById(R.id.S_per2);
        p1 = 100*((float) grade1.get("S")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("S")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("S") != 0 || grade2.get("S") != 0) {
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.lower);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.lower);
                t2.setBackgroundResource(R.color.lower);
            } else {
                t2.setBackgroundResource(R.color.lower);
            }
        }
        // M
        t1 = (TextView) findViewById(R.id.M_cnt1);
        t2 = (TextView) findViewById(R.id.M_cnt2);
        t1.setText(Integer.toString(grade1.get("M"))+"/"+Integer.toString(cnt_1));
        t2.setText(Integer.toString(grade2.get("M"))+"/"+Integer.toString(cnt_2));
        t1 = (TextView) findViewById(R.id.M_per1);
        t2 = (TextView) findViewById(R.id.M_per2);
        p1 = 100*((float) grade1.get("M")) /( (float) cnt_1);
        p2 = 100*((float) grade2.get("M")) /( (float) cnt_2);
        t1.setText(String.format("%.2f",p1) + "%");
        t2.setText(String.format("%.2f",p2) + "%");
        if(grade1.get("M") != 0 && grade2.get("M")!= 0){
            if (p1 > p2) {
                t1.setBackgroundResource(R.color.lower);
            } else if (Math.abs(p1 - p2) < 0.00001f) {
                t1.setBackgroundResource(R.color.lower);
                t2.setBackgroundResource(R.color.lower);
            } else {
                t2.setBackgroundResource(R.color.lower);
            }
        }
        // TODO CGPA SGPA Calc
        Map<String,Float>val_map = new HashMap<>();
        val_map.put("O", 10.0f);
        val_map.put("E",9f);
        val_map.put("A",8f);
        val_map.put("B",7f);
        val_map.put("C",6f);
        val_map.put("D",5f);
        val_map.put("F",2f);
        val_map.put("S",0f);
        val_map.put("M", 0f);
        ArrayList<ArrayList<String>> subject_grade = all_students.get(pos1).getSubject_grade();
        ArrayList<ArrayList<String>>subject_credit = all_students.get(pos1).getSubject_credit();
        ArrayList<ArrayList<String>>subject_code = all_students.get(pos1).getSubject_code();
        int tot_credit1 = 0;
        float cg1 = 0.0f;
        ArrayList<Float>sgpa1 = new ArrayList<>();
        for(int j = 0; j < subject_code.size(); j++){
            String credit = subject_code.get(j).get(subject_code.get(j).size() - 1);
            tot_credit1 += Integer.parseInt(credit);
            float mysgpa = 0.0f;
            for (int i = 0; i < subject_grade.get(j).size(); i++) {
                mysgpa += (val_map.get(subject_grade.get(j).get(i)) * Float.parseFloat(subject_credit.get(j).get(i))) / (Float.parseFloat(credit));
            }
            cg1 += mysgpa*Float.parseFloat(credit);
            sgpa1.add(mysgpa);
        }
        cg1 /= tot_credit1;
        subject_grade = all_students.get(pos2).getSubject_grade();
        subject_credit = all_students.get(pos2).getSubject_credit();
        subject_code = all_students.get(pos2).getSubject_code();

        int tot_credit2 = 0;
        float cg2 = 0.0f;
        ArrayList<Float>sgpa2 = new ArrayList<>();
        for(int j = 0; j < subject_code.size(); j++){
            String credit = subject_code.get(j).get(subject_code.get(j).size() - 1);
            tot_credit2 += Integer.parseInt(credit);
            float mysgpa = 0.0f;
            for (int i = 0; i < subject_grade.get(j).size(); i++) {
                mysgpa += (val_map.get(subject_grade.get(j).get(i)) * Float.parseFloat(subject_credit.get(j).get(i))) / (Float.parseFloat(credit));
            }
            cg2 += mysgpa*Float.parseFloat(credit);
            sgpa2.add(mysgpa);
        }
        cg2 /= tot_credit2;

        System.out.println(sgpa1);
        System.out.println(sgpa2);

        // CGPA
        t1 = (TextView) findViewById(R.id.cgpa1);
        t2 = (TextView) findViewById(R.id.cgpa2);
        t1.setText(String.format("%.2f",cg1));
        t2.setText(String.format("%.2f",cg2));

        if(Math.abs(cg1 - cg2) < 0.00001f){
            t1.setBackgroundResource(R.color.higher);
            t2.setBackgroundResource(R.color.higher);
        }
        else{
            if(cg1 > cg2){
                t1.setBackgroundResource(R.color.higher);
            }
            else{
                t2.setBackgroundResource(R.color.higher);
            }
        }
        // SGPA
        float sh1 = Collections.max(sgpa1),sh2 = Collections.max(sgpa2);
        t1 = (TextView) findViewById(R.id.sgpa_h1);
        t2 = (TextView) findViewById(R.id.sgpa_h2);
        t1.setText(String.format("%.2f",sh1));
        t2.setText(String.format("%.2f",sh2));

        if(Math.abs(sh1 - sh2) < 0.00001f){
            t1.setBackgroundResource(R.color.higher);
            t2.setBackgroundResource(R.color.higher);
        }
        else{
            if(sh1 > sh2){
                t1.setBackgroundResource(R.color.higher);
            }
            else{
                t2.setBackgroundResource(R.color.higher);
            }
        }
        sh1 = Collections.min(sgpa1);
        sh2 = Collections.min(sgpa2);
        t1 = (TextView) findViewById(R.id.sgpa_l1);
        t2 = (TextView) findViewById(R.id.sgpa_l2);
        t1.setText(String.format("%.2f",sh1));
        t2.setText(String.format("%.2f",sh2));

        if(Math.abs(sh1 - sh2) < 0.00001f){
            t1.setBackgroundResource(R.color.lower);
            t2.setBackgroundResource(R.color.lower);
        }
        else{
            if(sh1 > sh2){
                t2.setBackgroundResource(R.color.lower);
            }
            else{
                t1.setBackgroundResource(R.color.lower);
            }
        }
        sh1 = sgpa1.get(sgpa1.size() - 1);
        sh2 = sgpa2.get(sgpa2.size() - 1);
        t1 = (TextView) findViewById(R.id.sgpa_r1);
        t2 = (TextView) findViewById(R.id.sgpa_r2);
        t1.setText(String.format("%.2f",sh1));
        t2.setText(String.format("%.2f",sh2));

        if(Math.abs(sh1 - sh2) < 0.00001f){
            t1.setBackgroundResource(R.color.higher);
            t2.setBackgroundResource(R.color.higher);
        }
        else{
            if(sh1 > sh2){
                t1.setBackgroundResource(R.color.higher);
            }
            else{
                t2.setBackgroundResource(R.color.higher);
            }
        }
        ArrayList<Entry> sgchart1 = new ArrayList<>();
        ArrayList<String> sp1 = new ArrayList<>();
        for(int i = 0; i < sgpa1.size(); i++){
            sp1.add(String.format("%d",i + 1));
            sgchart1.add(new Entry(sgpa1.get(i), i + 1));
        }
//        LinearLayout r = (LinearLayout) findViewById(R.id.chart);
//        LineChart chart = new LineChart(this);
//        LineDataSet lineDataSet = new LineDataSet(sgchart1, "p1");
//        ArrayList<LineDataSet> l1 = new ArrayList<>();
//        l1.add(lineDataSet);
//        LineData data1 = new LineData(sp1,l1);
//        chart.setData(data1);
//        r.addView(chart);

    }
}
