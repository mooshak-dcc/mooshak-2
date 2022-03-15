package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.gwt.user.client.rpc.IsSerializable;

import pt.up.fc.dcc.mooshak.content.util.DateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CourseResource")
public class CourseResource implements IsSerializable {
	
	@XmlType(name = "ResourceType")
	@XmlEnum
	public enum ResourceType implements IsSerializable {

	    PROBLEM,
	    PDF,
	    VIDEO;
		
		private ResourceType() {
		}

	    public String value() {
	        return name();
	    }

	    public static ResourceType fromValue(String v) {
	        return valueOf(v);
	    }

	}
	
	@XmlType(name = "ResourceState")
	@XmlEnum
	public enum ResourceState implements IsSerializable {

	    SEEN,
	    AVAILABLE,
	    UNAVAILABLE,
	    RECOMMENDED,
	    AUXILIARY,
	    SOLVED;
		
		private ResourceState() { }

	    public String value() {
	        return name();
	    }

	    public static ResourceState fromValue(String v) {
	        return valueOf(v);
	    }

	}

    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "title")
    protected String title;
    @XmlAttribute(name = "type")
    protected ResourceType type;
    @XmlAttribute(name = "href")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "learningTime")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlSchemaType(name = "dateTime")
	protected Date learningTime;
    @XmlAttribute(name = "previous", namespace="http://www.example.org/Seqins")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected CourseResource previous;
    @XmlAttribute(name = "following", namespace="http://www.example.org/Seqins")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected CourseResource following;
    @XmlAttribute(name = "onFail")
    protected String onFail;
    @XmlAttribute(name = "onSuccess")
    protected String onSuccess;
    @XmlAttribute(name = "state")
    protected ResourceState state;
    @XmlAttribute(name = "tags")
    protected String tags;
    @XmlAttribute(name = "eval")
    protected Boolean eval;
    @XmlAttribute(name = "mandatory")
    protected Boolean mandatory;
    @XmlAttribute(name = "language")
    protected String language;
    
    public CourseResource() {
	}

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
    	if (id == null)
    		return href;
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceId() {
        return getId();
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceType }
     *     
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceType }
     *     
     */
    public void setType(ResourceType value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the learningTime property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getLearningTime() {
        return learningTime;
    }

    /**
     * Sets the value of the learningTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setLearningTime(Date value) {
        this.learningTime = value;
    }

    /**
     * Gets the value of the previous property.
     * 
     * @return
     *     possible object is
     *     {@link CourseResource }
     *     
     */
    public CourseResource getPrevious() {
        return previous;
    }

    /**
     * Sets the value of the previous property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPrevious(Object value) {
        this.previous = (CourseResource) value;
    }

    /**
     * Gets the value of the following property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public CourseResource getFollowing() {
        return following;
    }

    /**
     * Sets the value of the following property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setFollowing(Object value) {
        this.following = (CourseResource) value;
    }

    /**
     * Gets the value of the onFail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnFail() {
        return onFail;
    }

    /**
     * Sets the value of the onFail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnFail(String value) {
        this.onFail = value;
    }

    /**
     * Gets the value of the onSuccess property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnSuccess() {
        return onSuccess;
    }

    /**
     * Sets the value of the onSuccess property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnSuccess(String value) {
        this.onSuccess = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceState }
     *     
     */
    public ResourceState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceState }
     *     
     */
    public void setState(ResourceState value) {
        this.state = value;
    }

    /**
     * Gets the value of the eval property.
     * 
     * @return
     *     boolean
     *     
     */
    public Boolean getEval() {
    	if (eval == null)
    		return false;
        return eval;
    }

    /**
     * Sets the value of the eval property.
     * 
     * @param boolean
     *     
     */
    public void setEval(Boolean value) {
        this.eval = value;
    }

    /**
     * Gets the value of the mandatory property.
     * 
     * @return
     *     boolean
     *     
     */
    public Boolean getMandatory() {
    	if (mandatory == null)
    		return false;
        return mandatory;
    }

    /**
     * Sets the value of the mandatory property.
     * 
     * @param boolean
     *     
     */
    public void setMandatory(Boolean value) {
        this.mandatory = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }
}
