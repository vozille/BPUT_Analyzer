package vozille.myapplication;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class rank_history extends AppCompatActivity {
    public String filename3 = "ranklists.json";
    public AlertDialog.Builder clear;
    public ArrayList<ArrayList<rank_list>> rank_lists = new ArrayList<>();

    class rank_list {
        String name = "";
        String sgpa = "";
        String branch = "";
        String semester = "";

        public void setName(String n) {
            this.name = n;
        }

        public void setSgpa(String n) {
            this.sgpa = n;
        }

        public void setBranch(String n) {
            this.branch = n;
        }

        public void setSemester(String n) {
            this.semester = n;
        }

        public String getName() {
            return this.name;
        }

        public String getSgpa() {
            return this.sgpa;
        }

        public String getBranch() {
            return this.branch;
        }

        public String getSemester() {
            return this.semester;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, rank_sem.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Rank-list History");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        clear = new AlertDialog.Builder(this);
        clear.setMessage("The entire history will be erased.\nAre you sure ?");
        clear.setCancelable(true);
        clear.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clear_rankhistory();
            }
        });
        clear.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(filename3)));
            rank_lists = gson.fromJson(bufferedReader, new TypeToken<ArrayList<ArrayList<rank_list>>>() {
            }.getType());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearLayout body = (LinearLayout) findViewById(R.id.ranklisthistory);
        for (int i = 0; i < rank_lists.size(); i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (i % 2 == 1)
                row.setBackgroundColor(Color.parseColor("#d6ebf2"));
            TextView c = new TextView(this);
            row.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c.setTypeface(Typeface.DEFAULT_BOLD);
            c.setTextSize(20);
            c.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c.setText("Ranklist - " + String.format("%d", i + 1) + "\n" + rank_lists.get(i).get(0).getBranch() + ": Semester-> " + rank_lists.get(i).get(0).getSemester() + "\n" + rank_lists.get(i).get(0).getName() + "...");
            c.setId(i);
            final int id = i;
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_ranklist(id);
                }
            });
            row.addView(c);
            body.addView(row);
        }
        Button b = new Button(this);
        b.setText("Clear History");
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setTextSize(15);
        b.setBackgroundResource(R.color.colorAccent);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ranklist_clear_dialog();
            }
        });
        body.addView(b);
    }

    public void show_ranklist(int id) {
        Intent action1 = new Intent(this, display_rank_list.class);
        action1.putExtra("idcode", id);
        startActivity(action1);
    }

    public void ranklist_clear_dialog() {
        AlertDialog a = clear.create();
        a.show();
    }

    public void clear_rankhistory() {
        rank_lists.clear();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(rank_lists);
            FileOutputStream fileOutputStream = openFileOutput(filename3, MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, rank_history.class);
        startActivity(intent);
    }
}
