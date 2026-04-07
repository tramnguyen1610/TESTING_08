package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TimetablePage extends GeneralPage {

    public void clickBookTicketLink(String departStation, String arriveStation) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        // XPath tìm link "book ticket" theo ga đi và ga đến
        String xpath = "//td[text()='" + departStation + "']/following-sibling::td[text()='"
                + arriveStation + "']/..//a[text()='book ticket']";

        WebElement bookTicketLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

        // Cuộn đến element trước khi click
        ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].scrollIntoView(true);", bookTicketLink);

        bookTicketLink.click();
    }
}
