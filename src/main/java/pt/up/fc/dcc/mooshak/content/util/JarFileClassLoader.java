package pt.up.fc.dcc.mooshak.content.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility to load classes from a JAR file
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JarFileClassLoader {

	private static Map<String, JarFileClassLoaderCacheItem> urlClassLoadersCache = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T> T initializeClassFromJar(String jarPath, String className)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
			
		JarFileClassLoaderCacheItem cacheItem = null;
		if ((cacheItem = urlClassLoadersCache.get(jarPath)) == null || cacheItem.isExpired()) {
			cacheItem = new JarFileClassLoaderCacheItem(jarPath);
			urlClassLoadersCache.put(jarPath, cacheItem);
		}
		
		URLClassLoader urlClassLoader = cacheItem.getUrlClassLoader();

		T inst = null;
		try (JarFile jarFile = new JarFile(jarPath)) {
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class"))
					continue;
				
				String tmpClassName = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
				tmpClassName = tmpClassName.replace('/', '.');
				if (tmpClassName.equals(className)) {
					Class<?> clazz = urlClassLoader.loadClass(tmpClassName);
					Object object = clazz.newInstance();
					inst = (T) object;
				}
			}
		}
		
		if (inst == null)
			throw new ClassNotFoundException("Class " + className + " was not found in " + jarPath);
		
		return inst;
	}

	/**
	 * Cached jar-file class loader item
	 * 
	 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
	 */
	static class JarFileClassLoaderCacheItem {

		String filePath = null;
		URLClassLoader urlClassLoader = null;
		FileTime time = null;

		JarFileClassLoaderCacheItem(String filePath) throws IOException {
			this.filePath = filePath;

			URL[] urls = { new URL("jar:file:" + filePath + "!/") };
			this.urlClassLoader = URLClassLoader.newInstance(urls);
			this.time = Files.getLastModifiedTime(Paths.get(filePath));
		}

		/**
		 * @return the urlClassLoader
		 */
		public URLClassLoader getUrlClassLoader() {
			return urlClassLoader;
		}

		/**
		 * Is this item expired?
		 * 
		 * @return <code>true</code> if the item has expired, <code>false</code>
		 *         otherwise
		 * @throws IOException 
		 */
		boolean isExpired() {
			try {
				return time.compareTo(Files.getLastModifiedTime(Paths.get(filePath))) < 0;
			} catch (IOException e) {
				return true;
			}
		}
	}
}
