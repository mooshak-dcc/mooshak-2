//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.16 at 02:04:13 PM WEST 
//


package pt.up.fc.dcc.mooshak.content.erl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for SummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mark" type="{http://evae/messages}MarkType"/>
 *         &lt;element name="classify" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="feedback">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="environmentValues" type="{http://evae/messages}EnvironmentValuesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummaryType", propOrder = {
    "mark",
    "classify",
    "feedback",
    "environmentValues"
})
public class SummaryType {

    @XmlElement(required = true)
    protected MarkType mark;
    @XmlElement(required = true)
    protected String classify;
    @XmlElement(required = true)
    protected SummaryType.Feedback feedback;
    @XmlElement(required = true)
    protected EnvironmentValuesType environmentValues;

    /**
     * Gets the label of the mark property.
     * 
     * @return
     *     possible object is
     *     {@link MarkType }
     *     
     */
    public MarkType getMark() {
        return mark;
    }

    /**
     * Sets the label of the mark property.
     * 
     * @param label
     *     allowed object is
     *     {@link MarkType }
     *     
     */
    public void setMark(MarkType value) {
        this.mark = value;
    }

    /**
     * Gets the label of the classify property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassify() {
        return classify;
    }

    /**
     * Sets the label of the classify property.
     * 
     * @param label
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassify(String value) {
        this.classify = value;
    }

    /**
     * Gets the label of the feedback property.
     * 
     * @return
     *     possible object is
     *     {@link SummaryType.Feedback }
     *     
     */
    public SummaryType.Feedback getFeedback() {
        return feedback;
    }

    /**
     * Sets the label of the feedback property.
     * 
     * @param label
     *     allowed object is
     *     {@link SummaryType.Feedback }
     *     
     */
    public void setFeedback(SummaryType.Feedback value) {
        this.feedback = value;
    }

    /**
     * Gets the label of the environmentValues property.
     * 
     * @return
     *     possible object is
     *     {@link EnvironmentValuesType }
     *     
     */
    public EnvironmentValuesType getEnvironmentValues() {
        return environmentValues;
    }

    /**
     * Sets the label of the environmentValues property.
     * 
     * @param label
     *     allowed object is
     *     {@link EnvironmentValuesType }
     *     
     */
    public void setEnvironmentValues(EnvironmentValuesType value) {
        this.environmentValues = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="item" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "item"
    })
    public static class Feedback {

        protected List<SummaryType.Feedback.Item> item;

        /**
         * Gets the label of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SummaryType.Feedback.Item }
         * 
         * 
         */
        public List<SummaryType.Feedback.Item> getItem() {
            if (item == null) {
                item = new ArrayList<SummaryType.Feedback.Item>();
            }
            return this.item;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        public static class Item {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "type")
            protected String type;

            /**
             * Gets the label of the content property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * Sets the label of the content property.
             * 
             * @param label
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * Gets the label of the type property.
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
             * Sets the label of the type property.
             * 
             * @param label
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

        }

    }

}
