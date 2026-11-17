package com.paynext.base;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/** Creates an isolated browser context and page per test. Override with -Dheadless=false locally. */
public abstract class BaseTest {
    protected static Playwright playwright;
    protected BrowserContext context;
    protected Browser browser;
    protected static Page page;

    @BeforeClass(alwaysRun = true)
    public void globalSetUp() {
        playwright = Playwright.create();
        // Headless by default (CI has no display). Run headed locally with: mvn test -Dheadless=false
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        Browser.NewContextOptions contextOptions = getContextOptions();
        context = browser.newContext(contextOptions);
        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (page != null && !page.isClosed()) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
    }

    @AfterMethod
    public void captureFailure(ITestResult result) {
        if (!result.isSuccess() && page != null && !page.isClosed()) {
            Allure.addAttachment("Screenshot on Failure",
                    new ByteArrayInputStream(
                            page.screenshot(
                                    new Page.ScreenshotOptions().setFullPage(true)
                            )
                    ));
        }
    }

    @AfterClass(alwaysRun = true)
    public void globalTearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    protected void step(String stepName, Runnable action) {
        Allure.step(stepName, (Allure.ThrowableRunnableVoid) action);
    }

    protected Browser.NewContextOptions getContextOptions() {
        return new Browser.NewContextOptions();
    }
}
