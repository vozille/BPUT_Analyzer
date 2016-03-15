package vozille.myapplication;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class rank_sem extends AppCompatActivity {

    public String filename2 = "keys.json";
    public String filename3 = "ranklists.json";
    public float cnt = 0;
    public float tot = 1200;
    public Spinner p1;
    public Spinner p2;
    public int lowlim,le_lowlimit;
    public String sem_code = "";
    public String branch_1 = "";
    public final ArrayList<ArrayList<String>> codes =  new ArrayList<>();
    ArrayList<String> rolls = new ArrayList<>();
    public ArrayList<ArrayList<rank_list>> rank_lists = new ArrayList<>();
    public ProgressBar progressBar;
    public AlertDialog.Builder alert_data_error;
    public AlertDialog.Builder alert_stop_execution;
    public AsyncTask scraper;

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


    @Override
    public void onBackPressed(){
        Button b = (Button) findViewById(R.id.rankhistory);
        if(b.getVisibility() == View.INVISIBLE){
            AlertDialog a = alert_stop_execution.create();
            a.show();
        }
        else {
            Intent intent = new Intent(this, welcome.class);
            startActivity(intent);
        }
    }

    class student_key{
        String roll = "";
        String branch = "";
        ArrayList<String> codes = new ArrayList<>();
        public void setRoll(String n){ this.roll = n;}
        public void setCode(String n){ this.codes.add(n); }
        public void setBranch(String n){this.branch = n;}
        public String getRoll(){
            return this.roll;
        }
        public String getBranch(){
            return this.branch;
        }
        public ArrayList<String> getCodes(){
            return this.codes;
        }
    }
    class student_data
    {
        String name = "";
        String roll = "";
        String sgpa = "";
        public void setName(String n){
            this.name = n;
        }
        public void setRoll(String n){ this.roll = n; }
        public void setSgpa(String n){ this.sgpa = n; }

        public String getName(){return this.name;}
        public String getRoll(){return this.roll;}
        public String getSgpa(){return this.sgpa;}

    }
    public ArrayList<String> format_data(Document page, String first,String last) {
        ArrayList<String> s = new ArrayList<String>();
        int i = 1;
        while (true) {
            String res = page.select(first + Integer.toString(i) + last).text();
            if ("".equals(res)) {
                break;
            }
            s.add(res);
            i += 1;
        }
        s.remove(1);
        return s;
    }
    ArrayList<student_data> arr;
    ArrayList<student_key>arr2;
    public String code;
    public String code_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_sem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Rank List Home");

        arr = new ArrayList<>();
        arr2 = new ArrayList<>();
        codes.clear();
        rolls.clear();
        rank_lists.clear();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setProgress(0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        alert_data_error = new AlertDialog.Builder(this);
        alert_data_error.setMessage("Data fetching error due to one of the following:" +
                "\n* BPUT site is down\n* Invalid roll number\n* Slow Internet Connection");
        alert_data_error.setCancelable(true);
        alert_data_error.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
            }
        });

        alert_stop_execution = new AlertDialog.Builder(this);
        alert_stop_execution.setMessage("The execution will be stopped midway and all data obtained will be lost.\nAre you sure ?");
        alert_stop_execution.setCancelable(true);
        alert_stop_execution.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert_stop_execution.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scraper.cancel(true);
                reset_computation();
            }
        });


        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename2)));
            arr2 = gson.fromJson(bufferedReader, new TypeToken<ArrayList<student_key>>() {
            }.getType());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename3)));
            rank_lists = gson.fromJson(bufferedReader, new TypeToken<ArrayList<ArrayList<rank_list>>>() {
            }.getType());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i < arr2.size(); i++){
            rolls.add(arr2.get(i).getRoll());
            codes.add(arr2.get(i).getCodes());
        }
        p1 = (Spinner) findViewById(R.id.ranklistroll);
        p2 = (Spinner) findViewById(R.id.ranklistcode);
        ArrayAdapter<String> a1 = new ArrayAdapter<>(this, R.layout.spinner_compare_right,rolls);
        a1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        p1.setAdapter(a1);
        p1.setSelection(-1);
        p1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> curr_codes = new ArrayList<String>();
                try {
                    for (int i = 1; i <= codes.get(position).size(); i++) {
                        curr_codes.add("Semester - " + String.format("%d", i));
                    }
                    ArrayAdapter<String> a2 = new ArrayAdapter<String>(rank_sem.this, R.layout.spinner_compare_right, curr_codes);
                    a2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    p2.setAdapter(a2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void reset_computation(){
        Intent intent = new Intent(this, welcome.class);
        startActivity(intent);
    }

    public void reset(){
        Intent intent = new Intent(this,rank_sem.class);
        startActivity(intent);
    }

    public void get_rank_list(View view){
        if(p1.getSelectedItemPosition() >= 0 && p2.getSelectedItemPosition() >= 0){
            Button b2 = (Button) findViewById(R.id.getranklist);
            b2.setText("Fetching Data");
            Button b3 = (Button) findViewById(R.id.rankhistory);
            b3.setVisibility(View.INVISIBLE);
            b2.setBackgroundResource(R.color.compute_processing);
            sem_code = codes.get(p1.getSelectedItemPosition()).get(p2.getSelectedItemPosition());
            System.out.println(sem_code);
            double temp = Integer.parseInt(rolls.get(p1.getSelectedItemPosition()));
            lowlim = ((int) (temp / 1000)) * 1000;
            le_lowlimit = lowlim + 120000000;
            System.out.println(lowlim);
            scraper = new  WebScraper().execute();
        }
        else{
            AlertDialog a = alert_data_error.create();
            a.setMessage("Select the roll number and the semester");
            a.show();
        }
    }

    public void view_rank_list(View view){
        System.out.println("sending data");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if(arr.size() == 0){
            AlertDialog a = alert_data_error.create();
            a.show();
            return;
        }
        Intent action1 = new Intent(this, display_rank_list.class);
        try{
            Gson gson = new Gson();
            String json = gson.toJson(rank_lists);
            FileOutputStream fileOutputStream = openFileOutput(filename3, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        }
        catch (Exception e){

        }
        action1.putExtra("idcode", rank_lists.size() - 1);
        startActivity(action1);
    }

    public void rank_history(View view){
        Intent intent = new Intent(this, rank_history.class);
        startActivity(intent);
    }

    private class WebScraper extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            code = arr2.get(p1.getSelectedItemPosition()).getCodes().get(p2.getSelectedItemPosition());
            code_id = String.format("%d",p2.getSelectedItemPosition() + 1);
            ArrayList<urlthread> u = new ArrayList<>();
            for(int i = lowlim; i < lowlim + 965; i+= 30){
                urlthread t = new urlthread(i,i+30,code);
                u.add(t);
            }
            for(int i = le_lowlimit; i < le_lowlimit + 200; i+= 30){
                urlthread t = new urlthread(i,i+30,code);
                u.add(t);
            }
            for(urlthread i : u){
                i.start();
            }
            for(urlthread i : u){
                try{
                    i.join();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void res) {

            Button b1 = (Button) findViewById(R.id.showranklist);
            b1.setVisibility(View.VISIBLE);
            Button b2 = (Button) findViewById(R.id.getranklist);
            b2.setVisibility(View.INVISIBLE);
            Button b3 = (Button) findViewById(R.id.rankhistory);
            b3.setVisibility(View.VISIBLE);
            Collections.sort(arr, new Comparator<student_data>() {
                @Override
                public int compare(student_data lhs, student_data rhs) {
                    return -(lhs.getSgpa().compareTo(rhs.getSgpa()));
                }
            });
            // move to some other place
            ArrayList<rank_list> newranks = new ArrayList<>();

            //TODO handle exception

            if(arr.size() == 0){
                AlertDialog a = alert_data_error.create();
                a.show();
                return;
            }

            for(student_data i : arr){
                rank_list r1 = new rank_list();
                r1.setName(i.getName());
                r1.setSgpa(i.getSgpa());
                r1.setBranch(branch_1);
                r1.setSemester(code_id);
                newranks.add(r1);
                System.out.println(i.getName() + "   -   "+i.getSgpa() + "  " + i.getRoll());
            }
            rank_lists.add(newranks);
            try{
                Gson gson = new Gson();
                String json = gson.toJson(rank_lists);
                FileOutputStream fileOutputStream = openFileOutput(filename3, MODE_PRIVATE);
                fileOutputStream.write(json.getBytes());
                fileOutputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(1000);
        }
    }
    public class urlthread extends Thread {
        String code;
        private boolean flag = true;
        private int sta, end;

        public urlthread(int s, int e, String roll) {
            this.sta = s;
            this.code = sem_code;
            this.end = e;
        }

        @Override
        public void run() {
            for (int i = this.sta; i < this.end; i++) {
                String a = Integer.toString(i);
                String b = this.code;

                try {
                    URL addr = new URL("http://results.bput.ac.in/" + b + "_RES/" + a + ".html");
                    HttpURLConnection conn = (HttpURLConnection) addr.openConnection();
                    conn.setRequestMethod("HEAD");
                    int res = conn.getResponseCode();
                    conn.disconnect();
                    cnt+=1;
                    if (res == 200) {
                        Document page = Jsoup.connect("http://results.bput.ac.in/" + b + "_RES/" + a + ".html").get();
                        String branch = page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > b").text();
                        String name = (page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(2) > td:nth-child(2) > b").text());
                        ArrayList<String> foo = format_data(page, "body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(",") > td:nth-child(3)");

                        if(branch_1.length() == 0){
                            branch_1 = arr2.get(p1.getSelectedItemPosition()).getBranch();
                        }
                        if(branch.equalsIgnoreCase(branch_1)){
                            System.out.println(name + a);
                            student_data s = new student_data();
                            s.setName(name);
                            s.setRoll(a);
                            s.setSgpa(foo.get(foo.size() - 1));
                            arr.add(s);
                        }
                        int increment = Integer.parseInt(String.format("%.0f", (cnt*100) / tot));
                        progressBar.setProgress(increment);
                    }
                    else{
                        int increment = Integer.parseInt(String.format("%.0f", (cnt*100) / tot));
                        progressBar.setProgress(increment);
                    }
                } catch (Exception e) {
                    System.out.println("failed " + i);
                }
            }
        }
    }
}
