package pt.up.fc.dcc.mooshak.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Prints the cover/intro page of a Mooshak document for printing purposes
 * 
 * @author josepaiva
 */
public class IntroPage implements Printable {

	private final static int POINTS_PER_INCH = 72;

	private String config = "";

	private Font font = null;

	/**
	 * 
	 * @param font
	 *            the font to use on printing
	 * @param printout
	 */
	public IntroPage(Font font) {
		this.font = font;
	}

	/**
	 * Uses SANS_SERIF font with size 24px
	 * 
	 * @param printout
	 */
	public IntroPage() {
		font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	}

	/**
	 * 
	 * @param fontName
	 *            the font name. This can be a font face name or a font family
	 *            name
	 * @param size
	 *            the point size of the Font
	 */
	public IntroPage(String fontName, int size) {
		font = new Font(fontName, Font.PLAIN, size);
	}

	/**
	 * @return the config
	 */
	public String getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		Graphics2D graphics2d = (Graphics2D) graphics;

		// Translate the origin to 0,0 for the top left corner
		graphics2d.translate(pageFormat.getImageableX(),
				pageFormat.getImageableY());

		// Print the title
		String titleText = "Mooshak";
		Font titleFont = new Font("helvetica", Font.BOLD, 36);
		graphics.setFont(titleFont);

		// Compute the horizontal center of the page
		FontMetrics fontMetrics = graphics.getFontMetrics();
		double titleX = (pageFormat.getImageableWidth() / 2)
				- (fontMetrics.stringWidth(titleText) / 2);
		double titleY = 3 * POINTS_PER_INCH;
		graphics.drawString(titleText, (int) titleX, (int) titleY);

		// Set the default drawing color to black
		graphics.setColor(Color.BLACK);

		// Print the table
		graphics.setFont(font);

		return PAGE_EXISTS;
	}

}
