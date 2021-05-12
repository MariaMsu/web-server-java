package dataAccess.film;

import dataAccess.EntityWithId;

import javax.persistence.*;

@Entity
@Table(name = "Films")
public class Film implements EntityWithId {
    private long film_id = -1;
    private String film_name;
    private String producer;
    private Integer release_year;
    private Integer cassette_total_number;
    private Integer disc_total_number;
    private Integer cassette_available_number;
    private Integer disc_available_number;
    private Integer cassette_price;
    private Integer disc_price;
    private boolean film_is_removed;

    public Film() {}

    public Film(String film_name,
                String producer,
                int release_year,
                int cassette_total_number,
                int disc_total_number,
                int cassette_available_number,
                int disc_available_number,
                int cassette_price,
                int disc_price,
                boolean film_is_removed) {
        this.film_name = film_name;
        this.producer = producer;
        this.release_year = release_year;
        this.cassette_total_number = cassette_total_number;
        this.disc_total_number = disc_total_number;
        this.cassette_available_number = cassette_available_number;
        this.disc_available_number = disc_available_number;
        this.cassette_price = cassette_price;
        this.disc_price = disc_price;
        this.film_is_removed = film_is_removed;
    }

    @Id
    @Column(name = "film_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getFilm_id() {
        return film_id;
    }

    public void setFilm_id(long film_id) {
        this.film_id = film_id;
    }

    @Column(name = "film_name")
    public String getFilm_name() {
        return film_name;
    }

    public void setFilm_name(String film_name) {
        this.film_name = film_name;
    }

    @Column(name = "producer")
    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Column(name = "release_year")
    public Integer getRelease_year() {
        return release_year;
    }

    public void setRelease_year(int release_year) {
        this.release_year = release_year;
    }

    @Column(name = "cassette_total_number")
    public Integer getCassette_total_number() {
        return cassette_total_number;
    }

    public void setCassette_total_number(int cassette_total_number) {
        this.cassette_total_number = cassette_total_number;
    }

    @Column(name = "disc_total_number")
    public Integer getDisc_total_number() {
        return disc_total_number;
    }

    public void setDisc_total_number(int disc_total_number) {
        this.disc_total_number = disc_total_number;
    }

    @Column(name = "cassette_available_number")
    public Integer getCassette_available_number() {
        return cassette_available_number;
    }

    public void setCassette_available_number(int cassette_available_number) {
        this.cassette_available_number = cassette_available_number;
    }

    public boolean updateCassetteNumber(int newTotalCassetteNumber){
        int delta = newTotalCassetteNumber - this.cassette_total_number;
        if (delta == 0) { return true; }  // nothing is changed
        //  the number of cassettes distributed is greater than newTotalCassetteNumber
        if (-delta > this.cassette_available_number){ return false; }
        this.cassette_total_number += delta;
        this.cassette_available_number += delta;
        return true;
    }

    @Column(name = "disc_available_number")
    public Integer getDisc_available_number() {
        return disc_available_number;
    }

    public void setDisc_available_number(int disc_available_number) {
        this.disc_available_number = disc_available_number;
    }

    public boolean updateDiscNumber(int newTotalDiscNumber){
        int delta = newTotalDiscNumber - this.disc_total_number;
        if (delta == 0) { return true; }  // nothing is changed
        //  the number of discs distributed is greater than newTotalDiscNumber
        if (-delta > this.disc_available_number){ return false; }
        this.disc_total_number += delta;
        this.disc_available_number += delta;
        return true;
    }

    @Column(name = "cassette_price")
    public Integer getCassette_price() {
        return cassette_price;
    }

    public void setCassette_price(int cassette_price) {
        this.cassette_price = cassette_price;
    }

    @Column(name = "disc_price")
    public Integer getDisc_price() {
        return disc_price;
    }

    public void setDisc_price(int disc_price) {
        this.disc_price = disc_price;
    }

    @Column(name = "film_is_removed")
    public boolean getFilm_is_removed() {
        return film_is_removed;
    }

    public void setFilm_is_removed(boolean film_is_removed) {
        this.film_is_removed = film_is_removed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj.getClass() != this.getClass()) { return false; }
        final Film other = (Film) obj;
        return (this.film_id == other.film_id) &&
                this.film_name.equals(other.film_name) &&
                this.producer.equals(other.producer) &&
                (this.cassette_total_number.equals(other.cassette_total_number)) &&
                (this.disc_total_number.equals(other.disc_total_number)) &&
//                ignore this fields due to problems with data consistency (see README)
//                (this.cassette_available_number == other.cassette_available_number) &&
//                (this.disc_available_number == other.disc_available_number) &&
                (this.cassette_price.equals(other.cassette_price)) &&
                (this.disc_price.equals(other.disc_price)) &&
                (this.film_is_removed == other.film_is_removed);
    }

    @Override
    public String toString() {
        return "Film{" +
                "film_id=" + film_id +
                ", film_name='" + film_name + '\'' +
                ", producer='" + producer + '\'' +
                ", release_year=" + release_year +
                ", cassette_total_number=" + cassette_total_number +
                ", disc_total_number=" + disc_total_number +
                ", cassette_available_number=" + cassette_available_number +
                ", disc_available_number=" + disc_available_number +
                ", cassette_price=" + cassette_price +
                ", disc_price=" + disc_price +
                ", film_is_removed=" + film_is_removed +
                '}';
    }

    @Override
    public long receivePrimaryKey() {
        return getFilm_id();
    }

    public boolean checkCorrectness(){
        return ((this.cassette_total_number >= 0) &&
                (this.disc_total_number >= 0) &&
                (this.cassette_price >= 0) &&
                (this.disc_price >= 0)) &&

                (0 <= this.cassette_available_number) &&
                (this.cassette_available_number <= this.cassette_total_number)&&

                (0 <= this.disc_available_number) &&
                (this.disc_available_number <= this.disc_total_number);
    }
}
