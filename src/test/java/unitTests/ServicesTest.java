package unitTests;

import dataAccess.client.Client;
import dataAccess.client.ClientService;
import dataAccess.film.Film;
import dataAccess.film.FilmService;
import dataAccess.order.Order;
import dataAccess.order.OrderService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.PrepareDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ServicesTest {
    private ClientService client_s;
    private FilmService film_s;
    private OrderService order_s;


    @BeforeClass
    public void setUp() {
        this.client_s = new ClientService();
        this.film_s = new FilmService();
        this.order_s = new OrderService();
    }

    @Test(groups = {"basicServiceTest"})
    public void testClientService() throws IOException, SQLException {
        PrepareDatabase.initEmptyDB();
        final long nonExistentId = 100;
        final long existentId = 1;
        final Client clientUpdated =
                new Client("Толстая пчела", "8 800 700-06-11", false);
        final Client[] dataClients = TablesTestData.getClients();

        // fill the table
        for (Client client: dataClients) {
            Assert.assertTrue(client_s.addClient(client));
        }

        // read the whole table
        List<Client> clients = client_s.loadAll();
        Assert.assertEquals(clients.size(), dataClients.length);
        for (int i =0; i < clients.size(); ++i){
            Assert.assertEquals(clients.get(i), dataClients[i]);
        }

        // find by id & update
        Assert.assertNull(client_s.findClientById(nonExistentId));
        clientUpdated.setClient_id(nonExistentId);
        Assert.assertFalse(client_s.updateClient(clientUpdated));
        clientUpdated.setClient_id(existentId);
        Assert.assertTrue(client_s.updateClient(clientUpdated));
        Assert.assertEquals(clientUpdated, client_s.findClientById(existentId));

        // save object with initialized filed "id"
        Assert.assertFalse(client_s.addClient(clientUpdated));

        // remove all objects from the table
        for (Client client: clients){
            Assert.assertTrue(client_s.deleteClientById(client.getClient_id()));
        }
    }

    @Test(groups = {"basicServiceTest"})
    public void testFilmService() throws IOException, SQLException {
        PrepareDatabase.initEmptyDB();
        final long nonExistentId = 100;
        final long existentId = 1;
        final Film filmUpdated = new Film("Monsters University", "Kori Rae",
                2013, 2, 3, 1,
                2, 150, 160, false);
        final Film[] dataFilms = TablesTestData.getFilms();

        // fill the table
        for (Film film : dataFilms) {
            Assert.assertTrue(film_s.addFilm(film));
        }

        // read the whole table
        List<Film> films = film_s.loadAll();
        Assert.assertEquals(films.size(), dataFilms.length);
        for (int i = 0; i < films.size(); ++i) {
            Assert.assertEquals(films.get(i), dataFilms[i]);
        }

        // find by id & update
        Assert.assertNull(film_s.findFilmById(nonExistentId));
        filmUpdated.setFilm_id(nonExistentId);
        Assert.assertFalse(film_s.updateFilm(filmUpdated));
        filmUpdated.setFilm_id(existentId);
        Assert.assertTrue(film_s.updateFilm(filmUpdated));
        Assert.assertEquals(filmUpdated, film_s.findFilmById(existentId));

        // save object with initialized filed "id"
        Assert.assertFalse(film_s.addFilm(filmUpdated));

        // remove all objects from the table
        for (Film film : films) {
            Assert.assertTrue(film_s.deleteFilmById(film.getFilm_id()));
        }
    }

    @Test(groups = {"basicServiceTest"}, dependsOnMethods = {"testClientService", "testFilmService"})
    public void testOrderServiceBasic() throws IOException, SQLException {
        PrepareDatabase.initEmptyDB();
        // Orders are related to clients and films.
        // Therefore, it is required to add values to these two tables

        final Client[] dataClients = TablesTestData.getClients();
        for (Client client : dataClients) { client_s.addClient(client); }
        final Film[] dataFilms = TablesTestData.getFilms();
        for (Film film : dataFilms) { film_s.addFilm(film); }

        final Order[] dataOrders = TablesTestData.getOrders(dataClients, dataFilms);
        final long nonExistentId = 100;
        final long existentId = 1;
        final Order orderUpdated = new Order(dataClients[0], dataFilms[1], "disc", 50,
                java.sql.Date.valueOf("2008-11-01"), java.sql.Date.valueOf("2008-12-01"));

        // fill the table
        for (Order order : dataOrders) {
            Assert.assertTrue(order_s.addOrder(order));
        }

        // read the whole table
        List<Order> orders = order_s.loadAll();
        Assert.assertEquals(orders.size(), dataOrders.length);
        for (int i = 0; i < orders.size(); ++i) {
            Assert.assertEquals(orders.get(i), dataOrders[i]);
        }

        // find by id & update
        Assert.assertNull(order_s.findOrderById(nonExistentId));
        orderUpdated.setOrder_id(nonExistentId);
        Assert.assertFalse(order_s.updateOrder(orderUpdated));
        orderUpdated.setOrder_id(existentId);
        Assert.assertTrue(order_s.updateOrder(orderUpdated));
        Assert.assertEquals(orderUpdated, order_s.findOrderById(existentId));

        // save object with initialized filed "id"
        Assert.assertFalse(order_s.addOrder(orderUpdated));

        // return Film
        Assert.assertNull(order_s.returnFilmByOrderId(1, null));  // this order is already returned
        Assert.assertNotNull(order_s.returnFilmByOrderId(3, null));

        // remove all objects from the table
        for (Order order : orders) {
            Assert.assertTrue(order_s.deleteOrderById(order.getOrder_id()));
        }
    }

    @Test(dependsOnGroups =  {"basicServiceTest"})
    public void testSpecificOrderMethods() throws IOException, SQLException {
        PrepareDatabase.initEmptyDB();
        final Client[] dataClients = TablesTestData.getClients();
        for (Client client : dataClients) { client_s.addClient(client); }
        final Film[] dataFilms = TablesTestData.getFilms();
        for (Film film : dataFilms) { film_s.addFilm(film); }
        final Order[] dataOrders = TablesTestData.getOrders(dataClients, dataFilms);
        for (Order order : dataOrders) { order_s.addOrder(order); }

        List<Order> ordersClientInPeriod = order_s.getOrdersOfClientForSpecifiedPeriod(
                2, java.sql.Date.valueOf("2012-01-01"), java.sql.Date.valueOf("2012-12-31"));
        Assert.assertEquals(ordersClientInPeriod.size(), 1);
        Assert.assertEquals(ordersClientInPeriod.get(0), dataOrders[1]);

        List<Order> ordersNotReturned = order_s.getOrdersOfClientNotReturned(2);
        Assert.assertEquals(ordersNotReturned.size(), 1);
        Assert.assertEquals(ordersNotReturned.get(0), dataOrders[2]);

        List<Order> ordersFilmInPeriod = order_s.getOrdersOfFilmForSpecifiedPeriod(
                2, java.sql.Date.valueOf("2012-01-01"), java.sql.Date.valueOf("2012-12-31"));
        Assert.assertEquals(ordersFilmInPeriod.size(), 1);
        Assert.assertEquals(ordersFilmInPeriod.get(0), dataOrders[1]);

        PrepareDatabase.initEmptyDB();
    }
}