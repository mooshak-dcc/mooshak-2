package pt.up.fc.dcc.mooshak.evaluation.game.wrappers;

import java.lang.reflect.InvocationTargetException;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Base wrapper for an object of the Asura Builder framework
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GameBaseWrapper {
	
	private Object obj;
	
	public GameBaseWrapper(Object obj) {
		this.obj = obj;
	}
	
	/**
	 * Invoke a method on the wrapped object
	 * 
	 * @param methodName Name of the method to invoke
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	protected Object invoke(String methodName) throws MooshakException {

		try {
			return obj.getClass().getMethod(methodName).invoke(obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				 | NoSuchMethodException | SecurityException e) {
			
			throw new MooshakException("Problem invoking method " + methodName, e);
		}
	}
	
	/**
	 * Invoke a method on the wrapped object
	 * 
	 * @param methodName Name of the method to invoke
	 * @param args {@link Object...} arguments to pass to the method
	 * @return {@link Object} result of the invoked method
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	protected Object invoke(String methodName, Class<?>[] argsClasses, Object... args)
			throws MooshakException {
		
		/*Class<?>[] argsClasses = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			argsClasses[i] = args[i].getClass();
		}*/

		try {
			return obj.getClass().getMethod(methodName, argsClasses).invoke(obj, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				 | NoSuchMethodException | SecurityException e) {
			
			throw new MooshakException("Problem invoking method " + methodName, e);
		}
	}
}
