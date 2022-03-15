package pt.up.fc.dcc.mooshak.rest.exception.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement(name = "exception")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDetails {

    private Integer status;
    private String title;
    private String message;
    private String path;

	public ExceptionDetails() {
	}

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
