package pt.up.fc.dcc.mooshak.content.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBContextFactory {
	private static JAXBContextFactory instance = new JAXBContextFactory();

	private static final Map<String, JAXBContext> instances = new ConcurrentHashMap<String, JAXBContext>();

	private JAXBContextFactory() {
	}

	/**
	 * Returns an existing JAXBContext if one for the particular namespace
	 * exists, else it creates an instance adds it to a internal map.
	 * 
	 * @param contextPath
	 *            the context path
	 * @throws JAXBException
	 *             exception in creating context
	 * @return a created JAXBContext
	 */
	public JAXBContext getJaxBContext(final String contextPath) throws JAXBException {
		JAXBContext context = instances.get(contextPath);
		if (context == null) {
			context = JAXBContext.newInstance(contextPath);
			instances.put(contextPath, context);
		}
		return context;
	}

	/**
	 * Returns an existing JAXBContext if one for the particular namespace
	 * exists, else it creates an instance adds it to a internal map.
	 * 
	 * @param contextPath
	 *            the context path
	 * @throws JAXBException
	 *             exception in creating context
	 * @return a created JAXBContext
	 */
	public JAXBContext getJaxBContext(final Class<?> contextPath) throws JAXBException {
		JAXBContext context = instances.get(contextPath.getName());
		if (context == null) {
			context = JAXBContext.newInstance(contextPath);
			instances.put(contextPath.getName(), context);
		}
		return context;
	}

	/**
	 * Get instance.
	 * 
	 * @return Instance of this factory
	 */
	public static JAXBContextFactory getInstance() {
		return instance;
	}
}
