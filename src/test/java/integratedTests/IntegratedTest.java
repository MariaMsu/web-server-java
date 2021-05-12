package integratedTests;

import dataAccess.film.Film;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class IntegratedTest {
    // todo move path & url to config; automatically run the application before the test staring
    private final String chromeDriverPath = "/usr/bin/chromedriver";  // "/usr/bin/google-chrome";
    protected final String appURL = "http://localhost:8080/";
    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1200, 767));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test()
    public void filmAddTest() throws InterruptedException {
        Film newFilm = new Film("Sex and the city", "Michael Patrick King", 1998,
                18, 18, 18, 18,
                69, 96, false);

        driver.get(appURL);
        Assert.assertEquals(driver.getTitle(), "Index");
        TimeUnit.SECONDS.sleep(1);

        driver.findElement(By.id("filmsList_link")).click();
        Assert.assertEquals(driver.getTitle(), "Films list");
        TimeUnit.SECONDS.sleep(1);

        driver.findElement(By.id("filmAdd_button")).click();
        Assert.assertEquals(driver.getTitle(), "Film add");
        TimeUnit.SECONDS.sleep(1);

        driver.findElement(By.id("film_name")).sendKeys(newFilm.getFilm_name());
        driver.findElement(By.id("producer")).sendKeys(newFilm.getProducer());
        driver.findElement(By.id("release_year")).sendKeys(newFilm.getRelease_year().toString());
        driver.findElement(By.id("cassette_total_number")).sendKeys(newFilm.getCassette_total_number().toString());
        driver.findElement(By.id("disc_total_number")).sendKeys(newFilm.getDisc_total_number().toString());
        driver.findElement(By.id("cassette_price")).sendKeys(newFilm.getCassette_price().toString());
        driver.findElement(By.id("disk_price")).sendKeys(newFilm.getDisk_price().toString());
        driver.findElement(By.id("submit_button")).click();
        TimeUnit.SECONDS.sleep(1);

        // redirect to page with film info
        Assert.assertEquals(driver.getTitle(), "Film info");
        // check saved film info
        String filmInfoText = driver.findElement(By.id("filmInfo_text")).getText();
        Assert.assertTrue(filmInfoText.contains(newFilm.getFilm_name()));
        Assert.assertTrue(filmInfoText.contains(newFilm.getProducer()));
        Assert.assertTrue(filmInfoText.contains(newFilm.getRelease_year().toString()));
        Assert.assertTrue(filmInfoText.contains(newFilm.getCassette_total_number().toString()));
        Assert.assertTrue(filmInfoText.contains(newFilm.getDisc_total_number().toString()));
        Assert.assertTrue(filmInfoText.contains(newFilm.getCassette_price().toString()));
        Assert.assertTrue(filmInfoText.contains(newFilm.getDisk_price().toString()));
        // there are no orders of the added film
        String tableText = driver.findElement(By.id("filmOrder_table")).getText();
        Assert.assertTrue(tableText.contains("No orders here"));

        driver.findElement(By.id("filmDelete_button")).click();
        Assert.assertEquals(driver.getTitle(), "Films list");
    }

    @AfterClass
    public void clear() {
        driver.quit();
    }

}
