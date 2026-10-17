package listeners;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.opentest4j.TestAbortedException;

import utilities.ConfigReader;

/**
 * Re-runs a {@link RetryOnBackendFlake} test while it keeps failing, up to
 * 1 + retryCount attempts. Attempts stop as soon as one passes.
 *
 * A failed attempt that still has a retry left is aborted rather than failed,
 * so it is reported as skipped - the same way TestNG reports a retried test.
 * The last attempt rethrows the original error, so a genuine failure (or a
 * backend outage lasting every attempt) still fails the build.
 *
 * @BeforeEach / @AfterEach run per attempt, so every attempt gets a fresh browser.
 */
public class RetryExtension implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        Attempts attempts = new Attempts(1 + ConfigReader.getInt("retryCount"));

        Iterator<TestTemplateInvocationContext> iterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return attempts.anotherAttemptNeeded();
            }

            @Override
            public TestTemplateInvocationContext next() {
                attempts.start();
                return new Attempt(attempts);
            }
        };

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    /** How many attempts have run, and whether one of them passed. */
    private static final class Attempts {

        private final int max;
        private int started;
        private boolean passed;

        Attempts(int max) {
            this.max = max;
        }

        boolean anotherAttemptNeeded() {
            return !passed && started < max;
        }

        void start() {
            started++;
        }

        void recordPass() {
            passed = true;
        }

        boolean onLastAttempt() {
            return started >= max;
        }

        int number() {
            return started;
        }
    }

    private static final class Attempt implements TestTemplateInvocationContext {

        private final Attempts attempts;
        private final int number;

        Attempt(Attempts attempts) {
            this.attempts = attempts;
            this.number = attempts.number();
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            return number == 1 ? "attempt 1" : "retry " + (number - 1);
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return List.of(new Outcome(attempts));
        }
    }

    /** Swallows a failure that still has a retry left, and records a pass. */
    private static final class Outcome implements TestExecutionExceptionHandler, AfterTestExecutionCallback {

        private final Attempts attempts;
        private boolean failed;

        Outcome(Attempts attempts) {
            this.attempts = attempts;
        }

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
            failed = true;
            if (attempts.onLastAttempt()) {
                throw throwable;
            }
            System.out.println("Retrying " + context.getRequiredTestMethod().getName()
                    + " (attempt " + (attempts.number() + 1) + ") after: " + throwable);
            throw new TestAbortedException(
                    "Attempt " + attempts.number() + " failed, retrying: " + throwable, throwable);
        }

        @Override
        public void afterTestExecution(ExtensionContext context) {
            if (!failed) {
                attempts.recordPass();
            }
        }
    }
}
