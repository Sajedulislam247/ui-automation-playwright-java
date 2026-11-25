package com.paynext.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * Creates an isolated Playwright/browser/context/page per test method, stored in ThreadLocals
 * (see {@link PlaywrightManager}) so tests can run in parallel. Use {@link #page()} in tests.
 * Override with -Dheadless=false locally.
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        PlaywrightManager.start(getContextOptions());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (!result.isSuccess() && PlaywrightManager.hasOpenPage()) {
                Allure.addAttachment("Screenshot on Failure",
                        new ByteArrayInputStream(
                                page().screenshot(new Page.ScreenshotOptions().setFullPage(true))));
            }
        } finally {
            PlaywrightManager.stop();
        }
    }

    /** The Page belonging to the current test thread. */
    protected Page page() {
        return PlaywrightManager.getPage();
    }

    protected void step(String stepName, Runnable action) {
        Allure.step(stepName, (Allure.ThrowableRunnableVoid) action);
    }

    protected Browser.NewContextOptions getContextOptions() {
        return new Browser.NewContextOptions();
    }
}
