package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;

/**
 * Implementation of factory of dialogs for collecting parameters of Mooshak
 * methods
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class OperationDialogFactoryImpl implements OperationDialogFactory {

	Map<MooshakMethod, OperationDialog<?>> dialogs = new HashMap<MooshakMethod, OperationDialog<?>>();

	@Override
	public OperationDialog<?> getOperationDialog(MooshakMethod method,
			MethodContext context) {
		OperationDialog<?> dialog = null;

		if (!method.isInputable())
			throw new RuntimeException("Method is not inputable:" + method);

		if (dialogs.containsKey(method))
			dialog = dialogs.get(method);
		else {
			String name = method.getName();

			switch (name) {
			case "Bless":
				dialog = new OperationDialog<EnableSafeexecContent>(
						new EnableSafeexecContent());
				break;
			case "Prepare":
				MessageContent messageContent = new MessageContent();
				messageContent.setMessage("Are you sure that you want to "
						+ "delete all this contest content and restart it?");
				dialog = new OperationDialog<>(messageContent);
				break;
			case "ReplayForm":
				dialog = new OperationDialog<>(new ReplayFormContent());
				break;
			case "ReplayUpdates":
				dialog = new OperationDialog<>(new ReplayUpdatesContent());
				break;
			case "Import Teams":
				dialog = new OperationDialog<>(new ImportTeamUploadFormContent());
				break;
			case "ImportTeamsEditForm":
				dialog = new OperationDialog<>(new ImportTeamEditFormContent());
				break;
			case "Default Languages":
				messageContent = new MessageContent();
				messageContent.setMessage("Are you sure that you want to "
						+ "set the default languages and delete previous?");
				dialog = new OperationDialog<>(messageContent);
				break;
			case "TestSolutionResult":
				dialog = new OperationDialog<>(new ShowResultContent());
				break;
			case "ExportClassificationResult":
				dialog = new OperationDialog<>(new ExportPreviewContent());
				break;
			case "ExportRankingResult":
				dialog = new OperationDialog<>(new ExportPreviewContent());
				break;
			case "GeneratePasswordArchiveDownload":
				dialog = new OperationDialog<>(new ExportPreviewContent());
				break;
			case "GeneratePasswordExcelDownload":
				dialog = new OperationDialog<>(new ExportPreviewContent());
				break;
			case "TypeForm":
				dialog = new OperationDialog<>(new ContestTypeFormContent());
				break;
			case "Import Remote Problem":
				dialog = new OperationDialog<>(new ImportRemoteProblemContent());
				break;
			case "ViewCertificatesView":
				dialog = new OperationDialog<>(new ViewCertificatesContent());
				break;
			case "ResolverFeedDownload":
				dialog = new OperationDialog<>(new ExportPreviewContent());
				break;
			case "Generate Outputs From Solution":
				dialog = new OperationDialog<>(new GenerateOutputsContent());
				break;
			case "Create Tournament":
				dialog = new OperationDialog<>(new CreateTournamentContent());
				break;
			case "Set Up Tournament":
				dialog = new OperationDialog<>(new SetUpTournamentContent());
				break;
			case "Select Tournament Participants Step 2":
				dialog = new OperationDialog<>(new SelectTournamentSubmissions());
				break;
			case "Submit Problem SAs":
				dialog = new OperationDialog<>(new SetUpProblemBotsContent());
				break;
			case "Tournament JSON Download":
				dialog = new OperationDialog<>(new ExportPreviewContent());
				break;
			default:
				throw new RuntimeException("Unknown method name:"
						+ method.getName());
			}

		}

		dialog.setContext(context);
		dialog.enable();

		return dialog;
	}

}
