package pt.up.fc.dcc.mooshak.print;

import java.awt.*;
import java.awt.print.*;
import java.awt.geom.*;
import java.awt.font.*;

import javax.swing.JEditorPane;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.util.*;

/**
 * This class implements the Pageable and Printable interfaces and allows the
 * contents of any JTextComponent to be printed using the java.awt.print
 * printing API.
 **/
@Deprecated
public class PrintableDocument implements Pageable, Printable {
	
	private String config = "";
	
	private View root; // The root View to be printed
	private PageFormat format; // Paper plus page orientation
	private double scalefactor; // How much to scale before printing
	private int numPages; // How many pages in the document
	private double printX, printY; // coordinates of upper-left of print area
	private double printWidth; // Width of the printable area
	private double printHeight; // Height of the printable area
	private Rectangle drawRect; // The rectangle in which the document is painted
	private boolean coverPage = false;

	private boolean paginate = true;
	
	// How lenient are we with the bottom margin in widow/orphan prevention?
	static final double MARGIN_ADJUST = .97;

	// The font we use for printing page numbers
	static final Font headerFont = new Font("Serif", Font.PLAIN, 12);

	public PrintableDocument(PageFormat format,
			String text, double scalefactor, boolean hasCoverPage, boolean paginate) {
		this(new Font(Font.SANS_SERIF, Font.PLAIN, 12), text, format,
				scalefactor, hasCoverPage, paginate);
	}

	public PrintableDocument(String fontName, int size, PageFormat format,
			String text, double scalefactor, boolean hasCoverPage, boolean paginate) {
		this(new Font(fontName, Font.PLAIN, size), text, format,
				scalefactor, hasCoverPage, paginate);
	}

	/**
	 * This constructor allows the contents of any JTextComponent to be printed,
	 * using any specified PageFormat object and any scaling factor.
	 **/
	public PrintableDocument(Font font, String text, PageFormat format,
			double scalefactor, boolean hasCoverPage, boolean paginate) {
		
		this.paginate  = paginate;
		this.coverPage = hasCoverPage;
		
		// Add the page with content to print
		JTextComponent textComponent = new JEditorPane();

		// add a HTMLEditorKit to the editor pane
		HTMLEditorKit kit = new HTMLEditorKit();
		((JEditorPane) textComponent).setEditorKit(kit);
		
		// add styles to the template
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule(config);

		((JEditorPane) textComponent).setContentType("text/html;charset=UTF-8");
		((JEditorPane) textComponent).setDocument(kit.createDefaultDocument());

		((JEditorPane) textComponent).setFont(font);
		((JEditorPane) textComponent).setText(text.replaceAll("&nbsp;", " "));
		
		// Remember the page format, and ask it for the printable area
		this.format = format;
		this.scalefactor = scalefactor;
		this.printX = format.getImageableX() / scalefactor;
		this.printY = format.getImageableY() / scalefactor;
		this.printWidth = format.getImageableWidth() / scalefactor;
		this.printHeight = format.getImageableHeight() / scalefactor;

		// Get the document and its root Element from the text component
		Document document = textComponent.getDocument();
		Element rootElement = document.getDefaultRootElement();
		// Get the EditorKit and its ViewFactory from the text component
		EditorKit editorKit = textComponent.getUI().getEditorKit(textComponent);
		ViewFactory viewFactory = editorKit.getViewFactory();

		// Use the ViewFactory to create a root View object for the document
		// This is the object we'll print.
		root = viewFactory.create(rootElement);

		// The Swing text architecture requires us to call setParent() on
		// our root View before we use it for anything. In order to do this,
		// we need a View object that can serve as the parent. We use a
		// custom implementation defined below.
		root.setParent(new ParentView(root, viewFactory, textComponent));

		// Tell the view how wide the page is; it has to format itself
		// to fit within this width. The height doesn't really matter here
		root.setSize((float) printWidth, (float) printHeight);

		// Now that the view has formatted itself for the specified width,
		// Ask it how tall it is.
		double documentHeight = root.getPreferredSpan(View.Y_AXIS);

		// Set up the rectangle that tells the view where to draw itself
		// We'll use it in other methods of this class.
		drawRect = new Rectangle(0, 0, (int) printWidth, (int) documentHeight);

		// Now if the document is taller than one page, we have to
		// figure out where the page breaks are.
		paginate(root, drawRect);

		// Once we've broken it into pages, figure out how many pages.
		numPages = pageLengths.size();
		if(coverPage)
			numPages++;
	}

	/**
	 * @return the config
	 */
	public String getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(String config) {
		this.config = config;
	}

	/**
	 * @return the format
	 */
	public PageFormat getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(PageFormat format) {
		this.format = format;
	}

	/**
	 * @return the scalefactor
	 */
	public double getScalefactor() {
		return scalefactor;
	}

	/**
	 * @param scalefactor the scalefactor to set
	 */
	public void setScalefactor(double scalefactor) {
		this.scalefactor = scalefactor;
	}

	// This is the starting offset of the page we're currently working on
	double pageStart = 0;

	/**
	 * This method loops through the children of the specified view, recursing
	 * as necessary, and inserts pages breaks when needed. It makes a
	 * rudimentary attempt to avoid "widows" and "orphans".
	 **/
	protected void paginate(View v, Rectangle2D allocation) {
		// Figure out how tall this view is, and tell it to allocate
		// that space among its children
		double myheight = v.getPreferredSpan(View.Y_AXIS);
		v.setSize((float) printWidth, (float) myheight);

		// Now loop through each of the children
		int numkids = v.getViewCount();
		for (int i = 0; i < numkids; i++) {
			View kid = v.getView(i); // this is the child we're working with
			// Figure out its size and location
			Shape kidshape = v.getChildAllocation(i, allocation);
			if (kidshape == null)
				continue;
			Rectangle2D kidbox = kidshape.getBounds2D();

			// This is the Y coordinate of the bottom of the child
			double kidpos = kidbox.getY() + kidbox.getHeight() - pageStart;

			// If this is the first child of a group, then we want to ensure
			// that it doesn't get left by itself at the bottom of a page.
			// I.e. we want to prevent "widows"
			if ((numkids > 1) && (i == 0)) {
				// If it is not near the end of the page, then just move
				// on to the next child
				if (kidpos < printY + printHeight * MARGIN_ADJUST)
					continue;

				// Otherwise, the child is near the bottom of the page, so
				// break the page before this child and place this child on
				// the new page.
				breakPage(kidbox.getY());
				continue;
			}

			// If this is the last child of a group, we don't want it to
			// appear by itself at the top of a new page, so allow it to
			// squeeze past the bottom margin if necessary. This helps to
			// prevent "orphans"
			if ((numkids > 1) && (i == numkids - 1)) {
				// If it fits normally, just move on to the next one
				if (kidpos < printY + printHeight)
					continue;

				// Otherwise, if it fits with extra space, then break the
				// page at the end of the group
				if (kidpos < printY + printHeight / MARGIN_ADJUST) {
					breakPage(allocation.getY() + allocation.getHeight());
					continue;
				}
			}

			// If the child is not the first or last of a group, then we use
			// the bottom margin strictly. If the child fits on the page,
			// then move on to the next child.
			if (kidpos < printY + printHeight)
				continue;

			// If we get here, the child doesn't fit on this page. If it has
			// no children, then break the page before this child and continue.
			if (kid.getViewCount() == 0) {
				breakPage(kidbox.getY());
				continue;
			}

			// If we get here, then the child did not fit on the page, but it
			// has kids of its own, so recurse to see if any of those kids
			// will fit on the page.
			paginate(kid, kidbox);
		}
	}

	// For a document of n pages, this list stores the lengths of pages
	// 0 through n-2. The last page is assumed to have a full length
	ArrayList<Double> pageLengths = new ArrayList<Double>();

	// For a document of n pages, this list stores the starting offset of
	// pages 1 through n-1. The offset of page 0 is always 0
	ArrayList<Double> pageOffsets = new ArrayList<Double>();

	/**
	 * Break a page at the specified Y coordinate. Store the necessary
	 * information into the pageLengths and pageOffsets lists
	 **/
	void breakPage(double y) {
		double pageLength = y - pageStart - printY;
		pageStart = y - printY;
		pageLengths.add(new Double(pageLength));
		pageOffsets.add(new Double(pageStart));
	}

	/** Return the number of pages. This is a Pageable method. */
	public int getNumberOfPages() {
		return numPages;
	}

	/**
	 * Return the PageFormat object for the specified page. This is a Pageable
	 * method. This implementation uses the computed length of the page in the
	 * returned PageFormat object. The PrinterJob will use this as a clipping
	 * region, which will prevent extraneous parts of the document from being
	 * drawn in the top and bottom margins.
	 **/
	public PageFormat getPageFormat(int pagenum) {
		// On the last page, just return the user-specified page format
		if (pagenum == numPages - 1)
			return format;

		// Otherwise, look up the height of this page and return an
		// appropriate PageFormat.
		double pageLength = ((Double) pageLengths.get(pagenum)).doubleValue();
		PageFormat f = (PageFormat) format.clone();
		Paper p = f.getPaper();
		if (f.getOrientation() == PageFormat.PORTRAIT)
			p.setImageableArea(printX * scalefactor, printY * scalefactor,
					printWidth * scalefactor, pageLength * scalefactor);
		else
			p.setImageableArea(printY * scalefactor, printX * scalefactor,
					pageLength * scalefactor, printWidth * scalefactor);
		f.setPaper(p);
		return f;
	}

	/**
	 * This Pageable method returns the Printable object for the specified page.
	 * Since this class implements both Pageable and Printable, it just returns
	 * this.
	 **/
	public Printable getPrintable(int pagenum) {
		return this;
	}

	/**
	 * This is the basic Printable method that prints a specified page
	 **/
	public int print(Graphics g, PageFormat format, int pageIndex) {
		if(coverPage)
			pageIndex = pageIndex - 1;
		
		// Return an error code on attempts to print past the end of the doc
		if (coverPage && pageIndex >= numPages 
				|| !coverPage && pageIndex > numPages)
			return NO_SUCH_PAGE;
		
		// Cast the Graphics object so we can use Java2D operations
		Graphics2D g2 = (Graphics2D) g;

		// Translate to accomodate the top and left margins
		g2.translate(format.getImageableX(), format.getImageableY());

		// Scale the page by the specified scaling factor
		g2.scale(scalefactor, scalefactor);

		// Display a page number centered in the area of the top margin.
		// Set a new clipping region so we can draw into the top margin
		// But remember the original clipping region so we can restore it
		if (pageIndex >= 0) {
			Shape originalClip = g.getClip();
			g.setClip(new Rectangle(0, (int) -printY, (int) printWidth,
					(int) printY));
			
			g.setFont(headerFont); // Set the font
			g.setColor(Color.black); // Print with black ink
			// Get string and font measurements
			FontRenderContext frc = g2.getFontRenderContext();
			
			if (paginate) {
				// Compute the header to display, measure it, then display it
				String numString = "- " + (pageIndex + 1) + " -";
				Rectangle2D numBounds = headerFont.getStringBounds(numString, frc);
				LineMetrics metrics = headerFont.getLineMetrics(numString, frc);
				g.drawString(
						numString, // Display the string
						(int) ((printWidth - numBounds.getWidth()) / 2),
						(int) (-(printY - numBounds.getHeight()) / 2 + metrics
								.getAscent()));
			}
			g.setClip(originalClip); // Restore the clipping region
		}

		// Get the staring position and length of the page within the document
		double pageStart = 0.0;
		if (pageIndex > 0)
			pageStart = ((Double) pageOffsets.get(pageIndex - 1)).doubleValue();
		if (pageIndex < numPages - 1)
			((Double) pageLengths.get(pageIndex)).doubleValue();

		// Scroll so that the appropriate part of the document is lined up
		// with the upper-left corner of the page
		g2.translate(0.0, -pageStart);

		// Now paint the entire document. Because of the clipping region,
		// only the desired portion of the document will actually be drawn on
		// this sheet of paper.
		root.paint(g, drawRect);
		
		// Finally return a success code
		return PAGE_EXISTS;
	}

	/**
	 * This inner class is a concrete implementation of View, with a couple of
	 * key method implementations. An instance of this class is used as the
	 * parent of the root View object we want to print
	 **/
	static class ParentView extends View {
		ViewFactory viewFactory; // The ViewFactory for the hierarchy of views
		Container container; // The Container for the hierarchy of views

		public ParentView(View v, ViewFactory viewFactory, Container container) {
			super(v.getElement());
			this.viewFactory = viewFactory;
			this.container = container;
		}

		// These methods return key pieces of information required by
		// the View hierarchy.
		public ViewFactory getViewFactory() {
			return viewFactory;
		}

		public Container getContainer() {
			return container;
		}

		// These methods are abstract in View, so we've got to provide
		// dummy implementations of them here, even though they're never used.
		public void paint(Graphics g, Shape allocation) {
		}

		public float getPreferredSpan(int axis) {
			return 0.0f;
		}

		public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
			return 0;
		}

		public Shape modelToView(int pos, Shape a, Position.Bias b)
				throws BadLocationException {
			return a;
		}
	}
}
