package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("films.film_id"))
                .name(rs.getString("films.film_name"))
                .description(rs.getString("films.description"))
                .releaseDate(rs.getDate("films.release_date").toLocalDate())
                .duration(rs.getInt("films.duration"))
                .mpa(new Mpa(rs.getInt("mpas.rating_id"),
                        rs.getString("mpas.rating_name")))
                .build();
    }
}
