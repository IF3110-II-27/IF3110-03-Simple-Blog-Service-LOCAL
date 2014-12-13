package id.ac.itb.informatika.wbd.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateHelper {
    private String currentDate;
    
    public DateHelper(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = new java.util.Date();
        currentDate = dateFormat.format(date);
    }

    public String getCDate() {
        return currentDate;
    }

    public void setCDate(String cDate) {
        this.currentDate = cDate;
    }
}
