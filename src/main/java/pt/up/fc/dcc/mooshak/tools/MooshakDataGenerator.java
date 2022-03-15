package pt.up.fc.dcc.mooshak.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This is a small utility for generating the MooshakData.zip file
 * containing a minimal Mooshak installation
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class MooshakDataGenerator {
	
	private static final int COMPRESSION_LEVEL = 9;
	Path data = Paths.get("home/data");
	Path zipFile = Paths.get("MooshakData.zip");
	Path fullZipFile = Paths.get("MooshakData-full.zip");
	Path prefix = Paths.get("home");
	
	
	List<String> includeDescendant = Arrays.asList(
			"data/configs/",
			"data/contests/proto_icpc/",
			"data/contests/ToPAS14/"
	);
	List<String> includeDescendantFull = Arrays.asList(
			"data/contests/proto_enki/",
			"data/contests/proto_diagram/"
	);
	List<String> excludeDescendant = Arrays.asList(
			"data/configs/sessions/",
			"data/configs/checks",
			"data/contests/proto_icpc/submissions/",
			"data/contests/proto_icpc/validations/",
			"data/contests/proto_enki/submissions/",
			"data/contests/proto_enki/validations/",
			"data/contests/proto_diagram/submissions/",
			"data/contests/proto_diagram/validations/"
	);
			
	boolean isPrefixOf(List<String> list, String name) {
		
		return list.stream().anyMatch( s -> s.startsWith(name));
	}
	
	boolean hasAsPrefix(List<String> list, String name) {
		
		return list.stream().anyMatch( s -> name.startsWith(s));
	}
	
	
	class MooshakDataVisitor implements FileVisitor<Path> {
		ZipOutputStream zipStream;
		ZipOutputStream fullZipStream;
		
		MooshakDataVisitor(ZipOutputStream zipStream, ZipOutputStream fullZipStream) {
			this.zipStream = zipStream;
			this.fullZipStream = fullZipStream;
		}
		
		@Override
		public FileVisitResult preVisitDirectory(Path dir,
				BasicFileAttributes attrs) throws IOException {
			Path path = prefix.relativize(dir);
			String pathname =  path.toString();
			FileVisitResult result = FileVisitResult.CONTINUE;
			
			if (
					isPrefixOf(includeDescendantFull, pathname)
					|| hasAsPrefix(includeDescendantFull, pathname)
					|| isPrefixOf(includeDescendant, pathname)
					|| hasAsPrefix(includeDescendant, pathname)
			) {

				if (hasAsPrefix(excludeDescendant, pathname))
					result = FileVisitResult.SKIP_SUBTREE;
			} else
				result = FileVisitResult.SKIP_SUBTREE;
			
			if(result == FileVisitResult.CONTINUE) {

				fullZipStream.putNextEntry(new ZipEntry(pathname+"/"));
				fullZipStream.closeEntry();
				
				if (isPrefixOf(includeDescendant, pathname)
						|| hasAsPrefix(includeDescendant, pathname)) {
					zipStream.putNextEntry(new ZipEntry(pathname+"/"));
					zipStream.closeEntry();
				}
			}
			
			return result;
		}

		private static final int BUFFER_SIZE = 1<<12;
		byte buffer[] = new byte[BUFFER_SIZE];
		
		@Override
		public FileVisitResult visitFile(Path file,
				BasicFileAttributes attrs) throws IOException {
			Path path = prefix.relativize(file);			
			String pathname =  path.toString();
						
			if (!(
					isPrefixOf(includeDescendantFull, pathname)
					|| hasAsPrefix(includeDescendantFull, pathname)
					|| isPrefixOf(includeDescendant, pathname)
					|| hasAsPrefix(includeDescendant, pathname)
			)) {
				return FileVisitResult.SKIP_SUBTREE;
			}
			
			fullZipStream.putNextEntry(new ZipEntry(path.toString()));
			
			if (isPrefixOf(includeDescendant, pathname)
					|| hasAsPrefix(includeDescendant, pathname)) {
				zipStream.putNextEntry(new ZipEntry(path.toString()));
			}
			
			try(InputStream stream = Files.newInputStream(file)) {
				int len;
				while((len = stream.read(buffer,0,BUFFER_SIZE)) != -1) {
					fullZipStream.write(buffer, 0, len);	
			
					if (isPrefixOf(includeDescendant, pathname)
							|| hasAsPrefix(includeDescendant, pathname)) {
						zipStream.write(buffer, 0, len);	
					}
				}
			}
			
			fullZipStream.closeEntry();
			
			if (isPrefixOf(includeDescendant, pathname)
					|| hasAsPrefix(includeDescendant, pathname)) {
				zipStream.closeEntry();
			}
			
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc)
				throws IOException {
			return FileVisitResult.TERMINATE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc)
				throws IOException {
			return FileVisitResult.CONTINUE;
		}
	}
	
	MooshakDataGenerator() throws IOException {
		
		try (
				OutputStream fileStream = Files.newOutputStream(zipFile);
				ZipOutputStream zipStream = new ZipOutputStream(fileStream);
				OutputStream fullFileStream = Files.newOutputStream(fullZipFile);
				ZipOutputStream fullZipStream = new ZipOutputStream(fullFileStream)
		) {	
			zipStream.setComment("Minimal Mooshak installation, "+
			 "generated on "+(new Date()));
			zipStream.setLevel(MooshakDataGenerator.COMPRESSION_LEVEL);
			fullZipStream.setComment("Mooshak installation with Enki, "+
			 "generated on "+(new Date()));
			fullZipStream.setLevel(MooshakDataGenerator.COMPRESSION_LEVEL);
			Files.walkFileTree(data, new MooshakDataVisitor(zipStream, fullZipStream));
			
			System.out.println(fullZipFile.toAbsolutePath().toString());
		}
	}
	

	public static void main(String[] args) throws IOException {
		new MooshakDataGenerator();
	}

}
