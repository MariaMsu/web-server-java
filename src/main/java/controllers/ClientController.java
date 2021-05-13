package controllers;

import dataAccess.client.Client;
import dataAccess.client.ClientService;

import dataAccess.film.Film;
import dataAccess.order.Order;
import dataAccess.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.List;


@Controller
public class ClientController {
    ClientService clientService = new ClientService();
    OrderService orderService = new OrderService();

    @GetMapping("/clientsList")
    public String filmsListPage(Model model) {
        List<Client> client = clientService.loadAllActualSorted();  // todo load not all films
        model.addAttribute("clients", client);
        return "clientsList";
    }

    @GetMapping("/client")
    public String clientPage(@RequestParam(name = "client_id", required = true) int client_id,
                             @RequestParam(name = "issue_date_from", required = false) String issue_date_from,
                             @RequestParam(name = "issue_date_to", required = false) String issue_date_to,
                             Model model) {
        Client client = clientService.findClientById(client_id);
        if (client == null){
            model.addAttribute("error_msg", "There is no client with id=" + client_id);
            return "errorShow";
        }
        model.addAttribute("client", client);

        Date startDate;
        try {
            startDate = java.sql.Date.valueOf(issue_date_from);
            model.addAttribute("issue_date_from", startDate);
        } catch (IllegalArgumentException e) {
            startDate = null;
            model.addAttribute("issue_date_from", "the beginning of the history");
        }
        Date endDate;
        try {
            endDate = java.sql.Date.valueOf(issue_date_to);
            model.addAttribute("issue_date_to", endDate);
        } catch (IllegalArgumentException e) {
            endDate = null;
            model.addAttribute("issue_date_to", "the end of the history");
        }

        List<Order> orders = orderService.getOrdersOfClientForSpecifiedPeriod(client_id, startDate, endDate);
        model.addAttribute("orders", orders);
        return "client";
    }

    @PostMapping("/clientSave")
    public String clientSavePage(@RequestParam(name = "client_id", required = false) Integer client_id,
                               @RequestParam(name = "client_name") String client_name,
                               @RequestParam(name = "phone") String phone,
                               Model model) {
        Client client = null;
        boolean changeIsSuccessful = false;

        if (client_id != null) {
            client = clientService.findClientById(client_id);
            if (client != null) {
                client.setClient_name(client_name);
                client.setPhone(phone);
                changeIsSuccessful = clientService.updateClient(client);
            }
        }
        if (client == null) {
            client = new Client(client_name, phone, false);
            changeIsSuccessful = clientService.addClient(client);
        }

        if (changeIsSuccessful) {
            // todo return page with params somehow pretty
            return String.format("redirect:/client?client_id=%d", client.getClient_id());
        }
        model.addAttribute("error_msg", "Adding the order was not successful");
        return "errorShow";
    }

    @PostMapping("/clientDelete")
    public String clientDeletePage(@RequestParam(name = "client_id", required = true) Integer client_id, Model model){
        boolean result = clientService.safetyDeleteClientById(client_id);
        if (!result){ return String.format("redirect:/client?client_id=%d", client_id); }  // todo
        return "redirect:/clientsList";
    }

    @PostMapping("/clientAdd")
    public String clientAddPage(@RequestParam(name = "client_id", required = false) Integer client_id, Model model) {
        // this method fill html with info about film if client_id != null (update film)
        // and return not filled html if client_id == null (add film)
        if (client_id == null) {
            model.addAttribute("client", new Client());
            return "clientAdd";
        }

        Client client = clientService.findClientById(client_id);
        if (client == null) {
            model.addAttribute("error_msg", "There is no client with id=" + client_id);
            return "errorShow";
        }

        model.addAttribute("client", client);
        return "clientAdd";
    }
}