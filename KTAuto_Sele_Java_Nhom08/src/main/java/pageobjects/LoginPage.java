package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage extends GeneralPage {

    private final By txtUsername = By.id("username");
    private final By txtPassword = By.id("password");
    private final By btnLogin = By.xpath("//input[@type='submit' and @value='login']");
    private final By lblLoginError = By.xpath("//p[contains(@class, 'message error')]");

    public void login(String username, String password) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(txtUsername)).sendKeys(username);
        Constant.WEBDRIVER.findElement(txtPassword).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(btnLogin)).click();
    }

    public String getLoginErrorMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(lblLoginError))
                .getText().trim();
    }
}
