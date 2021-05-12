package dataAccess.order;

import dataAccess.film.Film;
import dataAccess.film.FilmDAO;
import dataAccess.film.FilmDAOImpl;

import java.util.Calendar;
import java.sql.Date;
import java.util.List;

public class OrderService {
    private final FilmDAO filmDAO = new FilmDAOImpl();
    private final OrderDAO orderDAO = new OrderDAOImpl();

    public boolean addOrder(Order order) {
        if (!order.checkCorrectness()) { return false;  }

        // save order only if there is available mediums
        Film film = order.getFilm();
        switch (order.getMedium()) {
            case "cassette":
                int cassette_number = film.getCassette_available_number();
                if (cassette_number >= 1) {
                    film.setCassette_available_number(cassette_number - 1);
                } else return false;
                break;

            case "disc":
                int disc_number = film.getDisc_available_number();
                if (disc_number >= 1) {
                    film.setDisc_available_number(disc_number - 1);
                } else return false;
                break;

            default:
                return false;
        }
        // todo It looks badly. Here have to be something like transaction
        boolean savingIsSuccessful = this.orderDAO.save(order);
        if (!savingIsSuccessful) { return false; }
        return this.filmDAO.update(film);
    }

    public boolean deleteOrderById(long id) { return this.orderDAO.deleteById(id); }

    public boolean updateOrder(Order order) {
        if (order.checkCorrectness()) { return this.orderDAO.update(order); }
        return false;
    }

    public Order returnFilmByOrderId(long id, Date return_date) {
        Order order = this.findOrderById(id);
        if ((order == null) || (order.getFilm_return_date() != null)) {
            // there is no order with the given key or the order is already returned
            return null;
        }
        if (return_date == null){
            return_date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        }
        order.setFilm_return_date(return_date);
        boolean savingIsSuccessful = this.updateOrder(order);
        if (!savingIsSuccessful){ return null; }

        // increase number of available mediums
        Film film = order.getFilm();
        switch (order.getMedium()) {
            case "cassette":
                film.setCassette_available_number(film.getCassette_available_number() + 1);
                break;

            case "disc":
                film.setDisc_available_number(film.getDisc_available_number() + 1);
                break;

            default: return null;
        }

        if (!this.filmDAO.update(film)) { return null; }
        return order;
    }

    public Order findOrderById(long id) { return this.orderDAO.findById(id); }

    public List<Order> loadAll() { return this.orderDAO.loadAll(); }

    public List<Order> getOrdersOfClientForSpecifiedPeriod(long client_id, Date startDate, Date endDate) {
        return this.orderDAO.getOrdersOfClientForSpecifiedPeriod(client_id, startDate, endDate);
    }

    public List<Order> getOrdersOfFilmForSpecifiedPeriod(long film_id, Date startDate, Date endDate) {
        return this.orderDAO.getOrdersOfFilmForSpecifiedPeriod(film_id, startDate, endDate);
    }

    public List<Order> getOrdersOfClientNotReturned(long client_id) {
        return this.orderDAO.getOrdersOfClientNotReturned(client_id);
    }
}
