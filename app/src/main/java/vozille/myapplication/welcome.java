package vozille.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class welcome extends AppCompatActivity {

    public String filename = "data.json";
    public int LIMIT = 640;
    public Button button;
    public boolean somewhere_else = false;
    public Bundle data = new Bundle();
    public boolean flag;
    AlertDialog.Builder done;
    AlertDialog.Builder notdone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = true;
        somewhere_else = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        button = (Button) findViewById(R.id.caliberate);
        done = new AlertDialog.Builder(this);
        done.setMessage("Calibration was successful");
        done.setCancelable(true);
        done.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        notdone = new AlertDialog.Builder(this);
        notdone.setMessage("Calibration Failure.\nThe results site is unreachable or is responding too slowly");
        notdone.setCancelable(true);
        notdone.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (somewhere_else) {
            setContentView(R.layout.activity_welcome);
            somewhere_else = false;
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void enter_request(View view) throws IOException {
        Intent action = new Intent(this, MyActivity.class);
        startActivity(action);
    }

    public void enter_history(View view) throws IOException {
        Intent action = new Intent(this, history.class);
        startActivity(action);
    }

    public String name_formatter(String name) {
        String res = "";
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ') {
                break;
            }
            res += name.charAt(i);
        }
        return res;
    }

    public void compare_display(View view) {
        setContentView(R.layout.content_compare_home);
        somewhere_else = true;
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
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < all_students.size(); i++) {
            names.add(name_formatter(all_students.get(i).get_details().get(0)));
        }

        Spinner p1 = (Spinner) findViewById(R.id.player1);
        Spinner p2 = (Spinner) findViewById(R.id.player2);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_compare_left, names);
        arrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_compare_right, names);
        arrayAdapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        p1.setAdapter(arrayAdapter1);
        p2.setAdapter(arrayAdapter2);

    }

    public void compare_click(View view) {
        Spinner p1 = (Spinner) findViewById(R.id.player1);
        Spinner p2 = (Spinner) findViewById(R.id.player2);

        data.putInt("player1", p1.getSelectedItemPosition());
        data.putInt("player2", p2.getSelectedItemPosition());
        Intent intent = new Intent(this, compare_home.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    public Integer get_id(String val) {
        String res = "";
        boolean flag = false;
        for (int i = 0; i < val.length(); i++) {
            if (val.charAt(i) == '_')
                break;
            if (flag)
                res += val.charAt(i);
            if (val.charAt(i) == '/')
                flag = true;
        }
        try {
            return Integer.parseInt(res);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void caliberate(View view) throws IOException {

        button.setText(R.string.caliberationmidway);
        new WebScraper().execute();
    }

    public void rankings(View view){
        Intent intent = new Intent(this,rank_sem.class);
        startActivity(intent);
    }

    public void help(View view) {
        Intent intent = new Intent(this, aboutapp.class);
        startActivity(intent);
    }

    private class WebScraper extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            try {
                Document page = Jsoup.connect("http://results.bput.ac.in/").get();
                Elements e1 = page.select("a");
                int maxm = -1;
                for (Element i : e1) {
                    maxm = Math.max(maxm, get_id(i.attr("href")));
                }
                LIMIT = maxm;
                SharedPreferences preferences = getSharedPreferences("myappdata", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("limit", LIMIT);
                editor.apply();
                System.out.println(editor.commit());
                System.out.println(LIMIT);
            } catch (IOException e) {
                System.out.println("error connecting");
                flag = false;
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void res) {
            button.setText("Calibrate");
            if (flag == false) {
                AlertDialog a = notdone.create();
                a.show();
            } else {
                AlertDialog a = done.create();
                a.show();
            }
        }

    }
}