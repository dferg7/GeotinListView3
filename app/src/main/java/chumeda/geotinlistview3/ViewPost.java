package chumeda.geotinlistview3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by chu on 11/29/15.
 */
public class ViewPost extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextId;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private DatePicker datePickerStartDate;
    private DatePicker datePickerEndDate;
    private TimePicker timePickerStartTime;
    private TimePicker timePickerEndTime;

    private Button buttonUpdate;
    private Button buttonDelete;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        Intent intent = getIntent();

        id = intent.getStringExtra(Config.POST_ID);

        editTextId = (EditText) findViewById(R.id.editTextId);
        //initializing views
        editTextTitle = (EditText) findViewById(R.id.editTextName);
        editTextDescription = (EditText) findViewById(R.id.editTextUserName);
        datePickerStartDate = (DatePicker) findViewById(R.id.datePickerStartDate);
        datePickerEndDate = (DatePicker) findViewById(R.id.datePickerEndDate);
        timePickerStartTime = (TimePicker) findViewById(R.id.timePickerStartTime);
        timePickerEndTime = (TimePicker) findViewById(R.id.timePickerEndTime);

        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        editTextId.setText(id);

        getPost();
    }

    private void getPost() {
        class GetPost extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewPost.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                showPost(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_POST,id);
                return s;
            }
        }
        GetPost gp = new GetPost();
        gp.execute();
    }

    private void showPost(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String title = c.getString(Config.TAG_TITLE);
            String description = c.getString(Config.TAG_DESCRIPTION);

            editTextTitle.setText(title);
            editTextDescription.setText(description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePost() {
        final String title = editTextTitle.getText().toString().trim();
        final String description = editTextTitle.getText().toString().trim();
        //Dates
        int monthStart = datePickerStartDate.getMonth();
        int dayStart = datePickerStartDate.getDayOfMonth();
        int yearStart = datePickerStartDate.getYear();
        final String dateStart = String.valueOf(yearStart) + "-" + String.valueOf(monthStart) + "-" + String.valueOf(dayStart);
        int monthEnd = datePickerEndDate.getMonth();
        int dayEnd = datePickerEndDate.getDayOfMonth();
        int yearEnd = datePickerEndDate.getYear();
        final String dateEnd = String.valueOf(yearEnd) + "-" + String.valueOf(monthEnd) + "-" + String.valueOf(dayEnd);

        //Times
        int timePickerStartTimeHour = timePickerStartTime.getCurrentHour();
        int timePickerStartTimeMin = timePickerStartTime.getCurrentMinute();
        int timePickerEndTimeHour = timePickerEndTime.getCurrentHour();
        int timePickerEndTimeMin = timePickerEndTime.getCurrentMinute();
        final String timeStart = String.valueOf(timePickerStartTimeHour) + ":" + String.valueOf(timePickerStartTimeMin);
        final String timeEnd = String.valueOf(timePickerEndTimeHour) + ":" + String.valueOf(timePickerEndTimeMin);

        //Location
        double longitudeNum = 150.000;
        double latitudeNum = 150.000;
        final String longitude = String.valueOf(longitudeNum);
        final String latitude = String.valueOf(latitudeNum);

        class UpdatePost extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewPost.this,"Fetching...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewPost.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_POST_ID,id);
                hashMap.put(Config.KEY_POST_TITLE, title);
                hashMap.put(Config.KEY_POST_DESCRIPTION, description);
                hashMap.put(Config.KEY_POST_LATITUDE, latitude);
                hashMap.put(Config.KEY_POST_LONGITUDE, longitude);
                hashMap.put(Config.KEY_POST_DATE_START, dateStart);
                hashMap.put(Config.KEY_POST_TIME_START, timeStart);
                hashMap.put(Config.KEY_POST_DATE_END, dateEnd);
                hashMap.put(Config.KEY_POST_TIME_END, timeEnd);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.URL_UPDATE_POST,hashMap);

                return s;
            }
        }

        UpdatePost up = new UpdatePost();
        up.execute();
    }

    private void deletePost() {
        class DeletePost extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewPost.this,"Deleting...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewPost.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_POST_ID,id);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(Config.URL_DELETE_POST, hashMap);
                Log.d("test", "s " +s);
                return s;
            }
        }

        DeletePost dp = new DeletePost();
        dp.execute();
    }

    private void confirmDeletePost() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete this post?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deletePost();
                        startActivity(new Intent(ViewPost.this, ViewAllPosts.class));
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonUpdate) {
            updatePost();
        }
        if(v == buttonDelete) {
            confirmDeletePost();
        }
    }
}
