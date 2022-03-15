package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject.Processor;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellBrowser.Builder;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

/**
 * Implementation of admin app's top level view 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class TopLevelViewImpl extends Composite implements TopLevelView {

	private static final int DEFAULT_BROWSER_COLUMN_WIDTH = 200;

	private static String TOP_LEVEL_FOLDER = "data";
	
	private static AdminUiBinder uiBinder = GWT.create(AdminUiBinder.class);

	private static Logger LOGGER = Logger.getLogger("");
	
	@UiTemplate("TopLevelView.ui.xml")
	interface AdminUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}
	
	interface TopLevelStyle extends CssResource {
	    String selected();
	}

	@UiField 
	TopLevelStyle style;
	
	@UiField
	Label load;
	
	@UiField
	Label procs;
	
	@UiField
	Label evals;
	
	@UiField
	Label threads;
	
	@UiField
	Label sessions;
	
	@UiField
	Label objects;
	
	@UiField
	Label freeMemory;
	
	@UiField
	Label maxMemory;
	
	@UiField
	Label openFileDescriptors;
	
	@UiField
	Label maxFileDescriptors;
	
	@UiField
	Label version;
	
	  /**
	   * The CellBrowser.
	   */
	 @UiField(provided = true)
	 CellBrowser cellBrowser;
	 
	 @UiField
	 CardPanel panel;

	private Presenter presenter = null;

	private DataManager dataManager = DataManager.getInstance();
	
	private String objectId = null;
	
	public TopLevelViewImpl() {
		configureCellBrowser();
		initWidget(uiBinder.createAndBindUi(this));
		
		terms.getElement().setAttribute("placeholder", 
				"search terms (use reg. expr.)");
		
		cellBrowser.ensureDebugId("adminCellBrowser");
	}
		
	/**
	 * Open top level folder at the beginning
	 * This needs to be executed after loading the object
	 */
	protected void onLoad() { 
		presenter.onSelectedObject(TOP_LEVEL_FOLDER);		
	};
	
	
	PersistentObjectIDCell selectedCell = null;
	
	/**
	 * Special text cell to remove prefix path in IDs 
	 */
	class PersistentObjectIDCell extends AbstractCell<String> {
		
		private boolean selected = false;

		// these are copies for redrawing on unsetting
		private Context context = null;
		private Element parent = null;
		private String value = null;

		
		PersistentObjectIDCell() {
			super(BrowserEvents.CLICK);
		}
		
		void select(Context context, Element parent, String value) {
			this.context = context;
			this.parent = parent;
			this.value = value;
			
			selected = true;
			
			setValue(context,parent,value);
		}
		
		void unselect() {
			
			if(selected) {
				selected = false;
				setValue(context,parent,value);
			} else
				LOGGER.log(Level.SEVERE,"unselecting non selected node");
		}
		
		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			String label;
			// fixes bug of paged cells in big list when a cell is selected 
			boolean isReallySelected = selected && value.equals(this.value);
			
			 if (value == null) {
			        return;
			 }
			
			int pos = value.lastIndexOf('/');
			if(pos > -1)
				label = value.substring(pos+1);
			else
				label = value;
			
			if(isReallySelected) 
				sb.appendHtmlConstant("<span style='color: orange;'>");
			sb.appendEscaped(label);
			if(isReallySelected)
				sb.appendHtmlConstant("</span>");
		}
		
		@Override
		public void onBrowserEvent(
				Context context,
				Element parent, String value, NativeEvent event,
				ValueUpdater<String> valueUpdater) {
		
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			
			resetFind();
			
			if(selectedCell != null) {
				selectedCell.unselect();
			}
			select(context,parent,value);
			selectedCell = this;
			
			if(presenter != null)
				presenter.onSelectedObject(value);
		}
		
	}
	
	
	private void configureCellBrowser() {

		TreeViewModel viewModel = new TreeViewModel() {

			@Override
			public <T> NodeInfo<?> getNodeInfo(T value) {
				ListDataProvider<String> dataProvider = null;
				Cell<String> cell = new PersistentObjectIDCell();
				
				if(value instanceof String) {
					String id = (String) value;
					dataProvider = dataManager.getChildrenDataProvider(id);
					if(presenter != null)
						presenter.onSelectedObject(id);
						
				}
				
				// the column width has to be set at each iteration
				if(cellBrowser != null)
					cellBrowser.setDefaultColumnWidth(
							DEFAULT_BROWSER_COLUMN_WIDTH);
				
				return new DefaultNodeInfo<String>(dataProvider,cell);

			}

			@Override
			public boolean isLeaf(Object value) {
				
				if(value instanceof MooshakObject) {
					MooshakObject mo = (MooshakObject) value;				 
					if(mo.getChildren().size() == 0)
						return true;
					else
						return false;
				} else
					return false;
			}
			
		};
		
		Builder<String> builder = 
				new CellBrowser.Builder<String>(viewModel,TOP_LEVEL_FOLDER);
		
		cellBrowser = builder.build();
		cellBrowser.setAnimationEnabled(true);
	}
	
	@Override
	public void updateCellBrowser(String id) {
	
		TreeNode node = getNode(cellBrowser.getRootTreeNode(),id);

 		if(node != null && ! node.isDestroyed()) {
			TreeNode parent = node.getParent();
			int index = node.getIndex();
			
			parent.setChildOpen(index, false,true);
			parent.setChildOpen(index, true,true);
		}
 		
 		if (selectedCell != null) {
	 		TreeNode selectedNode = 
	 				getNode(cellBrowser.getRootTreeNode(),selectedCell.value);
	 		
	 		if (selectedNode != null)
	 			selectedNode.getParent().setChildOpen(selectedNode.getIndex(), true,true);
 		}
	}
	
	private TreeNode getNode(TreeNode node, String id) {
		for(int i=0; i < node.getChildCount(); i++) 
			
			if(node.isChildOpen(i)) {
				Object value = node.getChildValue(i);
			
				if(value instanceof String) {
					String nodeId = ((String) value);
						
					if(id.equals(nodeId))
						return node.setChildOpen(i, true);
						
					if(id.startsWith(nodeId))
						return getNode(node.setChildOpen(i, true),id);
				}
			}
		return null;
	}
	
	

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	
	/**
	 * Get CardPanel where object content is presented.
	 * @return
	 */
	public CardPanel getContent() {
		return panel;
	}
	
	@UiField
	CustomImageButton logout;
	
	@UiHandler("logout")
	void logout(ClickEvent e) {
		if (presenter != null)
			presenter.onLogoutClicked();
	}
	
	private static RegExp regExp = RegExp.compile("(data/contests/([^/]+))");
	
	@UiField
	Button goToJudge;
	
	@UiHandler("goToJudge")
	void goToJudge(ClickEvent e) {
		if (presenter != null) {
			
			MatchResult matcher = regExp.exec(objectId);
			boolean matchFound = matcher != null;

			if (matchFound) {
				presenter.onGoToJudgeClicked(matcher.getGroup(2));
			}
		}
	}
	
	@UiField
	Button goToCreator;
	
	@UiHandler("goToCreator")
	void goToCreator(ClickEvent e) {
		if (presenter != null) {
			
			MatchResult matcher = regExp.exec(objectId);
			boolean matchFound = matcher != null;

			if (matchFound) {
				presenter.onGoToCreatorClicked(matcher.getGroup(2));
			}
		}
	}
	
	
	private List<String> foundObjects = null; 
	private int findCursor = -1; 
	private boolean nameNotContent = true;
	
	@UiField
	RadioButton name;
	
	@UiHandler("name")
	void setName(ClickEvent e) {
		nameNotContent = true;
	}
	
	@UiField
	RadioButton content;
	
	@UiHandler("content")
	void setContent(ClickEvent e) {
		nameNotContent = false;
		
		Window.alert("Searchs on content take more time");
	}
	
	@UiField
	TextBox terms;
	
	@UiHandler("terms")
	void searchTerms(ValueChangeEvent<String> e) {
		presenter.onFind(terms.getText(),nameNotContent);
	}
	
	@UiHandler("terms")
	void clearTerms(KeyPressEvent e) {
		findState.setText("");
		up.setEnabled(false);
		down.setEnabled(false);
	}
	
	
	@Override
	public void setFoundList(List<String> found) {
		foundObjects = found;
		
		if(found == null || found.size() == 0)
			setFindCursor(-1);
		else 
			setFindCursor(0);	
	}
	
	@UiField
	Label findState;
	
	private void resetFind() {
		terms.setText("");
		findState.setText("");
	}
	
	private void setFindCursor(int cursor) {
		final Map<Style, Cursor> cursors = 
				AbstractAppController.setCursorsToWait();
		
		findCursor = cursor;
		
		if(cursor < 0) {
			findState.setText("Not found");
			up.setEnabled(false);
			down.setEnabled(false);
		} else {
			String id = foundObjects.get(findCursor);
						
			loadDataAndUpdateCellBrowser(id);
			
			findState.setText((cursor+1)+" of "+foundObjects.size());
			
			up.setEnabled(cursor > 0);
			down.setEnabled(cursor+1 < foundObjects.size());
		}
		
		AbstractAppController.resetCursors(cursors);
	}
	
	/**
	 * Check if data was fetched from server and then open browser cells
	 * 
	 * @param id of  data object
	 */
	private void loadDataAndUpdateCellBrowser(String id) {

		if(selectedCell != null) {
			selectedCell.unselect();
			selectedCell = null;
		}

		TreeNode root = cellBrowser.getRootTreeNode();
		loadDataAndUpdateCellBrowser(id,id.indexOf("/")+1,root);
	}
	
	/**
	 * Check if data was fetched from server and then open browser cells
	 *  
	 * @param id	of data object
	 * @param index of id of current open node 
	 * @param node	of last open cell
	 */
	private void loadDataAndUpdateCellBrowser(
			final String id, 
			final int index,
			final TreeNode node) {
		
		final int next = id.indexOf("/", index);
		
		if(next == -1) {
			final String base = id.substring(0, index-1);
			
			dataManager.getMooshakObject(base, new Processor() {

				@Override
				public void process(DataObject dataObject) {
					TreeNode child = showInNode(node,dataObject,id);
					
					if(child != null) {
						Object nodeValue = node.getChildValue(index);
					
						if(nodeValue instanceof PersistentObjectIDCell){
							PersistentObjectIDCell cell = 
								(PersistentObjectIDCell) nodeValue;
												
							// should select final cell, but isn't working :-(
							cell.select(cell.context,cell.parent,cell.value);
							selectedCell = cell;	
						}
					}
				}
			});
				
		} else {
			final String base = id.substring(0, index-1);
			final String name = id.substring(0,next);
			
			dataManager.getMooshakObject(base, new Processor() {

				@Override
				public void process(DataObject dataObject) {
					TreeNode child = showInNode(node,dataObject,name);
					
					if((child != null))
						loadDataAndUpdateCellBrowser(id,next+1,child);

				}
				
			});
		}
	}
	
	private TreeNode showInNode(TreeNode node, DataObject dataObject,String name) {
		int index = getIndexOf(dataObject,name);
		TreeNode child = null;
		
		if(index == -1) 
			findState.setText("term not found !!");
		else if(index >= node.getChildCount()) 
			findState.setText("please open column");
		else 
			child = node.setChildOpen(index, true, true); 
				
		return child;
	}
	
	/**
	 * Return index of child in given id in the children of given data object
	 * 
	 * @param dataObject
	 * @param id
	 * @return
	 */
	private int getIndexOf(DataObject dataObject,String id) {
		int index = 0;
		
		for(String child: dataObject.getChildrenProvider().getList()) 
			if(id.equals(child))
				return index;
			else
				index++;
		
		return -1;
	}
	
	native void consoleLog( String message) /*-{
		console.log(message );
	}-*/;
	
	@UiField
	Button up;
	
	@UiHandler("up")
	void findUp(ClickEvent e) {
		if(foundObjects != null && findCursor > 0)
			setFindCursor(--findCursor);
	}
	
	@UiField
	Button down;
	
	@UiHandler("down")
	void findDown(ClickEvent e) {
		if(foundObjects != null && findCursor < foundObjects.size()-1) 
			setFindCursor(++findCursor);
	}
	

	@Override
	public void setSelectedObjectId(String id) {
		objectId = id;
		
		MatchResult matcher = regExp.exec(id);
		boolean matchFound = matcher != null;

		if (matchFound) {
	       goToCreator.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	       goToJudge.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
		else {
	       goToCreator.getElement().getStyle().setDisplay(Display.NONE);
	       goToJudge.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	@Override
	public void setServerStatus(ServerStatus status) {
		
		String totalMemoryTxt = Double.toString(status.getFreeMemory() >> 20);
		String maxMemoryTxt = Double.toString(status.getMaxMemory() >> 20 );
		
		load.setText(Double.toString(status.getSystemLoadAverage()));
		procs.setText(Integer.toString(status.getAvailableProcessors()));
		evals.setText(Integer.toString(status.getEvaluationsInProgress()));
		sessions.setText(Integer.toString(status.getSessionCount()));
		objects.setText(Integer.toString(status.getPersistentObjectCount()));
		threads.setText(Integer.toString(status.getActiveThreadCount()));
		freeMemory.setText(totalMemoryTxt+"M");
		maxMemory.setText(maxMemoryTxt+"M");
		openFileDescriptors.setText(
				Long.toString(status.getOpenFileDescriptorCount()));
		maxFileDescriptors.setText(
				Long.toString(status.getMaxFileDescriptorCount()));
	}

	@Override
	public void setVersion(String versionText) {
		version.setText(versionText);
	}
	
}
