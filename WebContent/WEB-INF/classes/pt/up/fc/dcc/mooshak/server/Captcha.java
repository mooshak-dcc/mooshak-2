package pt.up.fc.dcc.mooshak.server;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A captcha for keeping registration safe from robots 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Captcha extends HttpServlet {
	private static final Color BACKGROUND = new Color(11*16+11,11*16+11,12*16+12);

	private static final long serialVersionUID = 1L;

	private static final int CHALLENGE_SIZE = 5;
	private static final int HEIGHT = 50;
	private static final int WIDTH = 150;
	private static final int FONT_SIZE = 24;
	private static final int SEP = 3;
	private static final int MAX_LINE_WIDTH = 3;
	private static final double MAX_ROT = Math.PI/6;

	
	private static final Random RANDOM = new Random();
	private static final List<Color> COLORS = Arrays.asList(
			Color.ORANGE,Color.PINK,
			Color.CYAN,Color.MAGENTA,
			Color.RED,Color.GREEN,Color.BLUE,
			Color.GRAY,Color.DARK_GRAY);

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		process(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(
			HttpServletRequest request, 
			HttpServletResponse response) 
			throws ServletException, IOException {
		process(request,response);
	}

	private void process(
			HttpServletRequest request,
			HttpServletResponse response) 
			throws ServletException, IOException {
		

		HttpSession httpSession = request.getSession();
		String challenge = getChallenge();
		
		httpSession.setAttribute("captcha", challenge);

		response.setContentType("image/png");
		ServletOutputStream stream = response.getOutputStream();
		ImageIO.write(makeCaptcha(challenge.toCharArray()), "png", stream);

		stream.close();

	}	
	
	private String getChallenge() {
		return String.format("%0"+CHALLENGE_SIZE+"d",
				RANDOM.nextInt((int) Math.pow(10, CHALLENGE_SIZE)));
	}
	
	private BufferedImage makeCaptcha(char[] text) {
		
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, 
				BufferedImage.TYPE_INT_RGB); 
		
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		AffineTransform base = graphics.getTransform();
		Font font = new Font("Serif",Font.BOLD,FONT_SIZE);
		FontMetrics metrics = graphics.getFontMetrics(font);
		
		
		int advance = metrics.getMaxAdvance();
		int ascent = metrics.getAscent();
		
		graphics.setFont(font);
		graphics.setColor(BACKGROUND);
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		graphics.setColor(Color.BLACK);
		int x = 2*SEP;
		int y;
		double r;
		for(int i=0; i < text.length; i++) {
			
			y =  HEIGHT/2 + ascent /2 - RANDOM.nextInt(ascent)/2;
			r = RANDOM.nextDouble()* 2*MAX_ROT - MAX_ROT; 
			graphics.setColor(COLORS.get(RANDOM.nextInt(COLORS.size())));
			graphics.rotate(r,x+advance/2,y+ascent/2);
			graphics.drawChars(text, i, 1,x,y);
			graphics.setTransform(base);
			
			x += (advance/2 + SEP-RANDOM.nextInt(2*SEP));
		}
		Polygon poly = new Polygon();
		
		graphics.setColor(COLORS.get(RANDOM.nextInt(COLORS.size())));
		graphics.setStroke(new BasicStroke(RANDOM.nextInt(MAX_LINE_WIDTH)));
		
		poly.addPoint(1, RANDOM.nextInt(HEIGHT-1));
		poly.addPoint(WIDTH-1, RANDOM.nextInt(HEIGHT-1));
		poly.addPoint(RANDOM.nextInt(WIDTH-1),1);
		poly.addPoint(RANDOM.nextInt(WIDTH-1),HEIGHT-1);
		
		graphics.drawPolygon(poly);
		
		return image;
	}

}
