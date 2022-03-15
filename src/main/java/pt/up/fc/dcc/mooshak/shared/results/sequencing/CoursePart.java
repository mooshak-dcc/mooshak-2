package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.gwt.user.client.rpc.IsSerializable;

import pt.up.fc.dcc.mooshak.content.util.DateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoursePart", propOrder = {
	"resources",
    "children"
})
public class CoursePart implements IsSerializable {
	
	@XmlElement(name = "resource", namespace="http://www.example.org/Seqins")
    protected List<CourseResource> resources;
    @XmlElement(name = "coursepart", namespace="http://www.example.org/Seqins")
    protected List<CoursePart> children;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "minimumWeight")
    protected Integer minimumWeight;
    @XmlAttribute(name = "start")
    protected String start;
    @XmlAttribute(name = "startDate")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlSchemaType(name = "dateTime")
	protected Date startDate;
    @XmlAttribute(name = "endDate")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlSchemaType(name = "dateTime")
	protected Date endDate;
    @XmlAttribute(name = "previous", namespace="http://www.example.org/Seqins")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected CoursePart previous;
    @XmlAttribute(name = "following", namespace="http://www.example.org/Seqins")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected CoursePart following;
    @XmlAttribute(name = "precedences", namespace="http://www.example.org/Seqins")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<CoursePart> precedences;
    @XmlAttribute(name = "probabilityInExam")
    protected Integer probabilityInExam;
    @XmlAttribute(name = "weightInExam")
    protected Integer weightInExam;
    @XmlAttribute(name = "language")
    protected String language;
	
	public CoursePart() {
	}

	/**
	 * @param id
	 */
	public CoursePart(String id) {
		this.setId(id);
		this.setName(id);
	}

	/**
	 * @param id
	 * @param name
	 */
	public CoursePart(String id, String name) {
		this.id = id;
		this.name = name;
	}

    /**
     * Gets the value of the resources property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resources property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResources().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CourseResource }
     * 
     * 
     */
    public List<CourseResource> getResources() {
        if (resources == null) {
            resources = new ArrayList<CourseResource>();
        }
        return this.resources;
    }

    /**
     * Gets the value of the children property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the children property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildren().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CoursePart }
     * 
     * 
     */
    public List<CoursePart> getChildren() {
        if (children == null) {
            children = new ArrayList<CoursePart>();
        }
        return this.children;
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
     * Gets the value of the minimumWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinimumWeight() {
        return minimumWeight;
    }

    /**
     * Sets the value of the minimumWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinimumWeight(Integer value) {
        this.minimumWeight = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStart(String value) {
        this.start = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getStartDate() {
        
    	return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setStartDate(Date value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getEndDate() {
    	
    	return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setEndDate(Date value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the previous property.
     * 
     * @return
     *     possible object is
     *     {@link CoursePart }
     *     
     */
    public CoursePart getPrevious() {
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
        this.previous = (CoursePart) value;
    }

    /**
     * Gets the value of the following property.
     * 
     * @return
     *     possible object is
     *     {@link CoursePart }
     *     
     */
    public CoursePart getFollowing() {
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
        this.following = (CoursePart) value;
    }

    /**
     * Gets the value of the precedences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the precedences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrecedences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<CoursePart> getPrecedences() {
        if (precedences == null) {
            precedences = new ArrayList<CoursePart>();
        }
        return this.precedences;
    }

    /**
     * Gets the value of the probabilityInExam property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProbabilityInExam() {
        return probabilityInExam;
    }

    /**
     * Sets the value of the probabilityInExam property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProbabilityInExam(Integer value) {
        this.probabilityInExam = value;
    }

    /**
     * Gets the value of the weightInExam property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWeightInExam() {
        return weightInExam;
    }

    /**
     * Sets the value of the weightInExam property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWeightInExam(Integer value) {
        this.weightInExam = value;
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
