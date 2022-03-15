package pt.up.fc.dcc.mooshak.evaluation;

import static pt.up.fc.dcc.mooshak.content.PersistentCore.getAbsoluteFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.MooshakTypeException;
import pt.up.fc.dcc.mooshak.content.types.Submission;

/**
 * An instance of this class manages submission security. If is used both for
 * submission compilation and execution, but different instances must be used in
 * each case. The method {@code relax()} must be invoked before compilation or
 * execution to set less strict permissions on directories and files, and the
 * method {@code tightenSecuritry()} must be invoked afterwards to restore
 * security.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class SubmissionSecurity {

	public enum Level {
		COMPILATION, EXECUTION
	}

	/*-------------------------------------------------------------------*\
	 *  Standard permissions                                             *  
	\*-------------------------------------------------------------------*/
	private static final Set<PosixFilePermission> DIR_OPEN = new HashSet<PosixFilePermission>();
	static {
		for (PosixFilePermission perm : PosixFilePermission.values())
			DIR_OPEN.add(perm);
	}

	private static final Set<PosixFilePermission> DIR_CLOSE = new HashSet<PosixFilePermission>();
	static {
		DIR_CLOSE.add(PosixFilePermission.OWNER_READ);
		DIR_CLOSE.add(PosixFilePermission.OWNER_WRITE);
		DIR_CLOSE.add(PosixFilePermission.OWNER_EXECUTE);
	}

	private static final Set<PosixFilePermission> FILE_OPEN = new HashSet<PosixFilePermission>();
	static {
		FILE_OPEN.add(PosixFilePermission.OWNER_READ);
		FILE_OPEN.add(PosixFilePermission.OWNER_WRITE);
		FILE_OPEN.add(PosixFilePermission.GROUP_READ);
		FILE_OPEN.add(PosixFilePermission.OTHERS_READ);
	}

	private static final Set<PosixFilePermission> FILE_CLOSE = new HashSet<PosixFilePermission>();

	{
		FILE_CLOSE.add(PosixFilePermission.OWNER_READ);
		FILE_CLOSE.add(PosixFilePermission.OWNER_WRITE);
	}

	EvaluationParameters parameters;
	Set<Path> files = new HashSet<>();
	Set<Path> directories = new HashSet<>();

	public SubmissionSecurity(EvaluationParameters parameters, Level level) throws MooshakSafeExecutionException {

		this.parameters = parameters;

		switch (level) {
		case COMPILATION:
			directories.add(getAbsoluteFile(parameters.getDirectory()));
			collectFilesWithSameOwner();
			//collectPackageFoldersAndFiles();
			break;
		case EXECUTION:
			collectFilesWithSameOwner();
			collectFilesFromUserTestData();
			//collectPackageFoldersAndFiles();
			break;
		}
	}

	/**
	 * Set less strict permissions on directories and files to enable execution
	 * by other users
	 * 
	 * @throws MooshakSafeExecutionException
	 */
	public void relax() throws MooshakSafeExecutionException {
		setPermissions(directories, DIR_OPEN, files, FILE_OPEN);
	}

	/**
	 * Set strict permissions on directories and files to disable execution by
	 * other users
	 * 
	 * @param directories
	 *            to close
	 * @param files
	 *            to close
	 * @throws MooshakSafeExecutionException
	 */
	public void tighten() throws MooshakSafeExecutionException {
		setPermissions(directories, DIR_CLOSE, files, FILE_CLOSE);
	}

	/**
	 * Set permissions on execution directory
	 * 
	 * @param directory
	 *            where execution takes place
	 * @param dirPermissions
	 *            permissions to set on directory
	 * @param files
	 *            in directory that must change permissions
	 * @param filePermissions
	 *            permissions to set on files
	 * @throws MooshakTypeException
	 *             on IO error
	 */
	private void setPermissions(Set<Path> directories, Set<PosixFilePermission> dirPermissions, Set<Path> files,
			Set<PosixFilePermission> filePermissions) throws MooshakSafeExecutionException {

		try {
			for (Path directory : directories)
				Files.setPosixFilePermissions(directory, dirPermissions);

			for (Path file : files)
				Files.setPosixFilePermissions(file, filePermissions);

		} catch (IOException e) {
			String message = "IO error changing permissions " + "for safe execution";
			throw new MooshakSafeExecutionException(message, e.getCause());
		}
	}

	private void collectFilesWithSameOwner() throws MooshakSafeExecutionException {

		Path program = getAbsoluteFile(parameters.getProgramPath());
		Path directory = getAbsoluteFile(parameters.getDirectoryPath());

		UserPrincipal owner = null;

		try {
			owner = Files.getOwner(program);
		} catch (IOException cause) {
			String message = "I/O error looking for files from same owner";
			throw new MooshakSafeExecutionException(message, cause);
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			Path itemFileName;
			for (Path item : stream)
				if(     (itemFileName = item.getFileName()) != null 		&&
						! itemFileName.toString().startsWith(".data.tcl") 	&&
						! itemFileName.toString().startsWith(".class.tcl") 	&&
						! itemFileName.toString().equals(".") 				&&
						! itemFileName.toString().equals("..") 				&&
						Files.getOwner(item).equals(owner) 					&& 
						item.toFile().isFile())
					files.add(item);
			
		} catch (IOException cause) {
			String message = "I/O error looking for files from same owner";
			throw new MooshakSafeExecutionException(message, cause);
		}
	}

	private void collectFilesFromUserTestData() throws MooshakSafeExecutionException {
		Submission submission = parameters.getSubmission();
		Path directory = parameters.getDirectoryPath();

		directories.add(directory);
		if (submission.hasUserTestData()) {
			Path relativeUserTestData = null;
			try {
				relativeUserTestData = submission.getUserTestData().getPath();
			} catch (MooshakContentException cause) {
				String message = "Getting path of user test data directory";
				throw new MooshakSafeExecutionException(message, cause);
			}
			Path data = getAbsoluteFile(relativeUserTestData);

			directories.add(data);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(data)) {
				for (Path file : stream)
					files.add(file);
			} catch (IOException cause) {
				String message = "Listing user test data directory";
				throw new MooshakSafeExecutionException(message, cause);
			}
		}
	}

	/**
	 * Collect package folders
	 * 
	 * @throws MooshakSafeExecutionException
	 */
	private void collectPackageFoldersAndFiles() throws MooshakSafeExecutionException {

		Path program = getAbsoluteFile(parameters.getProgramPath());
		Path directory = getAbsoluteFile(parameters.getDirectoryPath());
		
		/*Path relativeProgramPath = directory.relativize(program);*/
		/*if (relativeProgramPath.getNameCount() <= 1)
			return;*/
		
		Path packagePath = directory;
		
		Logger.getLogger("").severe("dir: " + packagePath.toString());
		/*directories.add(packagePath);*/
		
		UserPrincipal owner = null;

		try {
			owner = Files.getOwner(program);
		} catch (IOException cause) {
			String message = "I/O error looking for files from same owner";
			throw new MooshakSafeExecutionException(message, cause);
		}
		
		collectFilesAndDirectoriesRecursively(packagePath, owner);
	}

	/**
	 * Collect recursively all files and directories under {@code directory}
	 * 
	 * @param directory
	 *            {@link Path} root directory to collect files
	 * @param owner
	 *            {@link UserPrincipal} user that owns the files
	 * @throws MooshakSafeExecutionException
	 */
	private void collectFilesAndDirectoriesRecursively(Path directory, UserPrincipal owner)
			throws MooshakSafeExecutionException {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path path : stream) {
				Path filename = path.getFileName();
				if (filename != null && !filename.toString().startsWith(".") && 
						Files.getOwner(path).equals(owner)) {

					if (Files.isDirectory(path)) {
						directories.add(path);
						collectFilesAndDirectoriesRecursively(path, owner);
					} else
						files.add(path);
				}
			}
		} catch (IOException cause) {
			String message = "Listing user test data directory";
			throw new MooshakSafeExecutionException(message, cause);
		}
	}
}
