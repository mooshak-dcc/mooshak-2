package pt.up.fc.dcc.mooshak.content.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Utilities for managing files
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FileUtils {

	/**
	 * Delete files and directories recursively
	 * 
	 * @param fileOrDirectory
	 *            {@link Path} file or directory to remove recursively
	 * @param onlyChildren
	 *            Delete only children
	 * @throws IOException
	 *             - if an I/O error occurs while deleting files and directories
	 *             recursively
	 */
	public static void deleteRecursively(Path fileOrDirectory, boolean onlyChildren) 
			throws IOException {

		Files
			.walk(fileOrDirectory)
			.filter(p -> !onlyChildren || !p.equals(fileOrDirectory))
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
	}

	/**
	 * Delete children files and directories except hidden files
	 * 
	 * @param directory
	 *            {@link Path} directory to remove contents
	 * @throws IOException
	 *             - if an I/O error occurs while deleting contents
	 */
	public static void deleteContentsExceptHidden(Path directory) throws IOException {
		
		if (!directory.toFile().isDirectory())
			return;
		
		Files
			.walk(directory)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.filter(f -> !directory.toFile().equals(f) && !f.isHidden())
			.forEach(File::delete);
	}
	
	/**
	 * Copy file(s) recursively from {@link Path} src to {@link Path} dst
	 * 
	 * @param src {@link Path} source path
	 * @param dst {@link Path} destination path
	 * @throws IOException 
	 */
	public static void copy(Path src, Path dst) throws IOException {
		
		Files.walk(src)
			.map(Path::toFile)
			.forEach(file -> {
				
				Path fileDst = dst.resolve(src.relativize(file.toPath()));
			
				if (file.isDirectory()) {
					if (!Files.exists(fileDst))
						fileDst.toFile().mkdirs();
				} else {
					if (!Files.exists(fileDst.getParent()))
						fileDst.getParent().toFile().mkdirs();
					
					try {
						Files.copy(src, dst, new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES,
								StandardCopyOption.REPLACE_EXISTING });
					} catch (IOException e) {
						Logger.getLogger("").severe("Could not copy " + file.getPath() + " to " + fileDst.toString());
					}
				}
				
		});
	}
	
	public static int lineCount(Path src) throws IOException {
		return lineCount(src, false);
	}
	
	public static int lineCount(Path src, boolean empty) throws IOException {
		return Bytes.lineCount(Files.readAllBytes(src), empty);
	}
}
