package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;


public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        String sqlQuery = "insert into time_entries(project_id, user_id, date, hours) " +
                "values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, timeEntry.getProjectId());
            stmt.setLong(2, timeEntry.getUserId());
            stmt.setDate(3, Date.valueOf(timeEntry.getDate()));
            stmt.setInt(4, timeEntry.getHours());
            return stmt;
        }, keyHolder);
        //Return the retrieved record
        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update("UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                        "WHERE id = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);
        return find(id);
    }

    @Override
    public TimeEntry find(long id) {
        return jdbcTemplate.query("select id, project_id, user_id, date, hours from time_entries where id = ?",
                new Object[]{id}, extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("select id, project_id, user_id, date, hours from time_entries", mapper);
    }

    @Override
    public TimeEntry delete(long id) {
        jdbcTemplate.update("delete from time_entries where id = ?", Long.valueOf(id));
        return find(id);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
