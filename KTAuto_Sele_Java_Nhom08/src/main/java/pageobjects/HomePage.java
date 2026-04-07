package pageobjects;

import common.Constant;
import org.openqa.selenium.By;

public class HomePage extends GeneralPage {


    public void clickLoginTab() {
        super.clickLoginTab();   // kế thừa từ GeneralPage
    }

    public void clickBookTicketTab() {
        super.clickBookTicketTab();
    }

    public void clickChangePasswordTab() {
        super.clickChangePasswordTab();
    }

    // Method tiện ích
    public void navigateToHome() {
        Constant.WEBDRIVER.get(Constant.BASE_URL);
    }
}
