/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora;

import javax.xml.bind.*;
import org.apache.commons.io.FilenameUtils;

import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.DL2;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.EdgeTypes;
//import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Eshu;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.NodeTypes;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author User
 *
 */
public class Configuration {

	private static final int MAX_SIZE = 1<<20;
	
	private DL2 dl2;
	private String path;
	private File xmlFile;
	private Map<String, String> imagesSVG = new HashMap<String, String>();

	/**
	 * @throws IOException 
	 * 
	 */
	public Configuration(String path)  throws MooshakException {
		this.setPath(path);
		String extension=FilenameUtils.getExtension(path);
		
		
		if(extension.equals("zip")){
			
			this.imagesSVG=this.getFilesZIP(path, "svg");
			
			try(InputStream stream = new ByteArrayInputStream(getFileXmlZip(path))) {
				
				loadEshuConfig(stream);
			} catch (IOException  e) {
				throw new MooshakException("Error reading buffer with XML configuration "+e.getMessage()); 
			}
		}
		else if(extension.equals("xml")){
			
			
			try(InputStream stream = Files.newInputStream(Paths.get(path))) {
				
			
				loadEshuConfig(stream);
				
				this.imagesSVG=getImagesDir( path);
				
			} catch (IOException  e) {
				
				throw new MooshakException("Error reading XML file configuration "+e.getMessage()); 

			}
		}
	}

	public void loadEshuConfig(InputStream stream) throws MooshakException{
		try {
			JAXBContext context = JAXBContext.newInstance (
					"pt.up.fc.dcc.mooshak.evaluation.kora.parse.config");
		
			Unmarshaller unmarshaller = context.createUnmarshaller();

			@SuppressWarnings("unchecked")	
			JAXBElement<DL2> doc = 
			(JAXBElement<DL2>)unmarshaller.unmarshal( stream );
			this.dl2 = doc.getValue();
	   
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}

	public Map<String, String> getFilesZIP(String path, String extension) throws MooshakException {
		Map<String, String> files = new HashMap<>();

		try (ZipFile zipFile = new ZipFile(path)) {
			for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				if (FilenameUtils.getExtension(entry.getName()).equals(extension)) {
					StringBuilder text = new StringBuilder();

					try (BufferedReader bf = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)))) {
						String line;

						while ((line = bf.readLine()) != null)
							text.append(line + "\n");
					}
					
					String nameABS=entry.getName();
					String filename=nameABS.substring(nameABS.lastIndexOf('/')+1);
					files.put(filename, text.toString());
				}
			}
		} catch (IOException e) {
			throw new MooshakException("getFiles:" + e.getMessage());
		}

		return files;
	}


	public Map<String, String> getImagesDir(String path) throws MooshakException {
		Map<String, String> files = new HashMap<>();
		File fileXml = new File(path);
		File dir = new File(fileXml.getParentFile().getAbsolutePath());

		List<NodeTypes> nodeConfig = this.dl2.getDiagram().getNodeTypes();
		for (NodeTypes nconfig : nodeConfig) {

			try {

				String iconSVG = readFile(dir.getAbsolutePath() + "/" + nconfig.getNodeStyle().getIconTollbarSVGPath().replaceAll("\\s+",""),
						StandardCharsets.UTF_8);
				String imageSVG = readFile(dir.getAbsolutePath() + "/" + nconfig.getNodeStyle().getImgSVGPath().replaceAll("\\s+",""),
						StandardCharsets.UTF_8);

				files.put(nconfig.getNodeStyle().getIconTollbarSVGPath(), iconSVG);
				files.put(nconfig.getNodeStyle().getImgSVGPath(), imageSVG);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<EdgeTypes> edgeConfig = this.dl2.getDiagram().getEdgeTypes();
		for (EdgeTypes econfig : edgeConfig) {
			try {
				String iconSVG = readFile(dir.getAbsolutePath() + "/" + econfig.getEdgeStyle().getIconTollbarSVGPath().replaceAll("\\s+",""),
						StandardCharsets.UTF_8);
				files.put(econfig.getEdgeStyle().getIconTollbarSVGPath(), iconSVG);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return files;
	}

	public String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public byte[] getFileXmlZip(String path) throws MooshakException  {
		
		try(ZipFile zipFile = new ZipFile(path)) {
			for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				if (FilenameUtils.getExtension(entry.getName()).equals("xml")) {
					int size = entry.getSize() == -1 ? MAX_SIZE : (int) entry.getSize();
					byte[] buffer = new byte[size];
					int offset = 0, length = size, len;
					try(InputStream stream = zipFile.getInputStream(entry)) {
						while(length > 0 && (len = stream.read(buffer, offset, length)) > -1) {
								offset += len;
								length -= len;
						}
					}
					
					return Arrays.copyOf(buffer, offset);
				}

			}
			throw new MooshakException("Missing XML file in ZIP configuration");

		} catch (IOException e) {
			throw new MooshakException("Error reading ZIP configuration",e);
		}		
	}


//	/**
//	 * @return the eshu
//	 */
//	public DL2 getEshu() {
//		return dl2;
//	}
//
//	/**
//	 * @param eshu the eshu to set
//	 */
//	public void setEshu(DL2 eshu) {
//		this.dl2 = eshu;
//	}
	
	/**
	 * @return the eshu
	 */
	public DL2 getDL2() {
		return dl2;
	}

	/**
	 * @param eshu the eshu to set
	 */
	public void setDL2(DL2 dl2) {
		this.dl2 = dl2;
	}



	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}


	public Map<String, String> getImagesSVG() {
		return imagesSVG;
	}


	public void setImagesSVG(Map<String, String> imagesSVG) {
		this.imagesSVG = imagesSVG;
	}



	public File getXmlFile() {
		return xmlFile;
	}



	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Configuration [eshu=" + dl2 + ", path=" + path + ", xmlFile=" + xmlFile + ", imagesSVG=" + imagesSVG
				+ "]";
	}





}
