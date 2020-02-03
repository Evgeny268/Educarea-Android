package com.educarea.mobile;

import java.util.ArrayList;
import java.util.Calendar;

import transfers.Timetable;

public class TimetableUtils {
    public static ArrayList<Timetable> getTimetablesForDay(ArrayList<Timetable> timetables, Calendar calendar){
        ArrayList<Timetable> dayTimetable = new ArrayList<>();
        for (int i = 0; i < timetables.size(); i++) {
            Timetable current = timetables.get(i);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int weekInYear = calendar.get(Calendar.WEEK_OF_YEAR);
            int parityweek;
            if (weekInYear%2==0){
                parityweek=2;
            }else parityweek=1;
            if (current.day == dayOfWeek){
                if (current.parityweek==0){
                    dayTimetable.add(current);
                }else {
                    if (parityweek==current.parityweek){
                        dayTimetable.add(current);
                    }
                }
            }
        }
        return dayTimetable;
    }
}
