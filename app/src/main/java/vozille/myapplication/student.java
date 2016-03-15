package vozille.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class student {

    private String name,roll,branch,college;
    private ArrayList<ArrayList<String>> subject_code = new ArrayList<>();
    private ArrayList<ArrayList<String>> subject_credit = new ArrayList<>();
    private ArrayList<ArrayList<String>> subject_grade = new ArrayList<>();
    private ArrayList<ArrayList<String>> subject_name = new ArrayList<>();

    public void setName(String n){name = n;}
    public void setRoll(String n){ roll = n; }
    public void setBranch(String n){ branch = n; }
    public void setCollege(String n){ college = n; }

    public void setSubject_code(ArrayList<ArrayList<String>> n){
        subject_code.addAll(n);
    }
    public void setSubject_credit(ArrayList<ArrayList<String>> n){
        subject_credit.addAll(n);
    }
    public void setSubject_grade(ArrayList<ArrayList<String>> n){
        subject_grade.addAll(n);
    }
    public void setSubject_name(ArrayList<ArrayList<String>> n){
        subject_name.addAll(n);
    }

    public ArrayList<String> get_details(){
        ArrayList<String> res = new ArrayList<>();
        res.add(name);
        res.add(roll);
        res.add(branch);
        res.add(college);
        return res;
    }

    public ArrayList<ArrayList<String>> getSubject_code(){
        return this.subject_code;
    }
    public ArrayList<ArrayList<String>>getSubject_credit(){
        return this.subject_credit;
    }
    public ArrayList<ArrayList<String>>getSubject_grade(){
        return this.subject_grade;
    }
    public ArrayList<ArrayList<String>>getSubject_name(){
        return this.subject_name;
    }

//    public JSONObject getJSON(){
//        JSONObject object = new JSONObject();
//        try{
//            object.put("name",name);
//            object.put("roll",roll);
//            object.put("branch",branch);
//            object.put("college",college);
//            object.put("subject_code",subject_code);
//            object.put("subject_credit)",subject_credit);
//            object.put("subject_grade",subject_grade);
//            object.put("subject_name",subject_name);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return object;
//    }
}
