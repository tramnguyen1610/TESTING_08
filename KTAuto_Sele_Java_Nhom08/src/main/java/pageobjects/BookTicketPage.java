package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BookTicketPage extends GeneralPage {

    private final By departStationSelect = By.name("DepartStation");
    private final By arriveStationSelect = By.name("ArriveStation");

    public String getSelectedDepartStation() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(departStationSelect)));
        return select.getFirstSelectedOption().getText().trim();
    }

    public String getSelectedArriveStation() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(arriveStationSelect)));
        return select.getFirstSelectedOption().getText().trim();
    }

    // Có thể bổ sung thêm các method khác sau này (select date, select seat, book ticket...)
}
