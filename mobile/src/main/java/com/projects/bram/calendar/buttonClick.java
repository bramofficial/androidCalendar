/*
package com.projects.bram.calendar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class buttonClick extends AppCompatActivity {

    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_click);

        calendarView = findViewById(R.id.calendarView);
        final TextView textView = findViewById(R.id.textView);

        //Creates a listener to see when the date selected has been changed.
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            /* i = year || i1 = month || i2 = day
               month seems to start from 0
               This code is ran when we select a new date.
               we need to start a new Intent and send through the date and add new goals on the
               new intent

            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                textView.setText("Date: " + i2 + " / " + (i1 + 1) + " / " + i);

                Toast.makeText(getApplicationContext(), "Selected Date:\n"+ "Day = " + i2 + "\n" + "Month = " + (i1 + 1) + "\n" + "Year = " + i, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(buttonClick.this, ToDoActivity.class);
                //This sends through our date from one activity to another
                intent.putExtra("Date", "" + (i1 + 1) + i2 + i + "");
                startActivity(intent);
            }});
        }
}
*/

