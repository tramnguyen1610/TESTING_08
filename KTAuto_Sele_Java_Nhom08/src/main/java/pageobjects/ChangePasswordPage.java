package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ChangePasswordPage extends GeneralPage {

    private final By txtCurrentPassword = By.id("currentPassword");
    private final By txtNewPassword = By.id("newPassword");
    private final By txtConfirmPassword = By.id("confirmPassword");
    private final By btnChangePassword = By.xpath("//input[@type='submit' and @value='Change Password']");
    private final By lblSuccessMessage = By.xpath("//p[@class='message success']");

    public void changePassword(String currentPwd, String newPwd, String confirmPwd) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(txtCurrentPassword)).sendKeys(currentPwd);
        Constant.WEBDRIVER.findElement(txtNewPassword).sendKeys(newPwd);
        Constant.WEBDRIVER.findElement(txtConfirmPassword).sendKeys(confirmPwd);
        Constant.WEBDRIVER.findElement(btnChangePassword).click();
    }

    public String getSuccessMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(lblSuccessMessage))
                .getText().trim();
    }
}
