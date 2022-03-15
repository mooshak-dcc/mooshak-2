package pt.up.fc.dcc.mooshak.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented

/**
 * Annotation for persistent object fields, assigning them a name (used in text
 * files), a type (@see {@code shared/AttributeType}) a tip (used as tool tip
 * text) and a help text.
 * 
 * @author José paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public @interface MooshakAttribute {

	public enum YesNo {
		YES, NO;

		public String toString() {
			return super.toString().toLowerCase();
		}
	};

	// Name of attribute as defined in Mooshak 1.*
	String name() default "?";

	AttributeType type() default AttributeType.TEXT;

	String complement() default "";

	String refType() default "";

	String tip() default "";

	String help() default "";

	String docSpec() default ""; // path of the document specification relative
									// to class path

	boolean quizEditor() default false;

	String conditionalField() default "";

	String conditionalValue() default "";

	int maxLength() default -1;
}
