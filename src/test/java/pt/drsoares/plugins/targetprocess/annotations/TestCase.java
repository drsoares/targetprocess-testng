package pt.drsoares.plugins.targetprocess.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The TestCase annotation will mark the test cases as Target Process Test Case with its respective id (mandatory)
 * and its test plan id (optional)
 *
 * @author Diogo Soares
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface TestCase {
    String id();

    String testPlan() default "";
}
