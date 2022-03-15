package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * <p>Java class for CourseMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CourseMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RELAX"/>
 *     &lt;enumeration value="STRICT"/>
 *     &lt;enumeration value="MODERATE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CourseMode")
@XmlEnum
public enum CourseMode implements IsSerializable {

    RELAX,
    STRICT,
    MODERATE;

    public String value() {
        return name();
    }

    public static CourseMode fromValue(String v) {
        return valueOf(v);
    }

}