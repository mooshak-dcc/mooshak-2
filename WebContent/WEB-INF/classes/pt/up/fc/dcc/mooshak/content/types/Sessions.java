package pt.up.fc.dcc.mooshak.content.types;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Sessions extends PersistentContainer<Session> {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_TIMEOUT = 3600;
	
	@MooshakAttribute(
			name="Timeout",
			type=AttributeType.INTEGER,
			tip = "Maximum time in seconds for an idle session")
	private Integer timeout = null; 
	
	@MooshakAttribute(
			name="Maximum_sessions",
			type=AttributeType.INTEGER,
			tip = "Maximum number of simultaneous session per team")
	private Integer maximumSessions = null; 
	
	@MooshakAttribute(
			name="Session",
			type=AttributeType.CONTENT)
	private Void session;

	
	public Session newSession() throws MooshakContentException {
		Session session;
		
		try {
			session = create(makeId(), Session.class);
		} catch (MooshakContentException cause) {
			throw new MooshakContentException("Creating session object",cause);
		}
		
		return session;
	}	
	
	static final Random random = new Random();
	static final short ID_SIZE = 12;
	static final char[] idChars =
		{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	
	private String makeId() {
		StringBuilder buffer = new StringBuilder(ID_SIZE);	
		for(int i=0; i<ID_SIZE; i++)
			buffer.append(idChars[random.nextInt(idChars.length)]);
		return buffer.toString();
	}
	
	 /*-------------------------------------------------------------------*\
	  * 		            Public methods                                *
	 \*-------------------------------------------------------------------*/
	
	/**
	 * Remove all sessions that outlived  their timeout
	 * @throws MooshakContentException
	 */
	public void removeExpiredSessions() throws MooshakContentException {		
		Path absolute = getAbsoluteFile();
		long now = System.currentTimeMillis();
		long slack = (timeout == null ? DEFAULT_TIMEOUT : timeout) * 1000;			
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(absolute)) {
			for(Path path: stream) {
				if(! Files.isDirectory(path))
					continue;

				Path relative = PersistentCore.getRelativePath(path);
				
				try {
					Session session = PersistentObject.open(relative);
					Date lastUsed = session.getLastUsed();
					if(lastUsed != null && lastUsed.getTime() + slack < now) {
						session.delete();
					}
				} catch (MooshakContentException e) {
					if(Files.getLastModifiedTime(path).toMillis()+slack < now) 
						deepDelete(path);
				}
				
			}
		} catch (IOException cause) {
			throw new MooshakContentException("Cleaning up sessions",cause);
		}
	}

	private void deepDelete(Path path) throws IOException {
		
		Files.walkFileTree(path, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				
				Files.delete(file);
				
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				Files.delete(dir);
				
				return FileVisitResult.CONTINUE;
			}
			
		});
	
	}
	
}
