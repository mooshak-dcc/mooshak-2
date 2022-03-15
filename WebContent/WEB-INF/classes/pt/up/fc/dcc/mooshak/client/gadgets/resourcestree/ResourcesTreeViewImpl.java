package pt.up.fc.dcc.mooshak.client.gadgets.resourcestree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.enki.resources.EnkiResources;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Course;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CoursePart;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class ResourcesTreeViewImpl extends Composite implements ResourcesTreeView {

	private static final ImageResource MULTIMEDIA_RESOURCE = EnkiResources.INSTANCE.multimedia();
	private static final ImageResource BOOK_RESOURCE = EnkiResources.INSTANCE.book();
	private static final ImageResource EXERCISE_RESOURCE = EnkiResources.INSTANCE.exercise();
	private static final ImageResource TICK = EnkiResources.INSTANCE.tick();
	private static final ImageResource STAR = EnkiResources.INSTANCE.star();
	private static final ImageResource AUXILIARY = EnkiResources.INSTANCE.auxiliary();

	private static ResourcesTreeUiBinder uiBinder = GWT.create(ResourcesTreeUiBinder.class);

	@UiTemplate("ResourcesTreeView.ui.xml")
	interface ResourcesTreeUiBinder extends UiBinder<Widget, ResourcesTreeViewImpl> {
	}

	interface BaseStyle extends CssResource {
		String darken();

		String seen();

		String available();

		String unavailable();

		String solved();

		String recommended();

		String auxiliary();
		
		@ClassName("menu-state-icon")
		String menuStateIcon();
	}

	private EnkiConstants constants = GWT.create(EnkiConstants.class);

	@UiField
	static BaseStyle style;

	private Presenter presenter;

	@UiField(provided = true)
	static CellTree tree;

	private CustomTreeModel treeModel;

	private String courseId = null;

	public ResourcesTreeViewImpl() {
		treeModel = new CustomTreeModel();

		tree = new CellTree(treeModel, null);

		initWidget(uiBinder.createAndBindUi(this));

		tree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		treeModel.selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent originalEvent) {
				ValueChangeEvent<Object> event = new ValueChangeEvent<Object>(
						treeModel.selectionModel.getSelectedObject()) {
				};

				if (event.getValue() == null || event.getValue() instanceof CoursePart)
					return;

				CourseResource courseResource = (CourseResource) event.getValue();
				if (courseResource.getState() == null || courseResource.getState().equals(ResourceState.UNAVAILABLE)) {
					Window.alert(constants.unavailableText());
					treeModel.selectionModel.setSelected(event.getValue(), false);
					return;
				}

				presenter.onSelectedResourceChanged(courseId, courseResource.getId(), courseResource.getTitle(),
						courseResource.getType(), courseResource.getHref(), courseResource.getLearningTime(),
						courseResource.getLanguage());
			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	/**
	 * Cell used to render the Course
	 * 
	 * @author josepaiva
	 */
	private static class CourseCell extends AbstractCell<Course> {

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Course value,
				NativeEvent event, ValueUpdater<Course> valueUpdater) {
			String eventType = event.getType();
			// Special case the ENTER key for a unified user experience.
			if ("click".equals(eventType)
					|| ("keydown".equals(eventType) && event.getKeyCode() == KeyCodes.KEY_ENTER)) {
				tree.getRootTreeNode().setChildOpen(context.getIndex(),
						!tree.getRootTreeNode().isChildOpen(context.getIndex()));
			}
		}

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, Course value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName());
			}
		}
	}

	/**
	 * Cell used to render a part of the course
	 * 
	 * @author josepaiva
	 */
	private static class PartCell extends AbstractCell<Object> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
			if (value != null) {
				if (value instanceof CoursePart)
					sb.appendEscaped(((CoursePart) value).getName());
				else {
					CourseResource courseResource = (CourseResource) value;

					sb.appendHtmlConstant("<table style='margin-left:-20px;'>");
					sb.appendHtmlConstant("<tr><td style='padding:5px;position:relative;'>");
					
					if (courseResource.getState() != null && (courseResource.getState()
							.equals(ResourceState.SOLVED) || courseResource.getState()
							.equals(ResourceState.SEEN))) {
						Image htmlImage = new Image(TICK);
						htmlImage.setSize("16px", "16px");
						htmlImage.setStyleName(style.menuStateIcon());
						sb.appendHtmlConstant(htmlImage.toString());
					} else if (courseResource.getState() != null && courseResource.getState()
							.equals(ResourceState.RECOMMENDED)) {
						Image htmlImage = new Image(STAR);
						htmlImage.setSize("16px", "16px");
						htmlImage.setStyleName(style.menuStateIcon());
						sb.appendHtmlConstant(htmlImage.toString());
					} else if (courseResource.getState() != null && courseResource.getState()
							.equals(ResourceState.AUXILIARY)) {
						Image htmlImage = new Image(AUXILIARY);
						htmlImage.setSize("16px", "16px");
						htmlImage.setStyleName(style.menuStateIcon());
						sb.appendHtmlConstant(htmlImage.toString());
					}

					ImageResource image = null;
					switch (courseResource.getType()) {
					case PROBLEM:
						image = EXERCISE_RESOURCE;
						break;
					case PDF:
						image = BOOK_RESOURCE;
						break;
					case VIDEO:
						image = MULTIMEDIA_RESOURCE;
						break;
					}
					Image htmlImage = new Image(image);
					htmlImage.setSize("24px", "24px");
					htmlImage.setStyleName(style.darken());
					sb.appendHtmlConstant(htmlImage.toString());
					sb.appendHtmlConstant("</td>");

					String state = "";
					switch (courseResource.getState() == null ? ResourceState.UNAVAILABLE : courseResource.getState()) {
					case AVAILABLE:
						state = style.available();
						break;
					case UNAVAILABLE:
						state = style.unavailable();
						break;
					case SEEN:
						state = style.seen();
						break;
					case SOLVED:
						state = style.solved();
						break;
					case RECOMMENDED:
						state = style.recommended();
						break;
					case AUXILIARY:
						state = style.auxiliary();
						break;
					default:
						break;
					}

					sb.appendHtmlConstant("<td style='font-size:95%;' class='" + state + "'>");
					sb.appendEscaped(courseResource.getTitle());
					sb.appendHtmlConstant("</td></tr>");
					sb.appendHtmlConstant("</table>");
				}

			}
		}
	}

	/**
	 * The model that defines the nodes in the tree.
	 */
	private static class CustomTreeModel implements TreeViewModel {

		private CourseList resources = null;

		private ListDataProvider<Course> coursesProvider = null;
		private Map<Course, ListDataProvider<Object>> coursePartsProviders = new HashMap<Course, ListDataProvider<Object>>();
		private Map<CoursePart, ListDataProvider<Object>> partsProviders = new HashMap<CoursePart, ListDataProvider<Object>>();

		SingleSelectionModel<Object> selectionModel = new SingleSelectionModel<Object>();

		private NoSelectionModel<Object> courseSM = new NoSelectionModel<>();
		private NoSelectionModel<Object> coursePartSM = new NoSelectionModel<>();
		
		// TODO unlimited depth of parts
		/*private NoSelectionModel<Object> partSM = new NoSelectionModel<>();*/

		public CustomTreeModel() {
			courseSM.addSelectionChangeHandler(new Handler() {
	
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					int index = coursesProvider.getList().indexOf(courseSM.getLastSelectedObject());
					TreeNode rootTreeNode = tree.getRootTreeNode();
					rootTreeNode.setChildOpen(index, !rootTreeNode.isChildOpen(index));
				}
			});
			
			coursePartSM.addSelectionChangeHandler(new Handler() {
	
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					
					for (Course course : coursesProvider.getList()) {
						ListDataProvider<Object> coursePartsProvider = coursePartsProviders.get(course);
						int index = coursePartsProvider.getList().indexOf(coursePartSM.getLastSelectedObject());
						if (index != -1) {
							TreeNode parentNode = tree.getRootTreeNode().setChildOpen(
									coursesProvider.getList().indexOf(course), true);
							parentNode.setChildOpen(index, !parentNode.isChildOpen(index));
						}
					}
				}
			});
		}
		
		/**
		 * Get the resource on the success
		 * @param id
		 * @return the resource on the success
		 */
		public CourseResource getOnSuccessResource(String id) {
			
			CourseResource current = getResource(id);
			
			if (current == null)
				return null;
			
			String onSuccess = current.getOnSuccess();
			
			return getResource(onSuccess);
		}
		
		/**
		 * Get the resource with given id
		 * @param id
		 * @return the resource with given id
		 */
		public CourseResource getResource(String id) {
			
			for (Course course : resources.getCourses()) {
				
				for (Object object : coursePartsProviders.get(course).getList()) {
					
					CourseResource resource = getResource(id, object);
					if (resource != null)
						return resource;
				}
			}
			
			return null;
		}
		
		/**
		 * Get the resource 
		 * @param id
		 * @param object part to begin the search
		 * @return the resource
		 */
		public CourseResource getResource(String id, Object object) {
			
			for (Object resourceObj : partsProviders.get(object).getList()) {
				
				if (resourceObj instanceof CourseResource) {
					CourseResource courseResource = (CourseResource) resourceObj;
					if (id.equals(courseResource.getId()))
						return courseResource;						
				} else {
					CourseResource courseResource = getResource(id, resourceObj);
					
					if (courseResource != null)
						return courseResource;
				}					
			}
			
			return null;
		}

		@Override
		public <T> NodeInfo<?> getNodeInfo(T value) {

			if (value == null) {
				// LEVEL 0
				// We passed null as the root value. Return the courses.
				if (resources != null)
					coursesProvider = new ListDataProvider<Course>(resources.getCourses());
				else
					coursesProvider = new ListDataProvider<Course>(new ArrayList<Course>());

				// Return a node info that pairs the data provider and the cell.
				return new DefaultNodeInfo<Course>(coursesProvider, new CourseCell(),
						courseSM, null);
			} else if (value instanceof Course) {
				// LEVEL 1
				// We want the children of the course.
				ListDataProvider<Object> dataProvider = new ListDataProvider<>();

				if (resources != null) {
					dataProvider.getList().addAll(((Course) value).getChildren());
					coursePartsProviders.put((Course) value, dataProvider);
				} else
					coursePartsProviders.put((Course) value, dataProvider);
				return new DefaultNodeInfo<Object>(coursePartsProviders.get((Course) value), new PartCell(),
						coursePartSM, null);
			} else if (value instanceof CoursePart) {
				// LEVEL 2
				// We want the children of the module.
				ListDataProvider<Object> dataProvider = new ListDataProvider<>();

				if (resources != null) {
					dataProvider.getList().addAll(((CoursePart) value).getChildren());
					dataProvider.getList().addAll(((CoursePart) value).getResources());
					partsProviders.put((CoursePart) value, dataProvider);
				} else
					partsProviders.put((CoursePart) value, dataProvider);

				return new DefaultNodeInfo<Object>(partsProviders.get((CoursePart) value), new PartCell(),
						selectionModel, null);
			} 	/*
				 * else if (value instanceof TopicType) { // LEVEL 3 - Leaf //
				 * We want the children of the topic. Return the resources. if
				 * (resources != null) resourceProviders.put( (TopicType) value,
				 * new ListDataProvider<ResourceType>(((TopicType) value)
				 * .getResource())); else resourceProviders.put((TopicType)
				 * value, new ListDataProvider<ResourceType>( new
				 * ArrayList<ResourceType>()));
				 * 
				 * // Use the shared selection model. return new
				 * DefaultNodeInfo<ResourceType>(
				 * resourceProviders.get((TopicType) value), new ResourceCell(),
				 * selectionModel, null); }
				 */

			return null;
		}

		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof CourseResource) {
				return true;
			}
			return false;
		}

		/**
		 * @return the resources
		 */
		public CourseList getResources() {
			return resources;
		}

		/**
		 * @param resources
		 *            the resources to set
		 */
		public void setResources(CourseList resources) {
			this.resources = resources;
			for (Course course : resources.getCourses()) {
				addCourse(course);
			}
		}

		/**
		 * Add a {@link Course} to the tree
		 * 
		 * @param course
		 */
		public void addCourse(Course course) {
			coursesProvider.getList().add(course);

			for (CoursePart coursePart : course.getChildren()) {
				addCoursePartToCourse(course, coursePart, coursePart.getLanguage());
			}
			coursesProvider.refresh();
		}

		/**
		 * Add a {@link CoursePart} to the course
		 * 
		 * @param course
		 * @param coursePart
		 * @param string 
		 */
		public void addCoursePartToCourse(Course course, CoursePart coursePart, String language) {
			ListDataProvider<Object> coursePartProvider = coursePartsProviders.get(course);
			if (coursePartProvider == null) {
				coursePartProvider = new ListDataProvider<Object>();
				coursePartsProviders.put(course, coursePartProvider);
			}
			
			for (CourseResource res : coursePart.getResources()) {
				if (res.getLanguage() == null)
					res.setLanguage(language);
			}
			
			coursePartProvider.getList().add(coursePart);

			for (CoursePart part : coursePart.getChildren()) {
				addToCoursePart(coursePart, part, part.getLanguage());
			}
			coursePartProvider.refresh();
		}

		/**
		 * Add a {@link CoursePart} or a {@link CourseResource} to the course
		 * 
		 * @param coursePart
		 * @param object
		 */
		public void addToCoursePart(CoursePart coursePart, Object object, String language) {
			ListDataProvider<Object> partProvider = partsProviders.get(coursePart);
			if (partProvider == null) {
				partProvider = new ListDataProvider<Object>();
				partsProviders.put(coursePart, partProvider);
			}
			if (object instanceof CoursePart) {
				if (((CoursePart) object).getLanguage() == null)
					((CoursePart) object).setLanguage(language);
				
				language = ((CoursePart) object).getLanguage();
			} else {
				if (((CourseResource) object).getLanguage() == null)
					((CourseResource) object).setLanguage(language);
			}
			partProvider.getList().add(object);

			if (object instanceof CoursePart) {

				for (CoursePart part : ((CoursePart) object).getChildren()) {
					addToCoursePart(coursePart, part, language);
				}
			}

			partProvider.refresh();
		}

		/**
		 * Add a {@link ResourceType} to the tree
		 * 
		 * @param resource
		 * 
		 *            public void addResource(TopicType topic, ResourceType
		 *            resource) {
		 * 
		 *            ListDataProvider<ResourceType> resourceProvider =
		 *            resourceProviders .get(topic); if (resourceProvider ==
		 *            null) { resourceProvider = new ListDataProvider
		 *            <ResourceType>(); resourceProviders.put(topic,
		 *            resourceProvider); }
		 *            resourceProvider.getList().add(resource);
		 *            resourceProviders.get(topic).refresh(); }
		 */

		public void refresh() {
			if (coursesProvider == null)
				return;

			coursesProvider.refresh();

			for (Course course : coursesProvider.getList()) {
				ListDataProvider<Object> coursePartsProvider = coursePartsProviders.get(course);

				if (coursePartsProvider == null)
					continue;
				coursePartsProvider.refresh();

				for (Object object : coursePartsProvider.getList()) {
					ListDataProvider<Object> partsProvider = partsProviders.get(object);
					if (partsProvider == null)
						continue;
					partsProvider.refresh();

					for (Object object2 : partsProvider.getList()) {
						ListDataProvider<Object> provider = partsProviders.get(object2);
						if (provider == null)
							continue;
						provider.refresh();
					}
				}
			}
		}
	}

	/**
	 * @return the resources
	 */
	public CourseList getResources() {
		return treeModel.getResources();
	}

	/**
	 * @param resources
	 *            the resources to set
	 */
	@Override
	public void setResources(CourseList resources) {
		this.treeModel.setResources(resources);
	}

	@Override
	public void updateResourceState(String courseId, String resourceId, ResourceState state) {

		CourseList courseList = getResources();

		for (Course course : courseList.getCourses()) {
			if (course.getId().equals(courseId)) {

				for (CoursePart coursePart : course.getChildren()) {
					updateResourceStateTmp(coursePart, courseId, resourceId, state);
				}

			}
		}
	}

	private void updateResourceStateTmp(CoursePart coursePart, String courseId, String resourceId,
			ResourceState state) {

		for (CourseResource courseResource : coursePart.getResources()) {
			if (courseResource.getId().equals(resourceId)) {
				courseResource.setState(state);
				treeModel.refresh();
				return;
			}
		}

		for (CoursePart part : coursePart.getChildren()) {
			updateResourceStateTmp(part, courseId, resourceId, state);
		}
	}

	/**
	 * Get the resource to present on successful solving of exercise with given id
	 * 
	 * @param id
	 * @return the resource to present on successful solving of exercise with given id
	 */
	@Override
	public CourseResource getOnSuccessResource(String id) {
		return treeModel.getOnSuccessResource(id);
	}
}
