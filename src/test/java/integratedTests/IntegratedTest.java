package integratedTests;

import dataAccess.client.Client;
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
    protected final String appURL = "http://localhost:8080/";
    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        final String chromeDriverPath = "/usr/bin/chromedriver";  // "/usr/bin/google-chrome";

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1200, 767));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    void assertFilmIsEqualToWebInfo(Film film, String infoText){
        Assert.assertTrue(infoText.contains(film.getFilm_name()));
        Assert.assertTrue(infoText.contains(film.getProducer()));
        Assert.assertTrue(infoText.contains(film.getRelease_year().toString()));
        Assert.assertTrue(infoText.contains(film.getCassette_total_number().toString()));
        Assert.assertTrue(infoText.contains(film.getDisc_total_number().toString()));
        Assert.assertTrue(infoText.contains(film.getCassette_price().toString()));
        Assert.assertTrue(infoText.contains(film.getDisc_price().toString()));
    }

    @Test()
    public void filmAddAndEditTest() {
        Film newFilm = new Film("Sex and the city", "Michael Patrick King", 1998,
                18, 18, 18, 18,
                69, 96, false);
        int newDiscPrice = 666;

        driver.get(appURL);  // go to the url
        Assert.assertEquals(driver.getTitle(), "Index");

        driver.findElement(By.id("filmsList_link")).click();  // go to page "Films list"
        Assert.assertEquals(driver.getTitle(), "Films list");

        driver.findElement(By.id("filmAdd_button")).click();
        Assert.assertEquals(driver.getTitle(), "Film add");

        driver.findElement(By.id("film_name")).sendKeys(newFilm.getFilm_name());
        driver.findElement(By.id("producer")).sendKeys(newFilm.getProducer());
        driver.findElement(By.id("release_year")).sendKeys(newFilm.getRelease_year().toString());
        driver.findElement(By.id("cassette_total_number")).sendKeys(newFilm.getCassette_total_number().toString());
        driver.findElement(By.id("disc_total_number")).sendKeys(newFilm.getDisc_total_number().toString());
        driver.findElement(By.id("cassette_price")).sendKeys(newFilm.getCassette_price().toString());
        driver.findElement(By.id("disc_price")).sendKeys(newFilm.getDisc_price().toString());
        driver.findElement(By.id("submit_button")).click();

        // redirect to page with film info
        Assert.assertEquals(driver.getTitle(), "Film info");
        // check saved film info
        String filmInfoText = driver.findElement(By.id("filmInfo_text")).getText();
        assertFilmIsEqualToWebInfo(newFilm, filmInfoText);

        // there are no orders of the added film
        String tableText = driver.findElement(By.id("filmOrder_table")).getText();
        Assert.assertTrue(tableText.contains("No orders here"));

        // edit film info
        driver.findElement(By.id("edit_button")).click();
        Assert.assertEquals(driver.getTitle(), "Film add");
        newFilm.setDisc_price(newDiscPrice);
        driver.findElement(By.id("disc_price")).sendKeys(newFilm.getDisc_price().toString());
        driver.findElement(By.id("submit_button")).click();
        Assert.assertEquals(driver.getTitle(), "Film info");
        String filmInfoTextUpdate = driver.findElement(By.id("filmInfo_text")).getText();
        assertFilmIsEqualToWebInfo(newFilm, filmInfoTextUpdate);

        driver.findElement(By.id("delete_button")).click();
        Assert.assertEquals(driver.getTitle(), "Films list");
    }

    void assertClientIsEqualToWebInfo(Client client, String infoText){
        Assert.assertTrue(infoText.contains(client.getClient_name()));
        Assert.assertTrue(infoText.contains(client.getPhone()));
    }

    @Test()
    public void clientAddAndEditTest() {
        Client newClient = new Client("Sexy Tanya", "+7 (351) 267-50-52", false);
        String newPhone = "8 (495) 777-51-90";

        driver.get(appURL);
        Assert.assertEquals(driver.getTitle(), "Index");

        driver.findElement(By.id("clientsList_link")).click();
        Assert.assertEquals(driver.getTitle(), "Clients list");

        driver.findElement(By.id("clientAdd_button")).click();
        Assert.assertEquals(driver.getTitle(), "Client add");

        driver.findElement(By.id("client_name")).sendKeys(newClient.getClient_name());
        driver.findElement(By.id("phone")).sendKeys(newClient.getPhone());
        driver.findElement(By.id("submit_button")).click();

        // redirect to page with client info
        Assert.assertEquals(driver.getTitle(), "Client info");
        // check saved client info
        String clientInfoText = driver.findElement(By.id("clientInfo_text")).getText();
        assertClientIsEqualToWebInfo(newClient, clientInfoText);

        // there are no orders of the added film
        String tableText = driver.findElement(By.id("clientOrder_table")).getText();
        Assert.assertTrue(tableText.contains("No orders here"));

        // edit film info
        driver.findElement(By.id("edit_button")).click();
        Assert.assertEquals(driver.getTitle(), "Client add");
        newClient.setPhone(newPhone);
        driver.findElement(By.id("phone")).sendKeys(newClient.getPhone());
        driver.findElement(By.id("submit_button")).click();
        Assert.assertEquals(driver.getTitle(), "Client info");
        String filmInfoTextUpdate = driver.findElement(By.id("clientInfo_text")).getText();
        assertClientIsEqualToWebInfo(newClient, filmInfoTextUpdate);

        driver.findElement(By.id("delete_button")).click();
        Assert.assertEquals(driver.getTitle(), "Clients list");
    }

    @AfterClass
    public void clear() {
        driver.quit();
    }
}
