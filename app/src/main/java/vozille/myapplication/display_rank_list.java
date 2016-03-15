package vozille.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class display_rank_list extends AppCompatActivity {
    public String filename3 = "ranklists.json";
    public ArrayList<ArrayList<rank_list>> rank_lists = new ArrayList<>();
    class rank_list{
        String name = "";
        String sgpa = "";
        String branch = "";
        String semester = "";
        public void setName(String n){ this.name = n;}
        public void setSgpa(String n){ this.sgpa = n;}
        public void setBranch(String n){this.branch = n;}
        public void setSemester(String n){this.semester = n;}
        public String getName(){ return this.name;}
        public String getSgpa(){ return this.sgpa;}
        public String getBranch(){ return this.branch;}
        public String getSemester(){return this.semester;}
    }
    public ArrayList<rank_list> arr1 = new ArrayList<>();

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,rank_history.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_rank_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        int id = intent.getIntExtra("idcode", 0);
        TextView branch = (TextView) findViewById(R.id.set_branch);
        TextView sem = (TextView) findViewById(R.id.set_semester);

        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename3)));
            rank_lists = gson.fromJson(bufferedReader, new TypeToken<ArrayList<ArrayList<rank_list>>>() {
            }.getType());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        arr1.addAll(rank_lists.get(id));
        branch.setText(arr1.get(0).getBranch());
        sem.setText("Semester -> "+arr1.get(0).getSemester());
        LinearLayout body = (LinearLayout) findViewById(R.id.ranklist);
        for(int i = 0; i < arr1.size(); i++){
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setMinimumHeight(30);
            if(i%2 == 1)
                row.setBackgroundColor(Color.parseColor("#d6ebf2"));
            TextView c0 = new TextView(this);
            c0.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            c0.setText(String.format("%d", i + 1));
            TextView c1 = new TextView(this);
            c1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6f));
            c1.setText(arr1.get(i).getName());
            TextView c2 = new TextView(this);
            c2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f));
            c2.setText(arr1.get(i).getSgpa().substring(5));
            c1.setTypeface(Typeface.DEFAULT_BOLD);
            c2.setTypeface(Typeface.DEFAULT_BOLD);
            c0.setTypeface(Typeface.DEFAULT_BOLD);
            c2.setGravity(Gravity.END);
            row.addView(c0);
            row.addView(c1);
            row.addView(c2);
            body.addView(row);
        }
    }
}
