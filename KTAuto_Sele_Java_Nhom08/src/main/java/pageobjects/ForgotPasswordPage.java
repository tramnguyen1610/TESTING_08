package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ForgotPasswordPage extends GeneralPage {

    private final By txtEmail = By.id("email");
    private final By btnSendInstructions = By.xpath("//input[@type='submit' and @value='Send Instructions']");
    private final By lblSuccessMessage = By.xpath("//p[@class='message success']");

    public void submitEmailForReset(String email) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(txtEmail)).sendKeys(email);
        Constant.WEBDRIVER.findElement(btnSendInstructions).click();
    }

    public String getSuccessMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(lblSuccessMessage))
                .getText().trim();
    }
}
