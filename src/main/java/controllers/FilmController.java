package controllers;

import dataAccess.film.Film;
import dataAccess.film.FilmService;
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
public class FilmController {
    FilmService filmService = new FilmService();
    OrderService orderService = new OrderService();

    @GetMapping("/filmsList")
    public String filmsListPage(Model model) {
        List<Film> films = filmService.loadAllActualSorted();  // todo load not all films
        model.addAttribute("films", films);
        return "filmsList";
    }

    @GetMapping("/film")
    public String filmPage(@RequestParam(name = "film_id", required = true) int film_id,
                           @RequestParam(name = "issue_date_from", required = false) String issue_date_from,
                           @RequestParam(name = "issue_date_to", required = false) String issue_date_to,
                           Model model) {
        Film film = filmService.findFilmById(film_id);
        if (film == null){
            model.addAttribute("error_msg", "There is no film with id=" + film_id);
            return "errorShow";
        }
        model.addAttribute("film", film);

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

        List<Order> orders = orderService.getOrdersOfFilmForSpecifiedPeriod(film_id, startDate, endDate);
        model.addAttribute("orders", orders);
        return "film";
    }

    @PostMapping("/filmSave")
    public String filmSavePage(@RequestParam(name = "film_id", required = false) Integer film_id,
                               @RequestParam(name = "film_name") String film_name,
                               @RequestParam(name = "producer") String producer,
                               @RequestParam(name = "release_year") int release_year,
                               @RequestParam(name = "cassette_total_number") int cassette_total_number,
                               @RequestParam(name = "disc_total_number") int disc_total_number,
                               @RequestParam(name = "cassette_price") int cassette_price,
                               @RequestParam(name = "disc_price") int disc_price,
                               Model model) {
        Film film = null;
        boolean changeIsSuccessful = false;

        if (film_id != null) {
            film = filmService.findFilmById(film_id);
            if (film != null) {
                film.setFilm_name(film_name);
                film.setProducer(producer);
                film.setRelease_year(release_year);
                if(!film.updateCassetteNumber(cassette_total_number)){
                    model.addAttribute("error_msg",
                            "the number of cassettes distributed is greater than newTotalCassetteNumber");
                    return "errorShow";
                }
                if(!film.updateDiscNumber(disc_total_number)){
                    model.addAttribute("error_msg",
                            "the number of discs distributed is greater than newTotalCassetteNumber");
                    return "errorShow";
                }
                film.setCassette_price(cassette_price);
                film.setDisc_price(disc_price);
                changeIsSuccessful = filmService.updateFilm(film);
            }
        }
        if (film == null) {
            film = new Film(film_name, producer, release_year,
                    cassette_total_number, disc_total_number,
                    cassette_total_number,  // is cassette_available_number,
                    disc_total_number,  // is  disc_available_number,
                    cassette_price, disc_price, false);
            changeIsSuccessful = filmService.addFilm(film);
        }

        if (changeIsSuccessful) {
            // todo return page with params somehow pretty
            return String.format("redirect:/film?film_id=%d", film.getFilm_id());
        }
        model.addAttribute("error_msg", "Adding the order was not successful");
        return "errorShow";
    }

    @PostMapping("/filmDelete")
    public String filmDeletePage(@RequestParam(name = "film_id", required = true) Integer film_id, Model model){
        boolean result = filmService.safetyDeleteFilmById(film_id);
        if (!result){ return String.format("redirect:/film?id=%d", film_id); }
        return "redirect:/filmsList";
    }

    @PostMapping("/filmAdd")
    public String filmAddPage(@RequestParam(name = "film_id", required = false) Integer film_id, Model model) {
        // this method fill html with info about film if film_id != null (update film)
        // and return not filled html if film_id == null (add film)
        if (film_id == null) {
            model.addAttribute("film", new Film());
            return "filmAdd";
        }

        Film film = filmService.findFilmById(film_id);
        if (film == null) {
            model.addAttribute("error_msg", "There is no film with id=" + film_id);
            return "errorShow";
        }

        model.addAttribute("film", film);
        return "filmAdd";
    }
}
