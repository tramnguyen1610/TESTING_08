package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class GeneralPage {

    // Locators cho các menu/tab dùng chung
    private final By tabLogin = By.xpath("//a[@href='/Account/Login.cshtml']");
    private final By tabBookTicket = By.xpath("//a[@href='/Page/BookTicketPage.cshtml']");
    private final By tabChangePassword = By.xpath("//a[@href='/Account/ChangePassword.cshtml']");
    private final By lblAccount = By.xpath("//div[@class='account']/strong");

    // Elements
    protected WebElement getTabLogin() {
        return Constant.WEBDRIVER.findElement(tabLogin);
    }

    protected WebElement getTabBookTicket() {
        return Constant.WEBDRIVER.findElement(tabBookTicket);
    }

    protected WebElement getTabChangePassword() {
        return Constant.WEBDRIVER.findElement(tabChangePassword);
    }

    protected WebElement getLblAccount() {
        return Constant.WEBDRIVER.findElement(lblAccount);
    }

    // Methods thao tác
    public void clickLoginTab() {
        getTabLogin().click();
    }

    public void clickBookTicketTab() {
        getTabBookTicket().click();
    }

    public void clickChangePasswordTab() {
        getTabChangePassword().click();
    }

    public String getAccountName() {
        return getLblAccount().getText();
    }
}
