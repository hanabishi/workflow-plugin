package org.jenkinsci.plugins.workflow.steps;

import com.google.common.util.concurrent.FutureCallback;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Kohsuke Kawaguchi
 */
public class RetryStepExecution extends StepExecution {
    @Inject
    RetryStep step;

    @Override
    public boolean start() throws Exception {
        getContext().invokeBodyLater(new Callback());
        return false;   // execution is asynchronous
    }

    @Override
    public void stop() throws Exception {
        // TODO
        // requiring finding the tip and aborting it
        // Perhaps StepContext should support stopping the execution
        // started by the invokeBodyLater method
        throw new UnsupportedOperationException();
    }

    private class Callback implements FutureCallback<Object>, Serializable {
        private int left;

        Callback() {
            left = step.getCount();
        }

        @Override
        public void onSuccess(Object result) {
            getContext().onSuccess(result);
        }

        @Override
        public void onFailure(Throwable t) {
            try {
                // TODO: here we want to access TaskListener that belongs to the body invocation end node.
                // how should we do that?
                /* TODO not currently legal:
                TaskListener l = getContext().get(TaskListener.class);
                t.printStackTrace(l.error("Execution failed"));
                */
                left--;
                if (left>0) {
                    /*
                    l.getLogger().println("Retrying");
                    */
                    getContext().invokeBodyLater(this);
                } else {
                    getContext().onFailure(t);
                }
            } catch (Throwable p) {
                getContext().onFailure(p);
            }
        }

        private static final long serialVersionUID = 1L;
    }
}
