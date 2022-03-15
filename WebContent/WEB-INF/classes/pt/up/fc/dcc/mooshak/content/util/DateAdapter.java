package pt.up.fc.dcc.mooshak.content.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * An XML Adapter to allow dates of type {@link Date} formatted as
 * yyyy-MM-ddTHH:mm:ss
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public DateAdapter() {
    }

    @Override
    public String marshal(Date v) {

        if (v == null)
            return null;

        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) {

        if (v == null)
            return null;

        try {
            return dateFormat.parse(v);
        } catch (ParseException e) {
            return null;
        }
    }
}
