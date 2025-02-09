package gov.nasa.jpl.aerie.scheduler.server.remotes.postgres;

import gov.nasa.jpl.aerie.scheduler.server.models.GoalId;
import gov.nasa.jpl.aerie.scheduler.server.models.GoalRecord;
import gov.nasa.jpl.aerie.scheduler.server.models.GoalSource;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*package-local*/ final class GetSpecificationGoalsAction implements AutoCloseable {
  private final @Language("SQL") String sql = """
      select s.goal_id, gd.revision, gm.name, gd.definition, s.simulate_after
      from scheduler.scheduling_specification_goals s
      left join scheduler.scheduling_goal_definition gd using (goal_id)
      left join scheduler.scheduling_goal_metadata gm on s.goal_id = gm.id
      where s.specification_id = ?
      and s.enabled
      and ((s.goal_revision is not null and s.goal_revision = gd.revision)
      or (s.goal_revision is null and gd.revision = (select def.revision
                                                      from scheduler.scheduling_goal_definition def
                                                      where def.goal_id = s.goal_id
                                                      order by def.revision desc limit 1)))
      order by s.priority;
      """;

  private final PreparedStatement statement;

  public GetSpecificationGoalsAction(final Connection connection) throws SQLException {
    this.statement = connection.prepareStatement(sql);
  }

  public List<GoalRecord> get(final long specificationId) throws SQLException {
    this.statement.setLong(1, specificationId);
    final var resultSet = this.statement.executeQuery();

    final var goals = new ArrayList<GoalRecord>();
    while (resultSet.next()) {
      final var id = resultSet.getLong("goal_id");
      final var revision = resultSet.getLong("revision");
      final var name = resultSet.getString("name");
      final var definition = resultSet.getString("definition");
      final var simulateAfter = resultSet.getBoolean("simulate_after");
      goals.add(new GoalRecord(new GoalId(id, revision), name, new GoalSource(definition), simulateAfter));
    }

    return goals;
  }

  @Override
  public void close() throws SQLException {
    this.statement.close();
  }
}
