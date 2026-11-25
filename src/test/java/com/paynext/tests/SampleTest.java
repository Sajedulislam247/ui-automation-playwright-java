package com.paynext.tests;

import com.paynext.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SampleTest extends BaseTest {

    @Test
    public void pageTitleIsExpected() {
        page().navigate("https://playwright.dev");
        Assert.assertTrue(page().title().contains("Playwright"));
    }
}
