package pt.up.fc.dcc.mooshak.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented

/**
 * Annotation for persistent object methods visible from the administration
 * web interface, assigning them a name, category (menu name) and
 * whether they can receive input from a dialog box
 *  
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public @interface MooshakOperation {
	
	/**
	 * Name of this operation
	 * @return
	 */
	String name() 		default "?";	
	
	/**
	 * Category of this operation 
	 * @return
	 */
	String category()	default "";
	
	/**
	 * Expects input arguments ({@code Map&lt;String&gt;&lt;String&gt;})
	 * @return
	 */
	boolean inputable()	default false;	
	/**
	 * Short text to display in a tool tip 
	 * @return
	 */
	String tip() 		default "";
	/**
	 * Long text to display as help
	 * @return
	 */
	String help() 		default	"";
	
	/**
	 * Show in menu (don't show if part of a wizard and not the first)
	 * @return
	 */
	boolean show()		default true;
	
	/**
	 * Send events to listeners
	 * @return
	 */
	boolean sendEvents()default false;
}
