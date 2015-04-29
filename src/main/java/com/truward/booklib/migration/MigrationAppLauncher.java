package com.truward.booklib.migration;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public class MigrationAppLauncher implements Runnable {
  @Resource(name = "old.dao.jdbcTemplate")
  JdbcOperations oldDb;

  @Resource(name = "book.dao.jdbcTemplate")
  JdbcOperations bookDb;

  public static void main(String[] args) {
    if (System.getProperty("migrationApp.settings.path") == null) {
      System.err.println("Please, set migrationApp.settings.path JVM property");
      System.exit(-1);
      return;
    }

    try (final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/app.xml")) {
      context.refresh();
      context.start();

      context.getBean("migrationLauncher", Runnable.class).run();
    }
  }

  @Override
  public void run() {
    copyAuthors();
  }

  //
  // Private
  //

  private void copyAuthors() {
    final List<NamedValue> authors = oldDb.query("SELECT id, f_name AS name FROM author", new NamedValueMapper());
    bookDb.batchUpdate("INSERT INTO person (id, f_name) VALUES (?, ?)", new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, authors.get(i).id);
        ps.setString(2, authors.get(i).name);
      }

      @Override
      public int getBatchSize() {
        return authors.size();
      }
    });
    System.out.println("AUTHORS INSERTED");
  }

  private static final class NamedValue {
    final long id;
    final String name;

    public NamedValue(long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  private static final class NamedValueMapper implements RowMapper<NamedValue> {

    @Override
    public NamedValue mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new NamedValue(rs.getLong("id"), rs.getString("f_name"));
    }
  }
}
