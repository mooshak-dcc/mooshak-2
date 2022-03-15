package pt.up.fc.dcc.mooshak.content;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Static methods for handling persistent objects' operations
 * 
 * Operations are methods of a persistent object class that can be invoked
 * from the administration's user interface. These methods are annotated,
 * with a name, category (menu name) and boolean to indicate if they receive
 * input from a dialog box.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Operations {

	/**
	 * An operation of Mooshak, a method marked with @MooshakOperation
	 * An instance of this class relates the method with the annotation data 
	 */
	public static class Operation {
		 Method method;
		 private MooshakOperation annot;
		 int order;
		 
		public Operation(Method method, MooshakOperation annot, int order) {
			super();
			this.method = method;
			this.annot = annot;
			this.order = order;
		}
		 
		/**
		 * Get name of this method
		 * @return
		 */
		public String getName() {
			return annot.name();
		}
		
		/**
		 * Get the category (menu name) of this method
		 * @return
		 */
		public String getCategory() {
			return annot.category();
		}
		
		/**
		 * Get a sort tip on using this method 
		 * @return
		 */
		public String getTip() {
			return annot.tip();
		}
		
		/**
		 * Get a longer help on this method
		 * @return
		 */
		public String getHelp() {
			return annot.help();
		}

		/**
		 * Does this method require input parameters?
		 * @return
		 */
		public boolean getInputable() {
			return annot.inputable();
		}
		
		/**
		 * Is this method to be shown on menu?
		 * @return
		 */
		public boolean getShowable() {
			return annot.show();
		}
		
		/**
		 * Is this method to send events immediately?
		 * @return
		 */
		public boolean getSendEvents() {
			return annot.sendEvents();
		}
		
		/**
		 * Execute this operation on given persistent object
		 * @param persistent	persistent object
		 * @param parameters	name/values pairs as a map
		 * @return				outcome of operation
		 * @throws MooshakContentException if operation is invalid
		 */
		public CommandOutcome execute(
				PersistentObject persistent,
				MethodContext context) throws MooshakException {
			
			Object outcome;
			
			checkValidity();
			
			try {
				synchronized (method) {
					
					boolean flag = method.isAccessible();
					method.setAccessible(true);
					if(context == null)
						outcome = method.invoke(persistent);
					else
						outcome = method.invoke(persistent, context);
					method.setAccessible(flag);

				}
			} catch (IllegalAccessException | 
					IllegalArgumentException | 
					InvocationTargetException cause) {
				
				String message = "Error invoking method "+method.getName();
				if (persistent.getPath() != null)
					message += " on "+persistent.getIdName();
				Logger.getLogger("").log(Level.SEVERE, message,cause);
				throw new MooshakException(message,cause);
			}
			
			return (CommandOutcome) outcome;
		}
		
		/**
		 * Check operation validity 
		 * @throws MooshakContentException if operation is invalid
		 */
		public void checkValidity() throws MooshakContentException {
			Class<?> returnType = method.getReturnType();
			Class<?> parameterTypes[] = method.getParameterTypes();
			
			if(! CommandOutcome.class.equals(returnType)) 
				throw error("Invalid return type "+returnType,"CommandOutcome"); 	
					
			switch(parameterTypes.length) {
			case 0:
				if(annot.inputable())
					throw error("Inputable set","parameters");
				break;
			case 1:
				if(! annot.inputable())
					throw error("Inputable not set","no parameters");

				if(! MethodContext.class.equals(parameterTypes[0])) 
					throw error("Invalid argument type","MethodContext");
				
				break;
			default:	
				throw error(parameterTypes.length+" parametets","0 or 1");
			}

		}
		
		private MooshakContentException error(String invalid, String expected) {
			String message = invalid+" in operation "+annot.name()+
					"\n\tExpected "+expected;
			
			return new MooshakContentException(message);
		}
		
	}
	
	private static Map<Class<? extends PersistentObject>,Map<String,Operation>> 
		labeledMethodsbyClass = new HashMap<>();
	
	private static Map<String,Operation> 
		getLabeledMethods(Class<? extends PersistentObject> type) {
			Map<String,Operation> labeledMethod;

			if(labeledMethodsbyClass.containsKey(type))
				labeledMethod = labeledMethodsbyClass.get(type);
			else {			
				labeledMethod = new HashMap<String,Operation>();  

				MooshakOperation annot;
				int order = 1;
				for(Method method: type.getDeclaredMethods()) {
					method.setAccessible(true);
					if((annot=method.getAnnotation(MooshakOperation.class)) != null) 
						labeledMethod.put(annot.name(),
								new Operation(method,annot,order++));
					method.setAccessible(false);
				}
				labeledMethodsbyClass.put(type,labeledMethod);
			}
			return labeledMethod;
		}
		
		/**
		 * Return operations of an extension of PersistentObject
		 * sorted by the order they were declared
		 * @param type
		 * @return
		 */
		public static Collection<Operation> getOperations(
				Class<? extends PersistentObject> type) {
			List<Operation> values = 
					new ArrayList<>(getLabeledMethods(type).values());
			
			Collections.sort(values, new Comparator<Operation>() {

				@Override
				public int compare(Operation a, Operation b) {
					return a.order - b.order;
				}
				
			});
			return values;
		}
		
		/**
		 * Return named Mooshak operation in given type
		 * @param type Mooshak class
		 * @param name of operation
		 * @return Operation object
		 * @throws MooshakContentException if operation is invalid
		 */
		public static Operation getOperation(
					Class<? extends PersistentObject> type,
					String name) throws MooshakContentException {
			
			Map<String, Operation> operations = getLabeledMethods(type);
			
			if(operations.containsKey(name)) {
				Operation operation = operations.get(name);
				
				operation.checkValidity();
				
				return operation;
			} else {
				String message = "unknown operation " + name +
							" in class "+type.getName();
				throw new MooshakContentException(message);
			}
		}
		
		
}
