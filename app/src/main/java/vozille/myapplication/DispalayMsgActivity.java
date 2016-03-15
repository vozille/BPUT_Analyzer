package vozille.myapplication;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DispalayMsgActivity extends AppCompatActivity {
    public static final boolean DEBUG = false;
    public String filename = "data.json";
    public boolean userinteract = false;
    public HashMap<Integer,String> subcredit_map = new HashMap<>();
    public HashMap<Integer,String> prevgrade_map = new HashMap<>();
    public HashMap<Integer,Integer> sgpa_map = new HashMap<>();
    public HashMap<Integer,Integer> credit_map = new HashMap<>();
    public HashMap<String,Float> val_map = new HashMap<>();
    public HashMap<Integer,Float> sgpa_precision = new HashMap<>();
    public Bundle data = new Bundle();
    public int ctr = 1000003, rowid = 1;
    public ArrayList<Integer> id_codes = new ArrayList<>();

    public ArrayList<String> formatter(String data){
        ArrayList<String> res = new ArrayList<>();
        for(String i : data.split(",[ ]*")){
            res.add(i);
        }
        return res;
    }

    public void initialise(){
        subcredit_map.clear();
        prevgrade_map.clear();
        sgpa_map.clear();
        credit_map.clear();
        val_map.clear();
        sgpa_precision.clear();

        ctr = 1000003;
        rowid = 1;
    }

    @Override
    public void onUserInteraction(){
        super.onUserInteraction();
        userinteract = true;
    }

    @Override
    public void onBackPressed(){
        Intent action = new Intent(this, history.class);
        startActivity(action);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispalay_msg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Result");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        initialise();

        val_map.put("O", 10.0f);
        val_map.put("E",9f);
        val_map.put("A",8f);
        val_map.put("B",7f);
        val_map.put("C",6f);
        val_map.put("D",5f);
        val_map.put("F",2f);
        val_map.put("S",0f);
        val_map.put("M", 0f);


        ArrayList<ArrayList<String>>subject_grade = new ArrayList<>();
        ArrayList<ArrayList<String>>subject_code = new ArrayList<>();
        ArrayList<ArrayList<String>>subject_credit = new ArrayList<>();
        ArrayList<ArrayList<String>>subject_name = new ArrayList<>();


        for(int i = 1; i < 5550; i+= 4){
            id_codes.add(i);
        }


        if(DEBUG) {
            /*
            Use this for debugging purposes by setting DEBUG = true
             */
            TextView name = (TextView) findViewById(R.id.name);
//            name.setText("  SAI PRAJNA NAYAK");
//            TextView branch = (TextView) findViewById(R.id.branch);
//            branch.setText("  MECHANICAL ENGINEERING");
//            TextView roll = (TextView) findViewById(R.id.roll);
//            roll.setText("  1301106106");
//
//            Bundle data = getIntent().getExtras();
//            String a1 = "Grade, E, O, E, E, A, A, O, E, E";
//            String b1 = "Subject Code, BEEE2215, BSCM1210, HSSM3204, PCME4204, PCME4205, PCME4206, HSSM7203, PCME7203, PCME7204, 26";
//            String c1 = "Subject, ENERGY CONVERSION TECHNIQUES, MATHEMATICS - IV, ENGINEERING ECONOMICS AND COSTING, KINEMATICS AND DYNAMICS OF MACHINES, ENGINEERING THERMODYNAMICS, BASIC MANUFACTURING PROCESSES, COMMUNICATION AND INTERPERSONAL SKILLS FOR CORPORATE READINESS LABORATORY, MACHINE SHOP AND FABRICATION PRACTICE, MATERIAL TESTING & HYDRAULIC MACHINES LABORATORY, SGPA: 9.00";
//            String d1 = "Credit, 3, 4, 3, 4, 3, 3, 2, 2, 2";
//            /*
//            End of testing code
//             */
//
//            ArrayList<String> aa1 = formatter(a1);
//            ArrayList<String> bb1 = formatter(b1);
//            ArrayList<String> cc1 = formatter(c1);
//            ArrayList<String> dd1 = formatter(d1);
//            aa1.remove(0);
//            bb1.remove(0);
//            cc1.remove(0);
//            dd1.remove(0);
//            subject_grade.add(aa1);
//            subject_code.add(bb1);
//            subject_name.add(cc1);
//            subject_credit.add(dd1);
//
//
//            a1 = "Grade, E, A, A, O, B, E, O, E, E";
//            b1 = "Subject Code, BECS2208, BSCM1205, HSSM3205, PCME4201, PCME4202, PCME4203, BECS7208, PCME7201, PCME7202, 27";
//            c1 = "Subject, DATABASE MANAGEMENT SYSTEM, MATHEMATICS - III, ORGANISATIONAL BEHAVIOUR, FLUID MECHANICS AND HYDRAULIC MACHINES, MECHANICS OF SOLIDS, INTRODUCTION TO PHYSICAL METALLURGY AND ENGG MATERIALS, DATABASE MANAGEMENT SYSTEM LABORATORY, MACHINE DRAWING, MECHANICAL ENGINEERING LABORATORY, SGPA: 8.74";
//            d1 = "Credit, 3, 4, 3, 4, 3, 4, 2, 2, 2";
//
//            aa1 = formatter(a1);
//            bb1 = formatter(b1);
//            cc1 = formatter(c1);
//            dd1 = formatter(d1);
//            aa1.remove(0);
//            bb1.remove(0);
//            cc1.remove(0);
//            dd1.remove(0);
//            subject_grade.add(aa1);
//            subject_code.add(bb1);
//            subject_name.add(cc1);
//            subject_credit.add(dd1);
        }
        //TODO boundary
        else{

            Intent intent = getIntent();
            Object ob1 = intent.getSerializableExtra("subject_grade");
            data = intent.getExtras();
            if (ob1 != null) {
                subject_grade = (ArrayList<ArrayList<String>>) ob1;
            }
            ob1 = intent.getSerializableExtra("subject_name");
            if (ob1 != null) {
                subject_name = (ArrayList<ArrayList<String>>) ob1;
            }
            ob1 = intent.getSerializableExtra("subject_code");
            if (ob1 != null) {
                subject_code = (ArrayList<ArrayList<String>>) ob1;
            }
            ob1 = intent.getSerializableExtra("subject_credit");
            if (ob1 != null) {
                subject_credit = (ArrayList<ArrayList<String>>) ob1;
            }


            TextView name = (TextView) findViewById(R.id.name);
            name.setText(data.getString("name"));
            TextView branch = (TextView) findViewById(R.id.branch);
            branch.setText(data.getString("branch"));
            TextView roll = (TextView) findViewById(R.id.roll);
            roll.setText(data.getString("roll"));
            TextView clg = (TextView) findViewById(R.id.college);
            clg.setText(data.getString("college"));
        }
        if(subject_code.size() == subject_credit.size() && subject_credit.size() == subject_grade.size() && subject_grade.size() == subject_name.size()){

        }
        else{
            subject_code.clear();
            subject_credit.clear();
            subject_grade.clear();
            subject_name.clear();
            onBackPressed();
        }
        if(subject_code.size() == 0){
            onBackPressed();
            return;
        }
        insert_data(subject_code,subject_credit,subject_grade,subject_name);
    }
    public void insert_data(ArrayList<ArrayList<String>> subject_code,ArrayList<ArrayList<String>> subject_credit,
                            ArrayList<ArrayList<String>> subject_grade,ArrayList<ArrayList<String>> subject_name){
        String sgpa;
        ArrayList<String> sem_headers = new ArrayList<>();
        for(int i = 1; i <= 100; i++){
            sem_headers.add("Sem - "+String.format("%d",i));
        }
        while(! subject_code.isEmpty()) {

            String credit = subject_code.get(0).get(subject_code.get(0).size() - 1);
            float mysgpa = 0.0f;
            for(int i = 0; i < subject_grade.get(0).size(); i++){
                mysgpa += (val_map.get(subject_grade.get(0).get(i))*Float.parseFloat(subject_credit.get(0).get(i)))/(Float.parseFloat(credit));
            }

            sgpa = String.format("%.2f",mysgpa);
            subject_code.get(0).remove(subject_code.get(0).size() - 1);

            add_separator(rowid);
            ++rowid;
            add_semheader(sem_headers.get(0));
//            add_separator(rowid);
//            ++rowid;
            add_header(rowid);
            ++rowid;
            boolean switcher = false;
            while (!subject_grade.get(0).isEmpty()) {
                add_data(rowid,subject_name.get(0).get(0), subject_code.get(0).get(0),
                        subject_credit.get(0).get(0), subject_grade.get(0).get(0), id_codes.get(0), ctr, switcher);
                ++rowid;
                switcher = ! switcher;
                subject_grade.get(0).remove(0);
                subject_code.get(0).remove(0);
                subject_credit.get(0).remove(0);
                subject_name.get(0).remove(0);
                id_codes.remove(0);
            }
            sem_headers.remove(0);
            subject_code.remove(0);
            subject_grade.remove(0);
            subject_credit.remove(0);
            subject_name.remove(0);
            sgpa_precision.put(ctr, Float.parseFloat(sgpa));
            add_sgpa(rowid, credit, sgpa, ctr);
            ++rowid;
            add_separator(rowid);
            ++rowid;
            ctr += 2;
            add_reset_button(rowid);
            ++rowid;
            add_separator(rowid);
            ++rowid;

        }
        add_cgpa();
        add_separator(rowid);
        ++rowid;
    }

    public void add_separator(int rowid){
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        TextView c1 = new TextView(this);
        c1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        c1.setHeight(30);
        c1.setBackgroundColor(android.R.color.holo_green_dark);
        row.addView(c1);
        body.addView(row);
    }

    public void add_semheader(String text){
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.color.sem_header);
        TextView c = new TextView(this);
        c.setText(text);
        c.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        c.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        c.setTypeface(Typeface.DEFAULT_BOLD);
        c.setTextSize(25);
        c.setTextColor(Color.parseColor("#fffff0"));
        row.addView(c);
        body.addView(row);
    }

    public void add_header(int rowid){
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        row.setId(rowid);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(Color.parseColor("#b378d3"));
        TextView c1 = new TextView(this);
        c1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f));
        c1.setTypeface(null, Typeface.BOLD);
        c1.setTextColor(Color.parseColor("#fffff0"));
        c1.setText(R.string.subnames);
        TextView c2 = new TextView(this);
        c2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
        c2.setTypeface(null, Typeface.BOLD);
        c2.setTextColor(Color.parseColor("#fffff0"));
        c2.setText(R.string.subcode);
        c2.setVisibility(View.INVISIBLE);
        TextView c3 = new TextView(this);
        c3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.1f));
        c3.setTypeface(null, Typeface.BOLD);
        c3.setTextColor(Color.parseColor("#fffff0"));
        c3.setText(R.string.credit);

        TextView c4 = new TextView(this);
        c4.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
        c4.setTypeface(null, Typeface.BOLD);
        c4.setTextColor(Color.parseColor("#fffff0"));
        c4.setText(R.string.subgrade);
        c4.setGravity(Gravity.END);
        row.addView(c1);
        row.addView(c2);
        row.addView(c3);
        row.addView(c4);
        body.addView(row);
    }
    public  void add_data(int rowid,String sub_name, final String sub_code, String credit, String grade, int id, int sgpaid, boolean switcher){
        //TODO add credit, sgpa id parameters
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        row.setId(rowid);
        row.setOrientation(LinearLayout.HORIZONTAL);
        if(switcher){
            row.setBackgroundColor(Color.parseColor("#d6ebf2"));
        }
        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView c1 = new TextView(this);
        c1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 8f));

        // TODO: 21-02-2016 chutiyapa
//        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){ // stupid rendering problems
//            c1.setText(sub_name);
//        }
//        else
        c1.setText(sub_name);
        TextView c2 = new TextView(this);
        c2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        c2.setGravity(Gravity.END);
        c2.setText(sub_code);
        c2.setVisibility(View.INVISIBLE);
        TextView c3 = new TextView(this);
        c3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.1f));

        //c3.setGravity(Gravity.END);
        c3.setText(credit);
        Spinner s1 = new Spinner(this);
        String[]grade_k = {"O","E","A","B","C","D","F","S","M"};
        ArrayAdapter<String> a1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,grade_k);
        a1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        s1.setAdapter(a1);
        s1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
        s1.setId(id);
        c1.setId(id + 1);
        c2.setId(id + 2);
        c3.setId(id + 3);
        subcredit_map.put(id + 3, credit);
        sgpa_map.put(id, sgpaid);
        credit_map.put(id, sgpaid + 1);
        prevgrade_map.put(id, grade);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (userinteract) {
                    int curr_id = parent.getId();
                    System.out.println(curr_id);
                    TextView curr_c;
                    if(curr_id == 1){
                        int foo = 4;
                        curr_c =(TextView) findViewById(foo);
                    }
                    else{
                        curr_c = (TextView) findViewById(curr_id + 3);
                    }

                    String sel_grade = parent.getItemAtPosition(pos).toString();

                    String cre_sub = curr_c.getText().toString();
                    float credit_sub = Float.parseFloat(cre_sub);
                    TextView sg = (TextView) findViewById(sgpa_map.get(curr_id));
                    TextView tot_c = (TextView) findViewById(credit_map.get(curr_id));
                    float sgpa = sgpa_precision.get(sgpa_map.get(curr_id));
                    float credit = Float.parseFloat(tot_c.getText().toString());
                    float change =
                            ((-val_map.get(prevgrade_map.get(curr_id)) + val_map.get(sel_grade)) * credit_sub) / credit;
                    prevgrade_map.put(curr_id, sel_grade);
                    sgpa += (double) change;
                    float newsgpa = sgpa;
                    sg.setText(String.format("%.2f",newsgpa));

                    sgpa_precision.put(sgpa_map.get(curr_id), sgpa);
                    TextView c = (TextView) findViewById(R.id.cgpa_student);
                    c.setText("CGPA : " + calc_cgpa());

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int garbage = 5;
            }
        });
        s1.setSelection(a1.getPosition(grade));
        row.addView(c1);
        row.addView(c2);
        row.addView(c3);
        row.addView(s1);
        body.addView(row);
    }

    public void add_sgpa(int rowid, String credit, String sgpa, int id){
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        row.setId(rowid);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(Color.parseColor("#343434"));
        if(! credit.isEmpty()){
            TextView c1 = new TextView(this);
            c1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            c1.setText(R.string.credit);
            TextView c2 = new TextView(this);
            c2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            c2.setText(credit);
            TextView c3 = new TextView(this);
            c3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            c3.setText(R.string.sgpa);
            TextView c4 = new TextView(this);
            c4.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            c4.setId(id);
            c2.setId(id + 1);
            c4.setText(sgpa);
            c3.setGravity(Gravity.END);
            c4.setGravity(Gravity.END);
            c1.setTextColor(Color.parseColor("#fffff0"));
            c2.setTextColor(Color.parseColor("#fffff0"));
            c3.setTextColor(Color.parseColor("#fffff0"));
            c4.setTextColor(Color.parseColor("#fffff0"));
            c1.setTextSize(20);
            c2.setTextSize(20);
            c3.setTextSize(20);
            c4.setTextSize(20);
            c1.setTypeface(null, Typeface.BOLD);
            c2.setTypeface(null, Typeface.BOLD);
            c3.setTypeface(null, Typeface.BOLD);
            c4.setTypeface(null, Typeface.BOLD);
            row.addView(c1);
            row.addView(c2);
            row.addView(c3);
            row.addView(c4);
            body.addView(row);
        }
    }

    public void add_reset_button(int rowid){
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        row.setId(rowid);
        Button reset = new Button(this);
        reset.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        reset.setText(R.string.resetbtn);
        reset.setTypeface(null, Typeface.BOLD);
        reset.setBackgroundResource(R.drawable.rectanglebutton1);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                intent.putExtras(data);
                ArrayList<student> all_students = new ArrayList<>();
                try {
                    Gson gson = new Gson();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename)));
                    all_students = gson.fromJson(bufferedReader, new TypeToken<ArrayList<student>>() {
                    }.getType());
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    all_students.clear();
                }
                SharedPreferences preferences = getSharedPreferences("myappdata", MODE_PRIVATE);
                int id = preferences.getInt("id", 0);
                System.out.println(id);
                intent.putExtra("subject_grade", all_students.get(id).getSubject_grade());
                intent.putExtra("subject_code", all_students.get(id).getSubject_code());
                intent.putExtra("subject_credit", all_students.get(id).getSubject_credit());
                intent.putExtra("subject_name", all_students.get(id).getSubject_name());
                startActivity(intent);
            }
        });
        body.addView(row);
        row.addView(reset);
    }
    public void add_cgpa(){
        LinearLayout body = (LinearLayout) findViewById(R.id.main_result_display);
        LinearLayout row = new LinearLayout(this);
        TextView c = new TextView(this);
        c.setId(R.id.cgpa_student);
        c.setText("CGPA : " +calc_cgpa());
        c.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        c.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        c.setTypeface(Typeface.DEFAULT_BOLD);
        c.setTextSize(25);
        c.setTextColor(Color.parseColor("#fffff0"));
        c.setBackgroundResource(R.color.cgpa);
        row.addView(c);
        body.addView(row);

    }
    public String calc_cgpa(){
        float h = 0.0f,t = 0.0f,num = 0.0f,denom = 0.0f;
        Set<Integer> k = sgpa_precision.keySet();
        for(Integer i : k){
            TextView c = (TextView) findViewById(i); // grade
            TextView d = (TextView) findViewById(i + 1); // credit
            num = Float.parseFloat(c.getText().toString());
            denom = Float.parseFloat(d.getText().toString());
            h += num*denom;
            t += denom;
        }
        if(t < 1.0){
            t = 1.0f;
        }
        return String.format("%.2f",h/t);
    }
}
