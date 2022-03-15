package pt.up.fc.dcc.mooshak.client.views;

import static pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.NON_BREAKING_SPACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.CellResetEditorButton;
import pt.up.fc.dcc.mooshak.client.images.ImageResources;
import pt.up.fc.dcc.mooshak.client.utils.FileDownloader;
import pt.up.fc.dcc.mooshak.client.widgets.CategorizedColumnFilter;
import pt.up.fc.dcc.mooshak.client.widgets.CategorizedHeader;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageCell;
import pt.up.fc.dcc.mooshak.client.widgets.DatagridColumnSelectorPopup;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * Implementation of a listing view
 * 
 * @author josepaiva
 */
public abstract class ListingImpl extends Composite implements Listing {
	public static final int NUMBER_OF_VISIBLE_COLUMNS = 15;

	private static final int FILTER_UPDATE_TIME = 10000;
	
	public interface ListingStyle extends CssResource {
		String flag();
		String columnSelector();
		String exportBtn();
		String timeBtn();
	}

	public interface Resources extends ClientBundle {

	      public static final Resources INSTANCE = GWT.create(Resources.class); 

	      @Source("Listing.css")
	      @CssResource.NotStrict
	      ListingStyle css();
	}

	private ICPCConstants constants = GWT.create(ICPCConstants.class);

	private Map<String, CategorizedColumnFilter> columnFilters = new HashMap<String, CategorizedColumnFilter>();

	private Timer filterUpdate;

	private String participant;

	private boolean showSubmissionCode = false;
	
	private DatagridColumnSelectorPopup columnSelectorPopup;
	
	private Map<String, Column<Row, String>> columns = new HashMap<>();
	private Map<String, String> columnsLabel = new HashMap<>();
	private Map<String, String> columnsWidth = new HashMap<>();
	
	private boolean absoluteTime = false;
	
	public ListingImpl() {
		Resources.INSTANCE.css().ensureInjected();
		columnSelectorPopup = new DatagridColumnSelectorPopup(this);
	}

	@Override
	public void setColumns(List<ColumnInfo> columnInfos) {

		ListHandler<Row> handler = getDataProvider().getListHandler();
		Column<?, ?> firstColumn = null;

		double leftDistance = 0;
		for (final ColumnInfo columnInfo : columnInfos) {
			String i18nName = geti18nColumnName(columnInfo.getName());
			StringBuilder label = new StringBuilder(i18nName);
			Column<Row, String> column;

			switch (columnInfo.getType()) {
			case TIME:
				column = new Column<Row, String>(new TextCell()) {

					@Override
					public String getValue(Row row) {
						return row.getValue(columnInfo.getName());
					}
					
					@Override
					public void render(Context context, Row object,
							SafeHtmlBuilder sb) {
						if(object == null)
							return;

						if(getDataGrid().getColumnWidth(this).equals("0")) {
							sb.append(SafeHtmlUtils.fromTrustedString(NON_BREAKING_SPACE));
							return;
						}
						
						if (!getDataProvider().getKind().equals(Kind.QUESTIONS)) {
							sb.appendHtmlConstant(object.getValue(absoluteTime ? "absTime" : 
				    			 columnInfo.getName()));
							return;
						}
						
						String value = object.getValue("problem");
						if (value != null) {
							if(Arrays.asList("??", ListingDataProvider.NON_BREAKING_SPACE)
									.contains(value))
							     sb.appendHtmlConstant("<span style=\"font-weight:bold\">"
							    		 + object.getValue(absoluteTime ? "absTime" : 
							    			 columnInfo.getName()) + "</span>");
							else
								sb.appendHtmlConstant(object.getValue(absoluteTime ? "absTime" : 
					    			 columnInfo.getName()));
					    }
					}
				};
				break;
			case FLAG:
				
				if (getDataProvider().getKind().equals(Kind.SUBMISSIONS))
					label = new StringBuilder("");
				
				column = new Column<Row, String>(new CustomImageCell()) {

					@Override
					public String getValue(Row row) {
						String value = row.getValue(columnInfo.getName());
						SafeUri safeUri;

						if (NON_BREAKING_SPACE.equals(value))
							safeUri = UriUtils.fromSafeConstant("");
						else {
							String uri = "flag/" + value;

							safeUri = UriUtils.fromTrustedString(uri);
						}

						return safeUri.asString();
					}
					
					public void render(com.google.gwt.cell.client.Cell.Context context, 
							Row object, SafeHtmlBuilder sb) {
						if(getDataGrid().getColumnWidth(this).equals("0"))
							sb.append(SafeHtmlUtils.fromTrustedString(NON_BREAKING_SPACE));
						else
							super.render(context, object, sb);
					};

				};
				column.setCellStyleNames(Resources.INSTANCE.css().flag());
				break;
			case CLASSIFICATION:
				column = new Column<Row, String>(new TextCell()) {

					@Override
					public String getValue(Row row) {
						return row.getValue(columnInfo.getName());
					}
					
					@Override
					public void render(Context context, Row object,
							SafeHtmlBuilder sb) {
						
						if(object == null)
							return;

						if(getDataGrid().getColumnWidth(this).equals("0")) {
							sb.append(SafeHtmlUtils.fromTrustedString(NON_BREAKING_SPACE));
							return;
						}
						
						String value = object.getValue("classification");
						if (value != null) {
							if(value.equalsIgnoreCase("accepted"))
							     sb.appendHtmlConstant("<span style=\"color: green\">"
							    		 + value + "</span>");
							else
								sb.appendHtmlConstant("<span style=\"color: red\">"
							    		 + value + "</span>");
					    }
					}
				};
				break;
			case TEAM:
				column = new Column<Row, String>(new TextCell()) {

					@Override
					public String getValue(Row row) {
						return row.getValue(columnInfo.getName());
					}
					
					@Override
					public void render(Context context, Row object,
							SafeHtmlBuilder sb) {
						
						if(object == null)
							return;

						if(getDataGrid().getColumnWidth(this).equals("0")) {
							sb.append(SafeHtmlUtils.fromTrustedString(NON_BREAKING_SPACE));
							return;
						}
						
						if (showColors()) {
							String color = object.getValue("color");
							if (color != null) {
							     sb.appendHtmlConstant("<div style=\"font-weight:bold;width:75%; float:left; word-break: break-word;color: " + color + ";\">" 
							    		 + getValue(object) + "</div>");
						    } else {
							     sb.appendHtmlConstant("<div style=\"font-weight:bold;width:75%; float:left; word-break: break-word;color:#c0c0c0;\">"
							    		 + getValue(object) + "</div>");
						    }
						} else {
							sb.appendHtmlConstant(getValue(object));
						}
					}
				};
				break;
			case PROBLEM:
				column = new Column<Row, String>(new TextCell()) {

					@Override
					public String getValue(Row row) {
						return row.getValue(columnInfo.getName());
					}
					
					@Override
					public void render(Context context, Row object,
							SafeHtmlBuilder sb) {
						
						if(object == null)
							return;

						if(getDataGrid().getColumnWidth(this).equals("0")) {
							sb.append(SafeHtmlUtils.fromTrustedString(NON_BREAKING_SPACE));
							return;
						}
						
						if (showColors()) {
							String color = object.getValue("problem_" + getValue(object) + "_color");
							if (color != null) {
							     sb.appendHtmlConstant("<div style=\"font-weight:bold;width:75%;float:left;word-break:break-word;color:" + color + ";\">" 
							    		 + getValue(object) + "</div>");
						    } else {
							     sb.appendHtmlConstant("<div style=\"font-weight:bold;width:75%;float:left;word-break:break-word;color:#c0c0c0;\">" 
							    		 + getValue(object) + "</div>");
						    }
						} else {
							sb.appendHtmlConstant(getValue(object));
						}
					}
				};
				break;
			default:
				column = new Column<Row, String>(new TextCell()) {

					@Override
					public String getValue(Row row) {
						return row.getValue(columnInfo.getName());
					}
					
					@Override
					public void render(Context context, Row object,
							SafeHtmlBuilder sb) {
						if(object == null)
							return;

						if(getDataGrid().getColumnWidth(this).equals("0")) {
							sb.append(SafeHtmlUtils.fromTrustedString(NON_BREAKING_SPACE));
							return;
						}
						
						if (columnInfo.getName().equals("group") &&
								(getDataProvider().getKind().equals(Kind.SUBMISSIONS) || 
										getDataProvider().getKind().equals(Kind.RANKINGS))) {
							String groupName = object.getValue("groupName");
							if (groupName != null)
								sb.appendHtmlConstant("<span style=\"color:" + object.getValue("color") + ";\""
										+ " title=\"" + groupName + "\">"
							    		 + object.getValue(columnInfo.getName()) + "</span>");
							else
								sb.appendHtmlConstant("<span style=\"color:" + object.getValue("color") + ";\">"
							    		 + object.getValue(columnInfo.getName()) + "</span>");
							return;
						} else if (columnInfo.getName().equals("group")) {
							String groupName = object.getValue("groupName");
							if (groupName != null){
								sb.appendHtmlConstant("<span title=\"" + groupName + "\">"
							    		 + object.getValue(columnInfo.getName()) + "</span>");
								return;
							}
								
						}
						else if (!getDataProvider().getKind().equals(Kind.QUESTIONS)) {
							super.render(context, object, sb);
							return;
						}
						
						String value = object.getValue("problem");
						if (value != null) {
							if(Arrays.asList("??", ListingDataProvider.NON_BREAKING_SPACE)
									.contains(value))
							     sb.appendHtmlConstant("<span style=\"font-weight:bold\">"
							    		 + object.getValue(columnInfo.getName()) + "</span>");
							else
								sb.appendHtmlConstant(object.getValue(columnInfo.getName()));
					    }
					}
				};
			}

			label.setCharAt(0, Character.toUpperCase(label.charAt(0)));

			if ((columnInfo.getType() == ColumnType.LABEL || columnInfo.getType() == ColumnType.PROBLEM ||
					columnInfo.getType() == ColumnType.TEAM ||
					columnInfo.getType() == ColumnType.CLASSIFICATION)
					&& (getDataProvider().getKind() != Kind.RANKINGS || columnInfo
						.getName().equalsIgnoreCase("team"))
					&& isFilterable()) {
				CategorizedHeader header = new CategorizedHeader(
						label.toString(),
						columnInfo.getName(),
						this,
						(int) (leftDistance) 
						+ getDataGrid().getAbsoluteLeft(), getDataGrid()
								.getAbsoluteTop() + 20);
				getDataGrid().addColumn(column, header);
				columnFilters.put(columnInfo.getName().toLowerCase(), header.getFilter());
			} else {
				if (getDataProvider().getKind() == Kind.RANKINGS &&
						columnInfo.getType() == ColumnType.LABEL &&
						columnInfo.getColor() != null)
					getDataGrid().addColumn(column, new Header<String>(new TextCell(new SafeHtmlRenderer<String>() {
						
						@Override
						public void render(String object, SafeHtmlBuilder builder) {
							builder.append(SafeHtmlUtils.fromTrustedString("<span style=\"font-weight:bold;"
									+ "color:" + columnInfo.getColor() + ";\""
										+ " title=\"" + object + "\">"
						    		 + object + "</span>"));
						}
						
						@Override
						public SafeHtml render(String object) {
							SafeHtmlBuilder builder = new SafeHtmlBuilder();
							render(object, builder);
							return builder.toSafeHtml();
						}
					})) {

						@Override
						public String getValue() {
							return columnInfo.getName();
						}
						
					});
				else
					getDataGrid().addColumn(column, label.toString());
			}

			getDataGrid()
					.setColumnWidth(column, columnInfo.getSize(), Unit.PCT);

			if (getSortable()) {
				if (firstColumn == null)
					firstColumn = column;

				column.setSortable(true);
				handler.setComparator(
						column,
						getDataProvider().getComparator(columnInfo.getName(),
								column));
			}

			leftDistance += columnInfo.getSize() * 0.01
					* getDataGrid().getOffsetWidth();
			
			if (columnInfo.getSize() > 0) {
				columns.put(columnInfo.getName(), column);
				columnsWidth.put(columnInfo.getName(), columnInfo.getSize() + "%");
				columnsLabel.put(columnInfo.getName(), label.toString());
			}
			
		}

		if (getSortable()) {
			getDataGrid().addColumnSortHandler(handler);

			ColumnSortList sortList = getDataGrid().getColumnSortList();

			sortList.push(firstColumn); // make first column the sort column
			sortList.push(firstColumn); // invert the sort order
		}
		
		if (canReplaceSubmission()) {
			getParticipantLogged();
			getShowOwnCode();
			Column<Row, String> column = 
					new Column<Row, String>(new CellResetEditorButton()) {

				@Override
				public String getValue(Row object) {
					return object.getId();
				}
				
				@Override
				public void render(Context context, Row object,
						SafeHtmlBuilder sb) {
					if(object == null || !object.getValue("teamId").equals(participant)
							|| !showSubmissionCode) {
						sb.appendHtmlConstant("<div style=\"height: 16px\"></div>");
						return;
					}
					
					sb.appendHtmlConstant(((CellResetEditorButton) getCell())
							.getElementString());
				}
				
			};
			column.setFieldUpdater(new FieldUpdater<Row, String>() {
				
				@Override
				public void update(int index, Row object, String value) {

					if(object == null || !object.getValue("teamId").equals(participant)
							|| !showSubmissionCode) 
						return;
					replaceSubmission(object.getId(),  object.getValue("teamId"),
							object.getValue("problem"));
				}
			});
			getDataGrid().addColumn(column, "");

			getDataGrid()
					.setColumnWidth(column, 5, Unit.PCT);
		}

		if (isFilterable()) {
			setFiltersData();
			filterUpdate = new Timer() {
	
				@Override
				public void run() {
					setFiltersData();
				}
			};
			filterUpdate.scheduleRepeating(FILTER_UPDATE_TIME);
		}
		
		if (isColumnConfigurable()) {
			final Label label = new Label("...");
			label.setStyleName(Resources.INSTANCE.css().columnSelector());
			
			Event.sinkEvents(label.getElement(), Event.ONCLICK);
			Event.setEventListener(label.getElement(), new EventListener() {

				@Override
				public void onBrowserEvent(Event event) {
					if (Event.ONCLICK == event.getTypeInt()) {
						columnSelectorPopup.show();
						columnSelectorPopup.setPopupPosition(label.getElement().getAbsoluteLeft() - 200,
								label.getElement().getAbsoluteTop() + 20);
					}
				}
			});
			getDataGrid().getElement().appendChild(label.getElement());
		
			columnSelectorPopup.setColumnList(columnsLabel);
			columnSelectorPopup.setVisibleColumns(new ArrayList<>(columns.keySet())
					.subList(0, NUMBER_OF_VISIBLE_COLUMNS));
		}
		
		if (isExportable()) {
			final CustomImageButton btnExport = new CustomImageButton();
			btnExport.setResource(ImageResources.INSTANCE.export());
			btnExport.setPixelSize(24, 24);
			btnExport.setStyleName(Resources.INSTANCE.css().exportBtn());
			btnExport.setTitle("Export To TSV");
			
			Event.sinkEvents(btnExport.getElement(), Event.ONCLICK);
			Event.setEventListener(btnExport.getElement(), new EventListener() {

				@Override
				public void onBrowserEvent(Event event) {
					if (Event.ONCLICK == event.getTypeInt()) {
						onExport();
					}
				}
			});
			getDataGrid().getElement().appendChild(btnExport.getElement());		
			getDataGrid().getElement().getStyle().setOverflow(Overflow.VISIBLE);
		}
		
		if (canSwitchTimeMode()) {
			final CustomImageButton btnSwitchTime = new CustomImageButton();
			btnSwitchTime.setResource(ImageResources.INSTANCE.time());
			btnSwitchTime.setPixelSize(24, 24);
			btnSwitchTime.setStyleName(Resources.INSTANCE.css().timeBtn());
			btnSwitchTime.setTitle("Switch to absolute/relative time");
			
			Event.sinkEvents(btnSwitchTime.getElement(), Event.ONCLICK);
			Event.setEventListener(btnSwitchTime.getElement(), new EventListener() {

				@Override
				public void onBrowserEvent(Event event) {
					if (Event.ONCLICK == event.getTypeInt()) {
						onSwitchTime();
					}
				}
			});
			getDataGrid().getElement().appendChild(btnSwitchTime.getElement());		
			getDataGrid().getElement().getStyle().setOverflow(Overflow.VISIBLE);
		}
	}

	public boolean isFilterable() {
		return true;
	}

	/**
	 * Gets the column name in the current language
	 * 
	 * @param columnName
	 * @return
	 */
	private String geti18nColumnName(String columnName) {
		switch (columnName.toLowerCase()) {
		case "time":
			return constants.time();
		case "team":
			return constants.team();
		case "group_color":
			return "";
		case "group":
			return constants.group();
		case "problem":
			return constants.problem();
		case "problem_color":
			return "";
		case "classification":
			return constants.classification();
		case "subject":
			return constants.subject();
		case "state":
			return constants.state();
		case "rank":
			return constants.rank();
		case "solved":
			return constants.solved();
		case "points":
			return constants.points();
		case "mark":
			return constants.mark();

		default:
			return columnName;
		}
	}

	@Override
	public void setFiltersData() {
		for (String column : columnFilters.keySet()) {
			CategorizedColumnFilter filter = columnFilters.get(column);

			Set<String> data = new HashSet<>();
			for (Row row : getDataProvider().getUnfilteredList())
				data.add(row.getValue(column));

			filter.setDataList(data);
		}
	}

	@Override
	public void updateDataGrid() {

		Map<String, List<String>> filters = new HashMap<String, List<String>>();
		for (int i = 0; i < getDataGrid().getColumnCount(); i++) {
			String columnName = ((String) getDataGrid().getHeader(i).getKey())
					.toLowerCase();
			CategorizedColumnFilter cell;
			if ((cell = columnFilters.get(columnName)) != null) {
				List<String> allowed = cell.getSelectionListbox();
				if (!allowed.isEmpty())
					filters.put(columnName, allowed);
			}

		}

		getDataProvider().setFilters(filters);

		getPager().setDisplay(getDataGrid());

	}

	public abstract boolean getSortable();

	public abstract ListingDataProvider getDataProvider();

	public abstract DataGrid<Row> getDataGrid();

	public abstract SimplePager getPager();
	
	public boolean canReplaceSubmission() {
		return false;
	}
	
	public void replaceSubmission(String id, String team, String problem) {
		
	}

	public void getParticipantLogged() {
	}

	public void getShowOwnCode() {
	}
	
	public void setParticipant(String participant) {
		this.participant = participant;
		getDataGrid().redraw();
	}
	
	public void setShowOwnCode(boolean show) {
		this.showSubmissionCode  = show;
		getDataGrid().redraw();
	}
	
	/**
	 * Exports the table as a tab-separated file
	 */
	public void onExport() {
		List<String> columnNames = new ArrayList<>();
		for (String key : columns.keySet()) {
			if (!getDataGrid().getColumnWidth(columns.get(key)).equals("0"))
				columnNames.add(key);
		}
		
		String exported = getDataProvider().exportDataToTSV(columnNames);
		FileDownloader.downloadFile(getExportFileName(), exported);
	}
	
	/**
	 * Switch time between absolute and relative
	 */
	public void onSwitchTime() {
		absoluteTime = !absoluteTime;
		getDataGrid().redraw();
	}
	
	/**
	 * File name of the exported file
	 */
	public String getExportFileName() {
		if (getDataProvider().getKind() != null)
			return getDataProvider().getKind().toString().toLowerCase() + "-table.tsv";
		else
			return "table.tsv";
	}
	
	
	public boolean isColumnConfigurable() {
		return columns.size() > NUMBER_OF_VISIBLE_COLUMNS;
	}
	
	public boolean isExportable() {
		return true;
	}
	
	@Override
	public void hideColumn(String columnName) {
		Column<Row, String> column = columns.get(columnName);
		getDataGrid().setColumnWidth(column, "0");
		getDataGrid().redraw();
	}
	
	@Override
	public void showColumn(String columnName) {
		Column<Row, String> column = columns.get(columnName);
		getDataGrid().setColumnWidth(column, columnsWidth.get(columnName));
		getDataGrid().redraw();
	}

	public boolean showColors() {
		return false;
	}
	
	public boolean canSwitchTimeMode() {
		if (getDataProvider().getKind().equals(Kind.RANKINGS))
			return false;
		return true;
	}
	
}
