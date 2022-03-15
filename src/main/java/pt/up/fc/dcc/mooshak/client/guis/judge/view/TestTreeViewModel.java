package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.guis.judge.data.Test;
import pt.up.fc.dcc.mooshak.client.guis.judge.data.TestField;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class TestTreeViewModel implements TreeViewModel {

	private AbstractDataProvider<Test> provider;

	/**
	 * This selection model is shared across all leaf nodes. A selection model
	 * can also be shared across all nodes in the tree, or each set of child
	 * nodes can have its own instance. This gives flexibility to determine how
	 * nodes are selected.
	 */
	private final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();

	public TestTreeViewModel(AbstractDataProvider<Test> dataProvider) {
		provider = dataProvider;
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// LEVEL 0 - We passed null as the root value. Return the tests.
			// Create a cell to display a test.
			Cell<Test> cell = new AbstractCell<Test>() {

				@Override
				public void render(Context context, Test value,
						SafeHtmlBuilder sb) {
					if (value != null) {
						String classify = getClassify(value);
						if (classify != null) {
							switch (classify) {
							case "ACCEPTED":
							case "Accepted":
							case "accepted":
								sb.appendHtmlConstant("<span style=\"color:green;\">");
								sb.appendEscaped(value.getName());
								sb.appendHtmlConstant("</span>");
								break;

							default:
								sb.appendHtmlConstant("<span style=\"color:red;\">");
								sb.appendEscaped(value.getName());
								sb.appendHtmlConstant("</span>");
								break;
							}
						} else
							sb.appendEscaped(value.getName());
					}
				}
			};

			// Return a node info that pairs the data provider and the cell.
			return new DefaultNodeInfo<Test>((ListDataProvider<Test>) provider,
					cell);
		} else if (value instanceof Test) {
			// LEVEL 1 - We want the children of the Test. Return the TestField.
			Cell<TestField> cell = new AbstractCell<TestField>() {

				@Override
				public void render(Context context, TestField value,
						SafeHtmlBuilder sb) {
					if (value != null) {
						sb.appendEscaped(value.getFieldName());
					}
				}
			};
			return new DefaultNodeInfo<TestField>(
					new ListDataProvider<TestField>(((Test) value).getFields()),
					cell);
		} else if (value instanceof TestField) {
			// LEVEL 2 - We want the field value. Return the field value.
			List<String> values = new ArrayList<String>();
			values.add(((TestField) value).getFieldValue());

			// Use the shared selection model.
			return new DefaultNodeInfo<String>(new ListDataProvider<String>(
					values), new TextCell(), selectionModel, null);
		}

		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		// The leaf nodes are field values which are Strings
		if (value instanceof String) {
			return true;
		}
		return false;
	}

	private String getClassify(Test value) {
		List<TestField> tests = value.getFields();
		for (TestField testField : tests) {
			if (testField.getFieldName().equals("Classify"))
				return testField.getFieldValue();
		}
		return null;
	}

}
