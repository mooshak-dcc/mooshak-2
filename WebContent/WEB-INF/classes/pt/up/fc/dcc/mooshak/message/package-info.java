
/**
 * 
 * Classes is this package are message types for Mooshak's API.
 * These classes must have a JAXB @XmlRootElement annotation
 * so that they can be converted to JSON or XML by Jackson 
 * (JSON serializer usaed by Jersey).
 * Also, they must have an empty constructor an be serializable.
 * If they contain member classes they must be static.
 * Fields in this classes must either be simple types or
 * ArrayList (for JSON lists) or
 * LinkedHashMap with keys as Strings (for JSON objects).
 * 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
package pt.up.fc.dcc.mooshak.message;


