package vozille.myapplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class history extends AppCompatActivity {

    public String filename = "data.json";
    public String filename2 = "keys.json";
    public LinearLayout body;
    public AlertDialog.Builder clear;
    public ArrayList<student> all_students = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        body = (LinearLayout) findViewById(R.id.name_list);
        clear = new AlertDialog.Builder(this);
        clear.setMessage("The entire history will be erased.\nAre you sure ?");
        clear.setCancelable(true);
        clear.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clear_history();
            }
        });
        clear.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
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
        for(int i = 0; i < all_students.size(); i++){
            set_student_data(i,all_students.get(i).get_details().get(0),all_students.get(i).get_details().get(1));
        }
        Button b = new Button(this);
        b.setText("Clear History");
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setTextSize(15);
        b.setBackgroundResource(R.color.colorAccent);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_clear_dialog();
            }
        });
        body.addView(b);
    }

    public void create_clear_dialog(){
        AlertDialog a = clear.create();
        a.show();
    }

    public void pass_data(int id){
        SharedPreferences preferences = getSharedPreferences("myappdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id", id);
        editor.apply();
        System.out.println(id);
        Intent action1 = new Intent(this, DisplayGrades.class);
        String name = all_students.get(id).get_details().get(0);
        String roll = all_students.get(id).get_details().get(1);
        String branch = all_students.get(id).get_details().get(2);
        String college = all_students.get(id).get_details().get(3);

        Bundle data = new Bundle();
        data.putString("name",name);
        data.putString("roll",roll);
        data.putString("branch",branch);
        data.putString("college",college);

        action1.putExtras(data);
        action1.putExtra("subject_grade",all_students.get(id).getSubject_grade());
        action1.putExtra("subject_code",all_students.get(id).getSubject_code());
        action1.putExtra("subject_credit",all_students.get(id).getSubject_credit());
        action1.putExtra("subject_name", all_students.get(id).getSubject_name());
        startActivity(action1);
    }

    public void set_student_data(int id,String name, String roll){
        TextView t = new TextView(this);
        t.setId(id);
        t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        t.setTextSize(15);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        if(id%2 == 1)
            t.setBackgroundResource(R.color.highlighter);
        String text = name + "\n" + roll;
        t.setText(text);
        final int pos = id;
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_data(pos);
            }
        });
        body.addView(t);
    }

    public void clear_history(){

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
        }
        all_students.clear();
        try{
            Gson gson = new Gson();
            String json = gson.toJson(all_students);
            FileOutputStream fileOutputStream = openFileOutput(filename, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            Gson gson = new Gson();
            String json = gson.toJson(all_students);
            FileOutputStream fileOutputStream = openFileOutput(filename2, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        setContentView(R.layout.activity_history);
    }
    @Override
    public void onBackPressed(){
        Intent action = new Intent(this, welcome.class);
        startActivity(action);
        super.onBackPressed();
    }

}
