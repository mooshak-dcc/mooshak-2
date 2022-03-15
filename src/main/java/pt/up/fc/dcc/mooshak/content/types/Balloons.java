package pt.up.fc.dcc.mooshak.content.types;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

public class Balloons extends PersistentContainer<Balloon> 
	implements HasListingRows, Preparable {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute( 
			name="List-Pending",
			type=AttributeType.MENU,
			tip = "Include balloons in Pending listing")
	private YesNo listPending;
	
	@MooshakAttribute( 
			name="Balloon",
			type=AttributeType.CONTENT)
	private Void balloon;
	
	
	/**
	 * Are balloons entries included pending listing? 
	 * @return
	 */
	public boolean isListPending() {
		if(YesNo.YES.equals(listPending))
			return true;
		else
			return false;
	}

	/**
	 * Define if balloons entries are included pending listing
	 * @param listPending
	 */
	public void setListPending(boolean listPending) {
		if(listPending)
			this.listPending = YesNo.YES;
		else
			this.listPending = YesNo.NO;
	}
	
	/*---------------------------------------------------------------------*\
	 *                   Listing support                                   *
	 *                                                                     *
	 *                                                                     *
	 * (non-Javadoc)                                                       *
	 * @see pt.up.fc.dcc.mooshak.content.types.HasListingRows#getRows()    *
	 * @see pt.up.fc.dcc.mooshak.content.types.HasListingRows#getColumns() *
	\*---------------------------------------------------------------------*/
	
	@Override
	public POStream<? extends HasListingRow> getRows() {
		
		return  newPOStream();
	}
	
	@Override
	public List<ColumnInfo> getColumns() {
		
		List<ColumnInfo> columns = new ArrayList<>();
		
		columns.add(new ColumnInfo("time", 20, ColumnType.TIME));
		columns.addAll(ColumnInfo.addColumns("id"));
		columns.add(new ColumnInfo("team", 20, ColumnType.TEAM));
		columns.addAll(ColumnInfo.addColumns("group"));
		columns.add(new ColumnInfo("problem", 20, ColumnType.PROBLEM));
		columns.addAll(ColumnInfo.addColumns("state"));
		
		return columns;
	}

	@Override
	public void prepare() throws MooshakException {
		try(POStream<Balloon> stream = newPOStream()) {
			for(Balloon balloon: stream)
				balloon.delete();
		} catch (Exception cause) {
			throw new MooshakException("Error iterating over balloons",cause);
		} 
		
	}	

	
}
