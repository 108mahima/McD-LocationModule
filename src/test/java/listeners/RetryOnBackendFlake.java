package listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit 5 counterpart of {@link RetryAnalyzer}: marks a test that may be
 * re-run when it fails, retryCount times (config.properties), because the
 * McDelivery backend intermittently leaves the page on skeleton loaders
 * (defect DEF_LOC_01).
 *
 * Use instead of @Test on tests that need the location bar to be filled by
 * the backend. The assertions are unchanged - if every attempt fails, the
 * test still fails and the defect is still reported.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@TestTemplate
@ExtendWith(RetryExtension.class)
public @interface RetryOnBackendFlake {
}
