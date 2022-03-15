package pt.up.fc.dcc.mooshak.print;

import java.awt.Font;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Controls the printing of a document
 * 
 * @author josepaiva
 */
public class PrinterController {

	private static final long TIMEOUT = 5000;

	private IntroPage introPage = null;
	// private PrintableDocument doc = null;
	private String template = "";
	private String config = "";

	// private Font font = null;

	/**
	 * Uses SANS_SERIF font with size 24px to print content
	 * 
	 * @param template
	 * @param config
	 */
	public PrinterController(String template, String config) {
		introPage = new IntroPage();

		this.template = template;
		// this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		this.config = config;
	}

	/**
	 * 
	 * @param font
	 *            the font to use on printing
	 * @param template
	 * @param config
	 */
	public PrinterController(Font font, String template, String config) {
		introPage = new IntroPage(font);

		this.template = template;
		// this.font = font;
		this.config = config;
	}

	/**
	 * 
	 * @param fontName
	 *            the font name. This can be a font face name or a font family
	 *            name
	 * @param size
	 *            the point size of the Font
	 * @param template
	 * @param config
	 */
	public PrinterController(String fontName, int size, String template,
			String config) {
		introPage = new IntroPage(fontName, size);

		this.template = template;
		// this.font = new Font(fontName, Font.PLAIN, size);
		this.config = config;
	}

	/**
	 * 
	 * @param defaultPrinter
	 * @return
	 * @throws MooshakException
	 */
	public boolean simplePrint(String defaultPrinter, String directory)
			throws MooshakException {
		Document document = new Document();
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(
					directory + File.separator + "mooshakPrint.pdf"));
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,
					new ByteArrayInputStream(template.getBytes()),
					new ByteArrayInputStream(config.getBytes()));
			document.close();
		} catch (DocumentException | IOException e) {
			throw new MooshakException("Could not print file");
		}

		File file = new File(directory + File.separator + "mooshakPrint.pdf");

		if (PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PDF,
				null).length > 0)
			return printWithJavaApi(defaultPrinter, file);

		return printWithCups(defaultPrinter, file);
	}

	/**
	 * Default printing, prints to printer defaultPrinter
	 * 
	 * @param defaultPrinter
	 * @return true
	 * @throws MooshakException
	 */
	public boolean print(String defaultPrinter, String directory)
			throws MooshakException {

		String name = "mooshakPrint.ps";
		File file = new File(directory + File.separator + name);
		printToFileWithIntro(file);

		if (PrintServiceLookup.lookupPrintServices(
				DocFlavor.INPUT_STREAM.POSTSCRIPT, null).length > 0)
			return printWithJavaApi(defaultPrinter, file);

		return printWithCups(defaultPrinter, file);
	}

	/**
	 * @param defaultPrinter
	 * @param file
	 * @return
	 * @throws MooshakException
	 */
	private boolean printWithCups(String defaultPrinter, File file)
			throws MooshakException {

		CupsClient client = null;
		CupsPrinter printer = null;
		try {
			client = new CupsClient(
					InetAddress.getLocalHost().getHostAddress(), 631, "mooshak");

			List<CupsPrinter> printers = null;
			long timeoutTime = System.currentTimeMillis() + 10000;
			while (System.currentTimeMillis() < timeoutTime && printers == null) {
				try {
					printers = client.getPrinters();
				} catch (Exception e) {
				}
			}

			if (printers == null || printers.isEmpty())
				throw new MooshakException();

			for (CupsPrinter service : printers) {
				if (service.getName().equalsIgnoreCase(defaultPrinter))
					printer = service;
			}

			if (printer == null)
				throw new MooshakException();
		} catch (Exception e) {
			throw new MooshakException("No printers connected with"
					+ " requested service");
		}

		try {
			FileInputStream fis = new FileInputStream(file);
			PrintJob printJob = new PrintJob.Builder(fis)
					.jobName("Mooshak 2.0").userName("mooshak").build();
			printer.print(printJob);

			Files.delete(file.toPath());
		} catch (Exception cause) {
			throw new MooshakException(cause.getLocalizedMessage());
		}

		return true;
	}

	private boolean printWithJavaApi(String defaultPrinter, File file)
			throws MooshakException {

		PrintService printer = null;
		try {
			for (PrintService service : PrintServiceLookup.lookupPrintServices(
					DocFlavor.INPUT_STREAM.PDF, null)) {
				if (service.getName().equalsIgnoreCase(defaultPrinter))
					printer = service;
			}
		} catch (Exception e) {
			throw new MooshakException(e.getLocalizedMessage());
		}

		if (printer == null) {
			try {
				printer = PrintServiceLookup.lookupPrintServices(
						DocFlavor.INPUT_STREAM.PDF, null)[0];
			} catch (Exception e) {
				throw new MooshakException("No printers connected with"
						+ " requested service");
			}
		}

		DocPrintJob job = printer.createPrintJob();
		try {
			FileInputStream fis = new FileInputStream(file);
			Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PDF, null);
			job.print(doc, null);

			Files.delete(file.toPath());
		} catch (Exception cause) {
			throw new MooshakException(cause.getLocalizedMessage());
		}

		return true;
	}

	@Deprecated
	private void printToFileWithIntro(File file) throws MooshakException {

		// Find a factory object for printing Printable objects to PostScript.
		DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
		String format = "application/postscript";

		StreamPrintServiceFactory factory = null;
		try {
			factory = StreamPrintServiceFactory
					.lookupStreamPrintServiceFactories(flavor, format)[0];
		} catch (Exception e) {
			throw new MooshakException("No printers connected");
		}

		if (factory == null)
			throw new MooshakException("No factory service found");

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (Exception cause) {
			throw new MooshakException(cause);
		}

		// Obtain a PrintService that prints to that file
		StreamPrintService service = factory.getPrintService(out);

		DocPrintJob job = service.createPrintJob();

		if (job.getPrintService() == null)
			throw new MooshakException("No printers connected");

		// Create a new book to add pages to
		Book book = new Book();

		// Add the document page using portrait page format
		PageFormat documentPageFormat = new PageFormat();
		documentPageFormat.setOrientation(PageFormat.PORTRAIT);

		// Add the cover page using the default page format for this print
		// job
		book.append(introPage, documentPageFormat);

		/*
		 * doc = new PrintableDocument(font, template, documentPageFormat, 1,
		 * true, true); doc.setConfig(config);
		 * 
		 * book.append(doc, documentPageFormat, doc.getNumberOfPages());
		 */

		// Now create a Doc that encapsulate the Printable object and its type
		Doc document = new SimpleDoc(book, flavor, null);

		// Now print the Doc to the DocPrintJob
		try {
			job.print(document, null);
		} catch (Exception cause) {
			throw new MooshakException(cause);
		}

		// And close the output file.
		try {
			out.close();
		} catch (IOException cause) {
			throw new MooshakException(cause);
		}
	}

	/**
	 * Prints to a given file
	 * 
	 * @param file
	 * @throws MooshakException
	 */
	public void printToFile(File file, boolean paginate, boolean landscape)
			throws MooshakException {

		final Document document;
		if (landscape) 
			document = new Document(PageSize.A4.rotate(), 72, 72, 72, 72);
		else
			document = new Document(PageSize.A4, 72, 72, 72, 72);
		PdfWriter writer;
		try {
			writer = PdfWriter
					.getInstance(document, new FileOutputStream(file));
			writer.setInitialLeading(15.5f);
			document.open();

			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						XMLWorkerHelper.getInstance().parseXHtml(writer, document,
								new ByteArrayInputStream(template.getBytes()),
								new ByteArrayInputStream(config.getBytes()));
					} catch (IOException e) {
						Logger.getLogger("").log(Level.SEVERE,e.getMessage());
					}
				}
			});

			executor.shutdown();
			try {
				executor.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
			} catch (InterruptedException cause) {
				throw new MooshakException();
			}

			document.close();
		} catch (DocumentException | IOException e) {
			throw new MooshakException("Could not print file");
		}

	}

}
