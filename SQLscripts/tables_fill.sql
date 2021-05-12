INSERT INTO Films (film_name, producer, release_year) VALUES
('Гарри Поттер и философский камень', 'Дэвид Хейман', 2001),
('Гарри Поттер и тайная комната', 'Дэвид Хейман', 2002),
('Гарри Поттер и узник Азкабана', 'Крис Коламбус', 2004),
('Гарри Поттер и Кубок огня', 'Крис Коламбус', 2005),
('Гарри Поттер и Орден Феникса', 'Марк Рэдклифф', 2007),
('Гарри Поттер и Принц-полукровка', 'Дэвид Баррон', 2009),
('Гарри Поттер и Дары Смерти. Часть 1', 'Дэвид Хейман', 2010),
('Гарри Поттер и Дары Смерти. Часть 2', 'Дэвид Хейман', 2011);

UPDATE Films SET cassette_price=101, disc_price=152, cassette_total_number=5, disc_total_number=10;


INSERT INTO Clients (client_name, phone) VALUES
('Пупкин Никонор Иванович', '8-927-856-80-89'),
('Пивасик-Винишко Татьяна Александровна', '8-351-267-50-52'),
('Лобызательная Эльвира Владленовна', '8-999-204-54-90'),
('Головач Елена Васильевна', '8-926-865-00-09'),
('Бердяев Николай Александрович', '8-925-745-01-11'),
('Блохэй Акула Изикеевич', '8-926-898-31-12'),
('Бирюков Андрей Матвеевич', '8-926-904-44-51'),
('Пеппе Лягух Мемович', '8-926-666-13-13'),
('Волосатова Анастасия Тентаклевна', '8-926-809-12-13'),
('Сколопендрова Зульфия Ибрагимовна', NULL),
('Первушина Александра Вадимовна', '8-800-302-00-60'),
('Пиши Курсач Дедлайнович', '8-495-952-91-61');


INSERT INTO Orders (client_id, film_id, medium, price, film_issue_date, film_return_date) VALUES
(11, 6, 'cassette', 200, '2020-01-12', '2020-02-13'),
(12, 6, 'disc', 300, '2019-11-13', '2019-11-15');

INSERT INTO Orders (client_id, film_id, medium, price, film_issue_date) VALUES
(4, 2, 'cassette', 300, '2021-01-10'),
(5, 5, 'cassette', 300, '2021-01-10'),
(6, 1, 'disc', 300, '2021-01-09'),
(7, 6, 'disc', 300, '2021-01-12'),
(2, 3, 'cassette', 300, '2021-01-12'),
(9, 1, 'cassette', 300, '2021-01-12'),
(8, 6, 'disc', 300, '2021-01-15'),
(9, 3, 'cassette', 300, '2021-01-16'),
(1, 3, 'disc', 300, '2021-01-11');


--  SET [medium]_available_number = [medium]_total_number - distributed
WITH Dist_table as (SELECT films.film_id,
                           count(CASE WHEN  film_return_date IS NULL AND medium='cassette' THEN 1 END) as "distributed"
                    FROM Films LEFT JOIN Orders USING (film_id) GROUP BY films.film_id)
UPDATE Films SET cassette_available_number = cassette_total_number - distributed
FROM Dist_table WHERE Dist_table.film_id = Films.film_id;

WITH Dist_table as (SELECT films.film_id,
                           count(CASE WHEN  film_return_date IS NULL AND medium='disc' THEN 1 END) as "distributed"
                    FROM Films LEFT JOIN Orders USING (film_id) GROUP BY films.film_id)
UPDATE Films SET disc_available_number = disc_total_number - distributed
FROM Dist_table WHERE Dist_table.film_id = Films.film_id;
