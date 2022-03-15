package pt.up.fc.dcc.mooshak.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Backup and recovery of data in directories. Backup files have the same name
 * as data files but with a suffix (a human readable timestamp). When a backup
 * file is used for data recovery the current data is stored in a redo file.
 * Redo and backup files are similar but use a different suffix separator. Most
 * procedures in this file use a flag to specify if a redo file (instead of a
 * backup file) must be used; the default is false.
 * 
 * @author josepaiva
 */
public class BackupObject {
	// data file name
	private static final String DATA_FILE = ".data.tcl";
	// maximum backups per directory
	private static final int MAXIMUM_BACKUPS = 5;
	// separator for backup files suffix
	private static final String UNDO_SEPARATOR = ".";
	// separator for redo files suffix
	private static final String REDO_SEPARATOR = ":";
	
	private static Map<String, BackupObject> backups =
			new HashMap<String, BackupObject>();
	
	private String dataObj;
	
	public static BackupObject getBackupObject(String object) {
		BackupObject backup = null;
		
		if(backups.containsKey(object))
			backup = backups.get(object);
		else {
			backup = new BackupObject(object);
			backups.put(object, backup);
		}
		
		return backup;
	}
	
	private BackupObject(String object) {
		this.dataObj = object;
	}

	/**
	 * Save a backup of current data (before saving changes)
	 * 
	 * @param directory
	 */
	public synchronized void record(Path directory) {
		
		Path file = Paths.get(directory.toString(), dataObj, DATA_FILE);
		
		String name = generateFileName(directory);
		
		Path copy = Paths.get(directory.toString(), dataObj, name);

		try {
			Files.move(file, copy, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not copy file");
		}
		
		List<String> files = enumerate(directory);
		for (int i = MAXIMUM_BACKUPS; i < files.size(); i++) {
			String filePath = files.get(i);

			try {
				Files.delete(Paths.get(directory.toString(), dataObj, 
						filePath));
			} catch (IOException e) {
				Logger.getLogger("").log(Level.SEVERE, "Could not delete"
						+ " file");
			}
		}
		
	}

	/**
	 * Recovers last backup/redo file. Calls the recover(directory, false)
	 * 
	 * @param directory
	 * @throws MooshakException
	 */
	public void recover(Path directory) throws MooshakException {
		recover(directory, false);
	}

	/**
	 * Recovers last backup/redo file
	 * 
	 * @param directory
	 * @param redo
	 * @throws MooshakException
	 */
	public void recover(Path directory, boolean redo) throws MooshakException {
		
		if(!canRecover(directory, redo))
			return;
		
		File file = new File(directory.toString() + File.separator 
				+ dataObj + File.separator + DATA_FILE);
		
		String name = generateFileName(directory, !redo);
		
		File copy = new File(directory.toString() + File.separator 
				+ dataObj + File.separator + name);
		
		try {
			Files.copy(file.toPath(), copy.toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not copy file");
		}
		
		List<String> files = enumerate(directory, redo);

		Path recover = Paths.get(directory.toString(), dataObj,
				files.get(0)); 
		try {
			Files.move(recover, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger("").log(Level.SEVERE, "Could not rename file");
		}
		
	}

	/**
	 * Can recover from a backup/redo file? Calls the canRecover(directory,
	 * false)
	 * 
	 * @param directory
	 * @return boolean true if it can be recovered, false otherwise
	 * @throws MooshakException
	 */
	public boolean canRecover(Path directory) throws MooshakException {

		return canRecover(directory, false);
	}

	/**
	 * Can recover from a backup/redo file?
	 * 
	 * @param directory
	 * @param redo
	 * @return boolean true if it can be recovered, false otherwise
	 * @throws MooshakException
	 */
	public boolean canRecover(Path directory, boolean redo)
			throws MooshakException {

		return enumerate(directory, redo).size() > 0;
	}

	/**
	 * Enumerates backup/redo files in given directory. Calls the
	 * enumerate(directory, false)
	 * 
	 * @param directory
	 * @return
	 * @throws MooshakException
	 */
	public List<String> enumerate(Path directory) {

		return enumerate(directory, false);
	}

	/**
	 * Enumerates backup/redo files in given directory
	 * 
	 * @param directory
	 * @param redo
	 * @return
	 * @throws MooshakException
	 */
	public List<String> enumerate(Path directory, boolean redo) {
		
		List<String> files = new ArrayList<>();
		
		File dir = new File(directory.toString() + File.separator 
				+ dataObj);
		if(!dir.isDirectory())
			return files;
		
		String sep = redo ? REDO_SEPARATOR : UNDO_SEPARATOR;
		
		String escaped = Pattern.quote(DATA_FILE + sep);
		Pattern preffix = Pattern.compile("^" + escaped + "[0-9]*");
		for (String name : dir.list()) {
			if(preffix.matcher(name).find())
				files.add(name);
		}
		
		Collections.sort(files, Collections.reverseOrder());
		
		return files;
	}
	
	/**
	 * Generates a filename for backup/redo files
	 * 
	 * @param directory
	 * @return
	 * @throws MooshakException
	 */
	public String generateFileName(Path directory) {
		
		return generateFileName(directory, false);
	}
	
	/**
	 * Generates a filename for backup/redo files
	 * 
	 * @param directory
	 * @param redo
	 * @return
	 */
	public String generateFileName(Path directory, boolean redo) {
		
		String sep = redo ? REDO_SEPARATOR : UNDO_SEPARATOR;
		
		String now = new SimpleDateFormat("yMdHHmmss").format(new Date());
		
		return DATA_FILE + sep + now;
	}

}
