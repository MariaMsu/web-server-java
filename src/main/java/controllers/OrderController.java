package controllers;

import dataAccess.client.Client;
import dataAccess.client.ClientService;
import dataAccess.film.Film;
import dataAccess.film.FilmService;
import dataAccess.order.Order;
import dataAccess.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;


@Controller
public class OrderController {
    FilmService filmService = new FilmService();
    ClientService clientService = new ClientService();
    OrderService orderService = new OrderService();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/order")
    public String orderAddPage(@RequestParam(name = "order_id", required = true) Integer order_id,
                               @RequestParam(name = "return", required = false) Boolean toReturn,
                               Model model) {
        Order order;
        if (toReturn != null && toReturn) {
            order = orderService.returnFilmByOrderId(order_id, null);
        } else {
            order = orderService.findOrderById(order_id);
        }
        model.addAttribute("order", order);
        return "order";
    }

    @GetMapping("/orderAdd")
    public String orderAddPage(@RequestParam(name = "film_id", required = false) Integer film_id,
                               @RequestParam(name = "client_id", required = false) Integer client_id,
                               Model model) {
        List<Film> films = filmService.loadAllActualSorted();
        model.addAttribute("films", films);
        model.addAttribute("selected_film_id", film_id);

        List<Client> client = clientService.loadAllActualSorted();
        model.addAttribute("clients", client);
        model.addAttribute("selected_client_id", client_id);

        Date todayDate = new Date(System.currentTimeMillis());
        model.addAttribute("today_date", dateFormat.format(todayDate));
        return "orderAdd";
    }

    @PostMapping("/orderSave")
    public String orderSavePage(@RequestParam(name = "film_id", required = false) Integer film_id,
                                @RequestParam(name = "client_id", required = false) Integer client_id,
                                @RequestParam(name = "medium", required = false) String medium,
                                @RequestParam(name = "film_issue_date", required = false) Date film_issue_date,
                                Model model) {
        if (film_id == null || client_id == null || medium == null || film_issue_date == null){
            model.addAttribute("error_msg",
                    "To create order film_id, client_id,  client_id, film_issue_date are required.\n" +
                            "Provided: " + film_id + ", " + client_id + ", " + medium + ", " + film_issue_date);
            return "errorShow";
        }
        Client client = clientService.findClientById(client_id);
        if (client == null) {
            model.addAttribute("error_msg", "There is no client with id=" + client_id);
            return "errorShow";
        }

        Film film = filmService.findFilmById(film_id);
        if (film == null) {
            model.addAttribute("error_msg", "There is no film with id=" + film_id);
            return "errorShow";
        }

        int price;
        switch (medium) {
            case "cassette" -> price = film.getCassette_price();
            case "disc" -> price = film.getDisc_price();
            default -> {
                model.addAttribute("error_msg", "Incorrect medium type=" + medium);
                return "errorShow";
            }
        }

        Order order = new Order(
                client, film, medium, price, film_issue_date, null);
        boolean savingIsSuccessful = orderService.addOrder(order);
        if (!savingIsSuccessful){
            model.addAttribute("error_msg", "Adding the order was not successful\n" +
                    "This could have happened due to the lack of free mediums");
            return "errorShow";
        }
        return String.format("redirect:/order?order_id=%d", order.getOrder_id());
    }
}

