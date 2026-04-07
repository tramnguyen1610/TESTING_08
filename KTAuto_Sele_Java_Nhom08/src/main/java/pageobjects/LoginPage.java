package pageobjects;

public class LoginPage extends GeneralPage {
    private static final String LOGIN = "admin@abc.com";
    private static final String PASSWORD = "tram12345678";

    private String username;
    private String password;

    public LoginPage() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return (username.equals("") || password.equals(""));
    }

    // Login method
    public void login(String username, String password) {
        this.username = username;
        this.password = password;
    }
}