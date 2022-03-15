package pt.up.fc.dcc.mooshak.rest.auth.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allows to define a list of roles who can access the type/method
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@Documented
@Retention (RUNTIME)
@Target({TYPE, METHOD})
public @interface Secured {
	Role[] value();
}
