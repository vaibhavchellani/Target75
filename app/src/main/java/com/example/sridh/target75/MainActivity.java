package com.example.sridh.target75;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sridh.target75.database.AttendanceContract;
import com.example.sridh.target75.database.AttendanceDBHelper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AttendanceRecyclerAdapter.ListItemClickListener{

    private TextView textView;
    private RecyclerView rv;
    private SQLiteDatabase database,database_writable;
    private AttendanceRecyclerAdapter adapter;
    private Cursor cursor;
    private SharedPreferences sharedPreferences;
    private static TimePicker tp;
    private static View v;
    private static int mHour;
    private static int mMinute;
    private final static Calendar mCalendar = Calendar.getInstance();
    private  AttendanceDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.rv_item);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        rv.setHasFixedSize(true);
        dbHelper = new AttendanceDBHelper(this);
        database_writable = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();

        database = dbHelper.getReadableDatabase();
        cursor = database.rawQuery("SELECT * FROM " + AttendanceContract.AttendanceEntry.TABLE_NAME, null);
        adapter =new AttendanceRecyclerAdapter(this,cursor,this);

        rv.setAdapter(adapter);
        rv.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int item_id = item.getItemId();
        if (item_id == R.id.new_subject) {
            startActivity(new Intent(MainActivity.this, NewSubjectActivity.class));
            return true;
        }

        else if(item_id==R.id.reminder_time){
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "Time Picker");
        }

        else if(item_id==R.id.reset_database){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Do you want to reset?");
            alertDialog.setTitle("Warning!");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    database_writable.delete(AttendanceContract.AttendanceEntry.TABLE_NAME,null,null);
                    cursor = database.rawQuery("SELECT * FROM " + AttendanceContract.AttendanceEntry.TABLE_NAME, null);
                    adapter =new AttendanceRecyclerAdapter(MainActivity.this,cursor,MainActivity.this);

                   rv.setAdapter(adapter);

                }
            });

            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);
            int month = mCalendar.get(Calendar.MONTH);

            Log.d("Month", Integer.toString(month));

            TimePickerDialog view = new TimePickerDialog(getActivity(),this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            LayoutInflater inflater = getActivity().getLayoutInflater();

            v=inflater.inflate(R.layout.time_picker, null);
            view.setView(v);

            tp = (TimePicker) v.findViewById(R.id.tpUser);

            return view;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {

            boolean isDoneButtonChecked = true;

            mHour = hourOfDay;
            mMinute = minutes;
          //  mHour=view.getHour();

        }

    }

        @Override
    public void onListItemClick(int clickeditemindex) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt("gridview_item",clickeditemindex).commit();
        startActivity(new Intent(MainActivity.this, StatsActivity.class));

        }

}