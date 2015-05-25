/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.gradle.plugin.stdproject;

import java.util.List;
import org.anarres.jdiagnostics.CompositeQuery;
import org.anarres.jdiagnostics.ThrowableQuery;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class JDiagnosticsTestListener implements TestListener {

    private static final Logger LOG = LoggerFactory.getLogger(JDiagnosticsTestListener.class);

    @Override
    public void beforeSuite(TestDescriptor td) {
    }

    @Override
    public void afterSuite(TestDescriptor td, TestResult tr) {
    }

    @Override
    public void beforeTest(TestDescriptor td) {
    }

    @Override
    public void afterTest(TestDescriptor td, TestResult tr) {
        if (!TestResult.ResultType.FAILURE.equals(tr.getResultType()))
            return;
        List<Throwable> exceptions = tr.getExceptions();
        if (exceptions.isEmpty())
            return;
        CompositeQuery query = new CompositeQuery();
        for (Throwable t : exceptions)
            query.add(new ThrowableQuery(t));
        LOG.info("Test failure diagnostics:\n" + query.call());
    }

}
