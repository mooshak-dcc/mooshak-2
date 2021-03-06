//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.19 at 11:31:31 AM WEST 
//


package pt.up.fc.dcc.mooshak.evaluation.kora.parse.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EdgeTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EdgeTypes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="variant" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="label" type="{http://www.example.org/EshuConfig}Label"/>
 *         &lt;element name="isConfigurable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="infoUrlEdge" type="{http://www.example.org/EshuConfig}InfoUrl"/>
 *         &lt;element name="propertiesEdge" type="{http://www.example.org/EshuConfig}Properties"/>
 *         &lt;element name="featuresEdge" type="{http://www.example.org/EshuConfig}Features"/>
 *         &lt;element name="cardinality" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="edgeStyle" type="{http://www.example.org/EshuConfig}EdgeStyle"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EdgeTypes", propOrder = {
    "type",
    "variant",
    "label",
    "isConfigurable",
    "infoUrlEdge",
    "propertiesEdge",
    "featuresEdge",
    "cardinality",
    "edgeStyle"
})
public class EdgeTypes {

    @XmlElement(required = true, defaultValue = "class")
    protected String type;
    @XmlElement(required = true, defaultValue = "class")
    protected String variant;
    @XmlElement(required = true)
    protected Label label;
    @XmlElement(defaultValue = "true")
    protected boolean isConfigurable;
    @XmlElement(required = true)
    protected InfoUrl infoUrlEdge;
    @XmlElement(required = true)
    protected Properties propertiesEdge;
    @XmlElement(required = true)
    protected Features featuresEdge;
    protected boolean cardinality;
    @XmlElement(required = true)
    protected EdgeStyle edgeStyle;

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
     * Gets the value of the variant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the value of the variant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVariant(String value) {
        this.variant = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link Label }
     *     
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link Label }
     *     
     */
    public void setLabel(Label value) {
        this.label = value;
    }

    /**
     * Gets the value of the isConfigurable property.
     * 
     */
    public boolean isIsConfigurable() {
        return isConfigurable;
    }

    /**
     * Sets the value of the isConfigurable property.
     * 
     */
    public void setIsConfigurable(boolean value) {
        this.isConfigurable = value;
    }

    /**
     * Gets the value of the infoUrlEdge property.
     * 
     * @return
     *     possible object is
     *     {@link InfoUrl }
     *     
     */
    public InfoUrl getInfoUrlEdge() {
        return infoUrlEdge;
    }

    /**
     * Sets the value of the infoUrlEdge property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoUrl }
     *     
     */
    public void setInfoUrlEdge(InfoUrl value) {
        this.infoUrlEdge = value;
    }

    /**
     * Gets the value of the propertiesEdge property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getPropertiesEdge() {
        return propertiesEdge;
    }

    /**
     * Sets the value of the propertiesEdge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setPropertiesEdge(Properties value) {
        this.propertiesEdge = value;
    }

    /**
     * Gets the value of the featuresEdge property.
     * 
     * @return
     *     possible object is
     *     {@link Features }
     *     
     */
    public Features getFeaturesEdge() {
        return featuresEdge;
    }

    /**
     * Sets the value of the featuresEdge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Features }
     *     
     */
    public void setFeaturesEdge(Features value) {
        this.featuresEdge = value;
    }

    /**
     * Gets the value of the cardinality property.
     * 
     */
    public boolean isCardinality() {
        return cardinality;
    }

    /**
     * Sets the value of the cardinality property.
     * 
     */
    public void setCardinality(boolean value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the edgeStyle property.
     * 
     * @return
     *     possible object is
     *     {@link EdgeStyle }
     *     
     */
    public EdgeStyle getEdgeStyle() {
        return edgeStyle;
    }

    /**
     * Sets the value of the edgeStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link EdgeStyle }
     *     
     */
    public void setEdgeStyle(EdgeStyle value) {
        this.edgeStyle = value;
    }
    
    
    public String toString() {
		return "{\"type\":" +  "\""+ type + "\""+ ", \"labelConf\":" + label + ", \"variant\": " + "\"" +variant+"\"" + ", \"isConfigurable\":" + "\"" +isConfigurable +"\"" + ", \"infoUrl\":"
				+ infoUrlEdge + ", \"propertiesView\":" + propertiesEdge + ", \"features\":" + featuresEdge +", \"cardinality\" :" + "\"" +cardinality + "\""
				+", \"style\" :" +  edgeStyle  
				+ "}";
	}
    
	public String getInfoUrlEdgeText() {
		String url="";
		List<String> infoUrlEdge = this.getInfoUrlEdge().getUrl();
		
		for(String entry: infoUrlEdge){
			url = url + entry +"\n";
		}
	
        return url;
    }

}
