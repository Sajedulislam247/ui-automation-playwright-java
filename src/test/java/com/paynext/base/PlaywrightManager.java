package com.paynext.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Holds Playwright objects per thread. Playwright instances are NOT thread-safe,
 * so every test thread gets its own Playwright -> Browser -> Context -> Page chain.
 */
public final class PlaywrightManager {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {
    }

    public static void start(Browser.NewContextOptions contextOptions) {
        // Headless by default (CI has no display). Run headed locally with: mvn test -Dheadless=false
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        BROWSER.set(browser);
        BrowserContext context = browser.newContext(contextOptions);
        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    public static Page getPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("No Page for thread " + Thread.currentThread().getName()
                    + ". Was PlaywrightManager.start() called?");
        }
        return page;
    }

    public static boolean hasOpenPage() {
        Page page = PAGE.get();
        return page != null && !page.isClosed();
    }

    public static void stop() {
        try {
            close(PAGE.get());
            close(CONTEXT.get());
            close(BROWSER.get());
            close(PLAYWRIGHT.get());
        } finally {
            // Always clear to avoid leaking state into pooled threads
            PAGE.remove();
            CONTEXT.remove();
            BROWSER.remove();
            PLAYWRIGHT.remove();
        }
    }

    private static void close(AutoCloseable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }
}
