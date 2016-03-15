package vozille.myapplication;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;


public class MyActivity extends AppCompatActivity {
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
    public String filename = "data.json";
    public String filename2 = "keys.json";
    public ArrayList<ArrayList<String>> subject_grade = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> subject_code = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> subject_credit = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> subject_name = new ArrayList<ArrayList<String>>();
    public ArrayList<String> details = new ArrayList<>();
    public ArrayList<student_key> arr2;
    public Bundle data = new Bundle();
    public Intent action1;
    public Button display, compute;
    public AlertDialog.Builder alert_calliberation;
    public AlertDialog.Builder alert_data_error;
    public boolean flag = false;
    public boolean flag2 = false;
    public boolean f3 = false;
    student_key student_key1 = new student_key();

    // FIXME: 25-02-2016 this just joins the query string for scraping

    public ArrayList<String> format_data(Document page, String first, String last, String code) {
        ArrayList<String> s = new ArrayList<String>();
        s.add(code);
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


    // FIXME: 25-02-2016 this deletes the back paper slots after merging
    public void delete_after_merge(ArrayList<String> arr, ArrayList<ArrayList<String>> res) {
        for (String i : arr) {
            int j = 0;
            while (j < res.size()) {
                if (res.get(j).contains(i)) {
                    res.remove(j);
                } else {
                    j++;
                }
            }
        }
    }

    // FIXME: 25-02-2016 this merges back results with main results

    public void manage_backs() {
        try {
            /*
            Gets subject code of last element added
            compares it with remaining ones
            find the one with larger size, the smaller size will be back paper result
            update results of elements from smaller to larger
             */
            int index = 0, newindex, gradeidx = 0, gradeidxold;
            String newgrade = "";
            ArrayList<String> marked_id = new ArrayList<>();
            for (int i = 0; i < subject_code.size(); i++) {
                for (int j = 0; j < subject_code.size(); j++) {
                    if (i == j)
                        continue;
                    for (int k = 1; k < subject_code.get(i).size() - 1; k++) {
                        if (subject_code.get(j).contains(subject_code.get(i).get(k)) && subject_code.get(j).size() > subject_code.get(i).size()) {
                            marked_id.add(subject_code.get(i).get(0));
                            // get back paper grade
                            for (int l = 0; l < subject_grade.size(); l++) {
                                if (subject_grade.get(l).get(0).equals(subject_code.get(i).get(0))) {
                                    newgrade = subject_grade.get(l).get(k);
                                    break;
                                }
                            }
                            for (int l = 0; l < subject_grade.size(); l++) {
                                if (subject_grade.get(l).get(0).equals(subject_code.get(j).get(0))) {
                                    index = l;
                                    break;
                                }
                            }
                            gradeidx = subject_code.get(j).indexOf(subject_code.get(i).get(k));
                            subject_grade.get(index).set(gradeidx, newgrade);
                        }
                    }
                }
            }
            delete_after_merge(marked_id, subject_code);
            delete_after_merge(marked_id, subject_credit);
            delete_after_merge(marked_id, subject_grade);
            delete_after_merge(marked_id, subject_name);
        }
        catch (Exception e){
            subject_code.clear();
            subject_credit.clear();
            subject_grade.clear();
            subject_name.clear();
        }
    }


    // FIXME: 25-02-2016 the main window
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        f3 = false;
        super.onCreate(savedInstanceState);
        flag = false;
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Get Results ");
        arr2 = new ArrayList<>();

        display = (Button) findViewById(R.id.button_disp);
        compute = (Button) findViewById(R.id.button_send);
        compute.setText(R.string.send_button);
        compute.setTypeface(null, Typeface.BOLD);

        alert_calliberation = new AlertDialog.Builder(this);
        alert_calliberation.setMessage("App has not been caliberated.\nClick on Caliberate button");
        alert_calliberation.setCancelable(true);
        alert_calliberation.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                go_back();
            }
        });

        alert_data_error = new AlertDialog.Builder(this);
        alert_data_error.setMessage("Data fetching error due to one of the following:" +
                "\n* BPUT site is down\n* Invalid roll number\n* Slow Internet Connection");
        alert_data_error.setCancelable(true);
        alert_data_error.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }

    void set_fail_notif() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notify1 = new Notification.Builder(this);
        notify1.setContentTitle("Analyzer");
        notify1.setContentText("Result Generation Unsucessful");
        notify1.setSmallIcon(R.drawable.ic_launcher1);

        Intent resultIntent = new Intent(this, MyActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notify1.setContentIntent(resultPendingIntent);
        notificationManager.notify(0,notify1.build());
    }

    void set_pass_notif() {
        manage_backs();
        sort_data();
        if(subject_code.size() == subject_credit.size() && subject_credit.size() == subject_grade.size() && subject_grade.size() == subject_name.size()){

        }
        else{
            subject_code.clear();
            subject_credit.clear();
            subject_grade.clear();
            subject_name.clear();
            AlertDialog a = alert_data_error.create();
            a.show();
            set_fail_notif();
            return;
        }
        if(subject_code.size() == 0){
            AlertDialog a = alert_data_error.create();
            set_fail_notif();
            a.show();
            return;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notify1 = new Notification.Builder(this);
        notify1.setContentTitle("Analyzer");
        notify1.setContentText("Result Generated");
        notify1.setSmallIcon(R.drawable.ic_launcher1);
        Intent action1 = new Intent(this, DispalayMsgActivity.class);

        action1.putExtras(data);
        action1.putExtra("subject_grade", subject_grade);
        action1.putExtra("subject_code", subject_code);
        action1.putExtra("subject_credit", subject_credit);
        action1.putExtra("subject_name", subject_name);


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
        try {
            student student1 = new student();
            student1.setName(data.getString("name"));
            student1.setRoll(data.getString("roll"));
            student1.setBranch(data.getString("branch"));
            student1.setCollege(data.getString("college"));
            student1.setSubject_credit(subject_credit);
            student1.setSubject_code(subject_code);
            student1.setSubject_grade(subject_grade);
            student1.setSubject_name(subject_name);
            ArrayList<String> roll_present = new ArrayList<>();
            for (int i = 0; i < all_students.size(); i++) {
                roll_present.add(all_students.get(i).get_details().get(1));
            }
            //update previous encounter with this
            if (roll_present.contains(data.getString("roll"))) {
                String r = data.getString("roll");
                int index = roll_present.indexOf(r);
                all_students.get(index).setName(data.getString("name"));
                all_students.get(index).setRoll(data.getString("roll"));
                all_students.get(index).setBranch(data.getString("branch"));
                all_students.get(index).setCollege(data.getString("college"));
                all_students.get(index).getSubject_grade().clear();
                all_students.get(index).getSubject_code().clear();
                all_students.get(index).getSubject_credit().clear();
                all_students.get(index).getSubject_name().clear();
                all_students.get(index).setSubject_credit(subject_credit);
                all_students.get(index).setSubject_code(subject_code);
                all_students.get(index).setSubject_grade(subject_grade);
                all_students.get(index).setSubject_name(subject_name);
            } else {
                all_students.add(0, student1);
            }
            Gson gson = new Gson();
            String json = gson.toJson(all_students);
            FileOutputStream fileOutputStream = openFileOutput(filename, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename2)));
            arr2 = gson.fromJson(bufferedReader, new TypeToken<ArrayList<student_key>>() {
            }.getType());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            arr2.clear();
        }
        int pos = -1;
        for(int i = 0; i < arr2.size(); i++){
            if(arr2.get(i).getRoll().equals(student_key1.getRoll())){
                pos = i;
            }
        }
        if(pos == -1) {
            arr2.add(student_key1);
        }
        else{
            if(arr2.get(pos).getCodes().size() < student_key1.getCodes().size()){
                arr2.remove(pos);
                arr2.add(student_key1);
            }
        }
        try{
            Gson gson = new Gson();
            String json = gson.toJson(arr2);
            FileOutputStream fileOutputStream = openFileOutput(filename2, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(action1);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notify1.setContentIntent(resultPendingIntent);
        notificationManager.notify(0, notify1.build());
    }

    // FIXME: 25-02-2016 sorts data

    public void sort_data() {

        try{
            student_key1.setRoll(data.getString("roll"));
            student_key1.setBranch(data.getString("branch"));
        }
        catch (Exception e){

        }
        Collections.sort((subject_grade), new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> lhs, ArrayList<String> rhs) {
                return Integer.parseInt(lhs.get(0)) - Integer.parseInt(rhs.get(0));
            }
        });
        Collections.sort((subject_code), new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> lhs, ArrayList<String> rhs) {
                return Integer.parseInt(lhs.get(0)) - Integer.parseInt(rhs.get(0));
            }
        });
        Collections.sort((subject_credit), new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> lhs, ArrayList<String> rhs) {
                return Integer.parseInt(lhs.get(0)) - Integer.parseInt(rhs.get(0));
            }
        });
        Collections.sort((subject_name), new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> lhs, ArrayList<String> rhs) {
                return Integer.parseInt(lhs.get(0)) - Integer.parseInt(rhs.get(0));
            }
        });
        try {
            for (int i = 0; i < subject_grade.size(); i++) {
                student_key1.setCode(subject_grade.get(i).get(0));
                subject_grade.get(i).remove(0);
            }
            for (int i = 0; i < subject_code.size(); i++) {
                subject_code.get(i).remove(0);
            }
            for (int i = 0; i < subject_credit.size(); i++) {
                subject_credit.get(i).remove(0);
            }
            for (int i = 0; i < subject_name.size(); i++) {
                subject_name.get(i).remove(0);
            }
        }
        catch (Exception e){
            return;
        }
        arr2.add(student_key1);
    }

    // FIXME: 25-02-2016 this sends the processed data to the display

    public void senddata(View view) throws IOException {
        subject_name.clear();
        subject_credit.clear();
        subject_code.clear();
        subject_grade.clear();
        data.clear();
        action1 = new Intent(this, DispalayMsgActivity.class);
        Vector<String> sems = new Vector<>();
        Vector<String> backsems = new Vector<>();
        EditText edit1 = (EditText) findViewById(R.id.inputRoll);
        String roll = edit1.getText().toString();
        new WebScraper(roll, sems, backsems).execute();
    }

    public void go_back() {
        super.onBackPressed();
    }
    // FIXME: 20-02-2016 result

    public void view_result(View view) {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.cancel(0);


        if(subject_code.size() == subject_credit.size() && subject_credit.size() == subject_grade.size() && subject_grade.size() == subject_name.size()){

        }
        else{
            subject_code.clear();
            subject_credit.clear();
            subject_grade.clear();
            subject_name.clear();
            AlertDialog a = alert_data_error.create();
            a.show();
            set_fail_notif();
            return;
        }
        if(subject_code.size() == 0){
            AlertDialog a = alert_data_error.create();
            set_fail_notif();
            a.show();
            return;
        }

        action1.putExtras(data);
        action1.putExtra("subject_grade", subject_grade);
        action1.putExtra("subject_code", subject_code);
        action1.putExtra("subject_credit", subject_credit);
        action1.putExtra("subject_name", subject_name);

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
        try {
            student student1 = new student();
            student1.setName(data.getString("name"));
            student1.setRoll(data.getString("roll"));
            student1.setBranch(data.getString("branch"));
            student1.setCollege(data.getString("college"));
            student1.setSubject_credit(subject_credit);
            student1.setSubject_code(subject_code);
            student1.setSubject_grade(subject_grade);
            student1.setSubject_name(subject_name);
            ArrayList<String> roll_present = new ArrayList<>();
            for (int i = 0; i < all_students.size(); i++) {
                roll_present.add(all_students.get(i).get_details().get(1));
            }
            //update previous encounter with this
            if (roll_present.contains(data.getString("roll"))) {
                String r = data.getString("roll");
                int index = roll_present.indexOf(r);
                all_students.get(index).setName(data.getString("name"));
                all_students.get(index).setRoll(data.getString("roll"));
                all_students.get(index).setBranch(data.getString("branch"));
                all_students.get(index).setCollege(data.getString("college"));
                all_students.get(index).getSubject_grade().clear();
                all_students.get(index).getSubject_code().clear();
                all_students.get(index).getSubject_credit().clear();
                all_students.get(index).getSubject_name().clear();
                all_students.get(index).setSubject_credit(subject_credit);
                all_students.get(index).setSubject_code(subject_code);
                all_students.get(index).setSubject_grade(subject_grade);
                all_students.get(index).setSubject_name(subject_name);
            } else {
                all_students.add(0, student1);
            }
            Gson gson = new Gson();
            String json = gson.toJson(all_students);
            FileOutputStream fileOutputStream = openFileOutput(filename, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename2)));
            arr2 = gson.fromJson(bufferedReader, new TypeToken<ArrayList<student_key>>() {
            }.getType());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            arr2.clear();
        }
        ArrayList<String> rolls = new ArrayList<>();
        for(int i = 0; i < arr2.size(); i++){
            rolls.add(arr2.get(i).getRoll());
        }
        if(! rolls.contains(student_key1.getRoll())) {
            arr2.add(student_key1);
        }
        try{
            Gson gson = new Gson();
            String json = gson.toJson(arr2);
            FileOutputStream fileOutputStream = openFileOutput(filename2, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            System.out.println(arr2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        startActivity(action1);
    }


    private class WebScraper extends AsyncTask<Void, Void, Void> {
        private String roll;
        private Vector<String> sem = new Vector<>();
        //TODO add backsem useage
        private Vector<String> backsem = new Vector<>();

        public WebScraper(String roll1, Vector<String> sem1, Vector<String> backsem1) {
            this.roll = roll1;
            this.sem.addAll(sem1);
            this.backsem.addAll(backsem1);
        }

        protected void onPreExecute() {
            compute = (Button) findViewById(R.id.button_send);
            compute.setText("Computing");
            compute.setTypeface(null, Typeface.BOLD);
            compute.setBackgroundResource(R.color.compute_processing);
            flag2 = true;
        }

        protected Void doInBackground(Void... params) {
            //TODO degug = true line below
//            int d = 0;
//            if(d == 0)
//                return null;
            if (this.sem.size() > 0) {
                System.out.println(this.sem.size());
                while (!this.sem.isEmpty()) {
                    try {
                        Document page = Jsoup.connect("http://results.bput.ac.in/" + this.sem.elementAt(0) + "_RES/" + this.roll + ".html").get();
                        ArrayList<String> first = new ArrayList<String>();
                        ArrayList<String> last = new ArrayList<String>();
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");

                        last.add(") > td:nth-child(5)");
                        last.add(") > td:nth-child(2)");
                        last.add(") > td:nth-child(4)");
                        last.add(") > td:nth-child(3)");

                        subject_grade.add(format_data(page, first.get(0), last.get(0), this.sem.elementAt(0)));
                        subject_code.add(format_data(page, first.get(1), last.get(1), this.sem.elementAt(0)));
                        subject_credit.add(format_data(page, first.get(2), last.get(2), this.sem.elementAt(0)));
                        subject_name.add(format_data(page, first.get(3), last.get(3), this.sem.elementAt(0)));
                        subject_name.get(subject_name.size() - 1).remove(subject_name.get(subject_name.size() - 1).size() - 1);

                        if (this.sem.size() == 1) {
                            data.putString("name", page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(2) > td:nth-child(2) > b").text());
                            data.putString("branch", page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > b").text());
                            data.putString("college", page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(3) > td:nth-child(2) > b").text());
                            data.putString("roll", roll);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.sem.remove(0);
                }
            } else {

                ArrayList<urlthread> urlthreads = new ArrayList<>();
                int ctr = 100;
                SharedPreferences preferences = getSharedPreferences("myappdata", MODE_PRIVATE);
                int MAX_LIMIT = preferences.getInt("limit", 0);
                if (MAX_LIMIT == 0) {
                    flag = true;
                    System.out.println(MAX_LIMIT);
                    return null;
                }
                System.out.println(MAX_LIMIT);
                while (ctr <= MAX_LIMIT) {
                    urlthread thread = new urlthread(ctr, ctr + 30, this.roll);
                    urlthreads.add(thread);
                    ctr += 30;
                }

                for (urlthread i : urlthreads) {
                    i.start();
                }

                for (urlthread i : urlthreads) {
                    if (f3)
                        return null;
                    try {
                        i.join();
                    } catch (InterruptedException e) {
                        System.out.println("sexy");
                        subject_grade.clear();
                        System.out.println(e);
                        break;
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Void res) {
            flag2 = false;
            System.out.println(subject_grade.size());
            if (flag) {
                set_fail_notif();

                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                v.vibrate(1000);
                AlertDialog a = alert_calliberation.create();
                a.show();
                return;
            }
            if (subject_grade.size() == 0) {
                set_fail_notif();
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                v.vibrate(1000);
                AlertDialog a = alert_data_error.create();
                a.show();
                return;
            }
            display = (Button) findViewById(R.id.button_disp);
            compute = (Button) findViewById(R.id.button_send);
            compute.setEnabled(false);
            compute.setVisibility(View.GONE);
            display.setVisibility(View.VISIBLE);
            System.out.println("Executed bitches");
            set_pass_notif();
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(1000);
        }
    }

    public class urlthread extends Thread {
        String roll;
        private boolean flag = true;
        private int sta, end;

        public urlthread(int s, int e, String roll) {
            this.sta = s;
            this.roll = roll;
            this.end = e;
        }

        @Override
        public void run() {
            for (int i = this.sta; i < this.end; i++) {
                String a = Integer.toString(i);
                String b = this.roll;

                try {
                    URL addr = new URL("http://results.bput.ac.in/" + a + "_RES/" + b + ".html");
                    HttpURLConnection conn = (HttpURLConnection) addr.openConnection();
                    conn.setRequestMethod("HEAD");
                    int res = conn.getResponseCode();
                    conn.disconnect();
                    if (res == 200) {
                        Document page = Jsoup.connect("http://results.bput.ac.in/" + a + "_RES/" + b + ".html").get();

                        if (this.flag) {
                            data.putString("name", page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(2) > td:nth-child(2) > b").text());
                            data.putString("branch", page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(4) > td:nth-child(2) > b").text());
                            data.putString("college", page.select("body > table > tbody > tr:nth-child(3) > td > table > tbody > tr:nth-child(3) > td:nth-child(2) > b").text());
                            data.putString("roll", roll);
                            details.add(data.getString("name"));
                            details.add(data.getString("branch"));
                            details.add(data.getString("college"));
                            details.add(data.getString("roll"));
                            this.flag = false;
                        }

                        ArrayList<String> first = new ArrayList<String>();
                        ArrayList<String> last = new ArrayList<String>();
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");
                        first.add("body > table > tbody > tr:nth-child(5) > td > table > tbody > tr:nth-child(");

                        last.add(") > td:nth-child(5)");
                        last.add(") > td:nth-child(2)");
                        last.add(") > td:nth-child(4)");
                        last.add(") > td:nth-child(3)");

                        subject_grade.add(format_data(page, first.get(0), last.get(0), a));
                        subject_code.add(format_data(page, first.get(1), last.get(1), a));
                        subject_credit.add(format_data(page, first.get(2), last.get(2), a));
                        subject_name.add(format_data(page, first.get(3), last.get(3), a));
                        subject_name.get(subject_name.size() - 1).remove(subject_name.get(subject_name.size() - 1).size() - 1);
                        System.out.println(subject_code);
                        System.out.println(subject_credit);
                        System.out.println(subject_grade);
                        System.out.println(subject_name);
                    }

                } catch (Exception e) {
                    f3 = true;
                    subject_grade.clear();
                    break;
                }
            }
        }
    }

}