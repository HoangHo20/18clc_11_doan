package com.example.imagealbum.customutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * <p>Custom comparator for sorted collection</p>
 * <p>Order: from oldest date to newest date</p>
 */
public class CustomComparatorDate implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {

        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date d_o1 = null;
        Date d_o2 = null;
        try {
            d_o1 = sourceFormat.parse(o1);
            d_o2 = sourceFormat.parse(o2);

            if (d_o1.equals(d_o2)) {
                return 0;
            } else {
                return d_o1.before(d_o2)? 1 : -1;
            }
        } catch (ParseException e) {
        e.printStackTrace();
        }

        return 0;
    }
}
