package pt.up.fc.dcc.mooshak.server.commands;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;

/**
 * Mock class for TeamCommandServiceImpl
 * Redefines evaluate when its not available, as currently in Windows
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class MockTeamCommandServiceImpl 
extends ParticipantCommandServiceImpl implements ParticipantCommandService{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void evaluate(
			String programName,
			byte[] programCode,
			String problemId,
			List<String> inputs,
			boolean consider) {
		
	}

	@Override
	public  EvaluationSummary getEvaluationSummary(String submission,boolean consiser) {
		EvaluationSummary summary = new EvaluationSummary();
		summary.setId("999999999");
		summary.setObservations("Some observations");
		summary.setFeedback("some feedback");
		summary.setState("pending");
		return summary;
	}


}
