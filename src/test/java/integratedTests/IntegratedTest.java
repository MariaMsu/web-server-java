package integratedTests;

import dataAccess.client.Client;
import dataAccess.film.Film;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.PrepareDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class IntegratedTest {
    // todo move path & url to config; automatically run the application before the test starting
    protected final String appURL = "http://localhost:8080/";
    protected WebDriver driver;

    @BeforeClass
    public void setUp() throws SQLException, IOException {
        final String chromeDriverPath = "/usr/bin/chromedriver";  // "/usr/bin/google-chrome";

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1200, 767));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Init database. Some tests require that the base is not empty
        PrepareDatabase.initEmptyDB();
        PrepareDatabase.fillDB();
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
    public void filmAddEditDeleteTest() {
        Film newFilm = new Film("Sex and the city", "Michael Patrick King", 1998,
                18, 18, 18, 18,
                69, 96, false);
        int newDiscPrice = 666;

        driver.get(appURL);  // go to the root url
        Assert.assertEquals(driver.getTitle(), "Index");

        driver.findElement(By.id("filmsList_link")).click();  // go to page "Films list"
        Assert.assertEquals(driver.getTitle(), "Films list");

        // add new film
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

        // delete film
        driver.findElement(By.id("delete_button")).click();
        Assert.assertEquals(driver.getTitle(), "Films list");
    }

    void assertClientIsEqualToWebInfo(Client client, String infoText){
        Assert.assertTrue(infoText.contains(client.getClient_name()));
        Assert.assertTrue(infoText.contains(client.getPhone()));
    }

    @Test()
    public void clientAddEditDeleteTest() {
        Client newClient = new Client("Sexy Tanya", "+7 (351) 267-50-52", false);
        String newPhone = "8 (495) 777-51-90";

        driver.get(appURL);
        Assert.assertEquals(driver.getTitle(), "Index");

        driver.findElement(By.id("clientsList_link")).click();
        Assert.assertEquals(driver.getTitle(), "Clients list");

        // add new client
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

        // there are no orders of the added client
        String tableText = driver.findElement(By.id("clientOrder_table")).getText();
        Assert.assertTrue(tableText.contains("No orders here"));

        // edit client info
        driver.findElement(By.id("edit_button")).click();
        Assert.assertEquals(driver.getTitle(), "Client add");
        newClient.setPhone(newPhone);
        driver.findElement(By.id("phone")).sendKeys(newClient.getPhone());
        driver.findElement(By.id("submit_button")).click();
        Assert.assertEquals(driver.getTitle(), "Client info");
        String filmInfoTextUpdate = driver.findElement(By.id("clientInfo_text")).getText();
        assertClientIsEqualToWebInfo(newClient, filmInfoTextUpdate);

        // delete client
        driver.findElement(By.id("delete_button")).click();
        Assert.assertEquals(driver.getTitle(), "Clients list");
    }

    @Test()
    public void errorTest(){
        // this client does not exist
        driver.get(appURL+"/client?client_id=9999");
        Assert.assertEquals(driver.getTitle(), "Error");

        // this film does not exist
        driver.get(appURL+"/film?film_id=9999");
        Assert.assertEquals(driver.getTitle(), "Error");
    }

    @Test()
    public void orderAddReturnTest() {
        String filmName;
        String clientName;
        String orderURL;

        driver.get(appURL);
        Assert.assertEquals(driver.getTitle(), "Index");

        driver.findElement(By.id("orderAdd_link")).click();
        Assert.assertEquals(driver.getTitle(), "Order add");

        // select first client
        Select clientDropdown = new Select(driver.findElement(By.id("client_select")));
        clientName = clientDropdown.getOptions().get(0).getText();
        clientDropdown.selectByIndex(0);

        // select first film
        Select filmDropdown = new Select(driver.findElement(By.id("film_select")));
        String filmNameAndMediums = filmDropdown.getOptions().get(0).getText();
        filmName = filmNameAndMediums.split(" \\(")[0];
        filmDropdown.selectByIndex(0);

        // check click ability & choose disc
        driver.findElement(By.id("cassette_radio")).click();
        driver.findElement(By.id("disc_radio")).click();

        // set film_issue_date
        driver.findElement(By.id("film_issue_date")).sendKeys("11-23-2020");

        driver.findElement(By.id("submit_button")).click();

        Assert.assertEquals(driver.getTitle(), "Order info");
        String infoText = driver.findElement(By.id("orderInfo_text")).getText();
        // check saved order info
        Assert.assertTrue(infoText.contains(clientName));
        Assert.assertTrue(infoText.contains(filmName));
        Assert.assertTrue(infoText.contains("disc"));
        Assert.assertTrue(infoText.contains("null"));  // chek Film return date = null

        orderURL = driver.getCurrentUrl();  // save order url

        // check order appeared in the film page
        driver.findElement(By.linkText(filmName)).click();
        String filmOrderText = driver.findElement(By.id("filmOrder_table")).getText();
        Assert.assertTrue(filmOrderText.contains(clientName));

        // check order appeared in the client page
        driver.get(orderURL);
        driver.findElement(By.linkText(clientName)).click();
        String clientOrderText = driver.findElement(By.id("clientOrder_table")).getText();
        Assert.assertTrue(clientOrderText.contains(filmName));

        // return film
        driver.findElement(By.id("returnOrder_link")).click();

        Assert.assertEquals(driver.getTitle(), "Order info");
        String infoReturnedText = driver.findElement(By.id("orderInfo_text")).getText();
        Assert.assertFalse(infoReturnedText.contains("null"));  // chek Film return date != null
    }

    @AfterClass
    public void clear() {
        driver.quit();
    }
}
