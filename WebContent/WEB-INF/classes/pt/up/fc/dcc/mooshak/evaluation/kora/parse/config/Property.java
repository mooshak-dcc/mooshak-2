//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.19 at 11:31:31 AM WEST 
//


package pt.up.fc.dcc.mooshak.evaluation.kora.parse.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Property">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="typeShow" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="view" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="impExp" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property")
public class Property {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "typeShow", required = true)
    protected String typeShow;
    @XmlAttribute(name = "disabled", required = true)
    protected String disabled;
    @XmlAttribute(name = "view", required = true)
    protected boolean view;
    @XmlAttribute(name = "impExp", required = true)
    protected boolean impExp;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the typeShow property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeShow() {
        return typeShow;
    }

    /**
     * Sets the value of the typeShow property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeShow(String value) {
        this.typeShow = value;
    }

    /**
     * Gets the value of the disabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisabled() {
        return disabled;
    }

    /**
     * Sets the value of the disabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabled(String value) {
        this.disabled = value;
    }

    /**
     * Gets the value of the view property.
     * 
     */
    public boolean isView() {
        return view;
    }

    /**
     * Sets the value of the view property.
     * 
     */
    public void setView(boolean value) {
        this.view = value;
    }

    /**
     * Gets the value of the impExp property.
     * 
     */
    public boolean isImpExp() {
        return impExp;
    }

    /**
     * Sets the value of the impExp property.
     * 
     */
    public void setImpExp(boolean value) {
        this.impExp = value;
    }

    public String toString() {
 		return "{\"name\":" + "\""+name + "\""+ ", \"type\":" + "\""+ type +"\"" + ", \"typeShow\":" + "\""+typeShow +"\""+ ", \"disabled\":" + "\""+ disabled +"\""
 				+ ", \"view\":" + "\""+ view +"\"" + ", \"impExp\":" + "\"" +impExp + "\"" + "}";
 	}
}
