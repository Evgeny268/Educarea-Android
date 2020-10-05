package com.educarea.mobile;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import transfers.Timetable;

public class TimetableUtils {
    public ArrayList<Timetable> getTimetablesForDay(ArrayList<Timetable> timetables, Calendar calendar){
        ArrayList<Timetable> dayTimetable = new ArrayList<>();
        for (int i = 0; i < timetables.size(); i++) {
            Timetable current = timetables.get(i);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int parityweek;
            if (weekIsEven(calendar)){
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

    public boolean weekIsEven(Calendar calendar){
        int currentYear = calendar.get(Calendar.YEAR);
        Calendar startEducationYear = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) < 8){
            startEducationYear.set(currentYear-1, 8, 1);
        }else {
            startEducationYear.set(currentYear, 8, 1);
        }
        long weekCount = (calendar.getTime().getTime() - startEducationYear.getTime().getTime()) /  (24 * 60 * 60 * 1000 * 7);
        return weekCount % 2 == 0;
    }
}
