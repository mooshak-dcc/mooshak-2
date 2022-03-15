package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A report of the solution submitted. 
 * 
 * @author josepaiva
 */
public class Report extends PersistentObject {
	private static final long serialVersionUID = 1L;
	

	@MooshakAttribute(
			name="Report",
			type=AttributeType.FILE)
	private Path report = null;
	
	
	//-------------------- Setters and getters ----------------------//
	

	
	/**
	 * Get current reportPath
	 * @return
	 */
	public Path getReportPath() {
		if(report == null)
			return null;
		else
			return getPath().resolve(report);
	}
	
	/**
	 * Set current reportPath
	 * @param reportPath
	 */
	public void setReportPath(Path report) {
		this.report = report.getFileName();
	}

}
