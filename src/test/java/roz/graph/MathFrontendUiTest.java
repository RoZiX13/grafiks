package roz.graph;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class MathFrontendUiTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:3000";

    @BeforeAll
    static void setUp() {
        // Если используете WebDriverManager:
        // WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void plot2d_shouldRenderCanvas() {
        driver.get(BASE_URL);

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='function']"))
        );
        input.clear();
        input.sendKeys("x^2");

        driver.findElement(By.cssSelector("button[data-testid='plot-2d']")).click();

        WebElement canvas = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("canvas"))
        );

        assertTrue(canvas.isDisplayed());
        assertTrue(canvas.getSize().getWidth() > 0);
        assertTrue(canvas.getSize().getHeight() > 0);
    }

    @Test
    void plot3d_shouldRenderCanvas() {
        driver.get(BASE_URL);

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='function3d']"))
        );
        input.clear();
        input.sendKeys("x^2 + y^2");

        driver.findElement(By.cssSelector("button[data-testid='plot-3d']")).click();

        WebElement canvas = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("canvas"))
        );

        assertTrue(canvas.isDisplayed());
        assertTrue(canvas.getSize().getWidth() > 0);
        assertTrue(canvas.getSize().getHeight() > 0);
    }

    @Test
    void derivative_shouldShowResult() {
        driver.get(BASE_URL);

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='derivative']"))
        );
        input.clear();
        input.sendKeys("x^3");

        driver.findElement(By.cssSelector("button[data-testid='derivative']")).click();

        WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='derivative-result']"))
        );

        assertFalse(result.getText().isBlank());
    }

    @Test
    void integral_shouldShowResult() {
        driver.get(BASE_URL);

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='integral']"))
        );
        input.clear();
        input.sendKeys("x^2");

        driver.findElement(By.cssSelector("button[data-testid='integral']")).click();

        WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='integral-result']"))
        );

        assertFalse(result.getText().isBlank());
    }

    @Test
    void roots_shouldShowResult() {
        driver.get(BASE_URL);

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='roots']"))
        );
        input.clear();
        input.sendKeys("x^2 - 4");

        driver.findElement(By.cssSelector("button[data-testid='roots']")).click();

        WebElement result = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='roots-result']"))
        );

        assertFalse(result.getText().isBlank());
    }
}