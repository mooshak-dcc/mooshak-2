package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A completion of a part of the course
 * 
 * @author josepaiva
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompletionUpdate")
public class CompletionUpdate implements IsSerializable {

	@XmlElement
	private String partId;
	
	public CompletionUpdate() {
	}

	public CompletionUpdate(String partId) {
		super();
		this.partId = partId;
	}

	/**
	 * @return the partId
	 */
	public String getPartId() {
		return partId;
	}

	/**
	 * @param partId the partId to set
	 */
	public void setPartId(String partId) {
		this.partId = partId;
	}

}
