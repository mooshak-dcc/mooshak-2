//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.19 at 11:31:31 AM WEST 
//


package pt.up.fc.dcc.mooshak.evaluation.kora.parse.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WindowsProperties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WindowsProperties">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="startVisible" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WindowsProperties")
public class WindowsProperties {

    @XmlAttribute(name = "disabled", required = true)
    protected boolean disabled;
    @XmlAttribute(name = "startVisible", required = true)
    protected boolean startVisible;

    /**
     * Gets the value of the disabled property.
     * 
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the value of the disabled property.
     * 
     */
    public void setDisabled(boolean value) {
        this.disabled = value;
    }

    /**
     * Gets the value of the startVisible property.
     * 
     */
    public boolean isStartVisible() {
        return startVisible;
    }

    /**
     * Sets the value of the startVisible property.
     * 
     */
    public void setStartVisible(boolean value) {
        this.startVisible = value;
    }
    
    
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[\"disabled \":" + disabled + ", \"startVisible\": " + startVisible + "]";
	}

	 public Map<String, String> getInfo(){
	    	
	    	Map<String, String> list = new HashMap<String, String>();
	    	
	    	list.put("disabled", ""+this.disabled);
	    	list.put("startVisible", ""+this.startVisible);
	    	

	    	return list;
	    	
	    }

}
