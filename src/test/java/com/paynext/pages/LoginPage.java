package com.paynext.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import static io.qameta.allure.Allure.step;

public class LoginPage extends BasePage {
    public LoginPage(Page page) {
        super(page);
    }

    @Override
    public String getPageID() {
        return "";
    }

    @Override
    public String getUrl() {
        return "";
    }

    public LoginPage clickSignIn() {
        step("Click login button", ()
                -> getLoginButtonLocator().click());
        return this;
    }

    public Locator waitForLoginModalWindow() {
        return page.locator(".lzd-member-loginsign-popup-content");
    }

    public LoginPage fillEmailAddress(String email) {
        step("Email Address provided as: " + email, () ->
                page.getByPlaceholder("Please enter your Phone or Email").fill(email));
        return this;
    }

    public LoginPage fillPassword(String password) {
        step("Password used as: " + password, ()
                -> page.locator("[type=\"password\"]").fill(password));
        return this;
    }

    public LoginPage clickLoginButton() {
        step("Login button clicked", ()
                -> page.locator(".iweb-button-mask").click());
        return this;
    }

    @Step("Locate Login button presence")
    public Locator getLoginButtonLocator() {
        step("Locating Login button");
        return page.locator("#anonLogin");
    }

    @Step("Locate User Account Navigation Menu")
    public Locator getMyAccountNevLocator() {
        step("Locating user account menu");
        return page.locator("#myAccountTrigger");
    }
}
