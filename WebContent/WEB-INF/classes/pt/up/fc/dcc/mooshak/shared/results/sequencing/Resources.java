package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwt.user.client.rpc.IsSerializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resources", namespace="http://www.example.org/Seqins")
public class Resources implements IsSerializable {

	@XmlElement(name = "resource", namespace="http://www.example.org/Seqins")
    protected List<CourseResource> resource;

    public List<CourseResource> getResource() {
        if (resource == null) {
            resource = new ArrayList<CourseResource>();
        }
        return this.resource;
    }

}
