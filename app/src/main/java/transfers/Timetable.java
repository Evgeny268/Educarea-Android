package transfers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.time.LocalTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property ="type")
public class Timetable  implements Serializable, Transfers {
    public int timetableId = 0; //NOT NULL
    public int groupId = 0; //NOT NULL
    public int groupPersonId = 0;
    public String objectName = null; //NOT NULL
    public String cabinet = null;
    public int parityweek; //NOT NULL
    public int day; //NOT NULL
    public String time = null;

    public Timetable() {
    }

    public Timetable(int groupId, String objectName, int parityweek, int day) {
        this.groupId = groupId;
        this.objectName = objectName;
        this.parityweek = parityweek;
        this.day = day;
    }

    public Timetable(int timetableId, int groupId, String objectName, int parityweek, int day) {
        this.timetableId = timetableId;
        this.groupId = groupId;
        this.objectName = objectName;
        this.parityweek = parityweek;
        this.day = day;
    }


}
