package com.precioLuz.providers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarDatePickerProvider {

    public CalendarDatePickerProvider() {
    }

    public static String[] obtenerFechasFromDatePicker(int dayOfMonth, int month, int year) throws ParseException {

        String [] _fechasBusqueda = new String[2];

        String _date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTime(sdf.parse(_date));
            _date = sdf.format(calendar.getTime());
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String _dateNext = sdf.format(calendar.getTime());

        _fechasBusqueda[0] = _date;
        _fechasBusqueda[1] = _dateNext;
        return _fechasBusqueda;
    }
}
