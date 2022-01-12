package gov.nasa.jpl.aerie.merlin.server.remotes;

import gov.nasa.jpl.aerie.merlin.driver.ActivityInstanceId;
import gov.nasa.jpl.aerie.merlin.protocol.types.SerializedValue;
import gov.nasa.jpl.aerie.merlin.server.exceptions.NoSuchActivityInstanceException;
import gov.nasa.jpl.aerie.merlin.server.exceptions.NoSuchPlanException;
import gov.nasa.jpl.aerie.merlin.server.models.ActivityInstance;
import gov.nasa.jpl.aerie.merlin.server.models.Constraint;
import gov.nasa.jpl.aerie.merlin.server.models.NewPlan;
import gov.nasa.jpl.aerie.merlin.server.models.Plan;
import gov.nasa.jpl.aerie.merlin.server.models.PlanId;
import gov.nasa.jpl.aerie.merlin.server.models.ProfileSet;
import gov.nasa.jpl.aerie.merlin.server.models.Timestamp;
import gov.nasa.jpl.aerie.merlin.server.services.RevisionData;

import java.util.List;
import java.util.Map;

/**
 * An owned interface to a concurrency-safe store of plans.
 *
 * A {@code PlanRepository} provides access to a shared store of plans, each indexed by a unique ID.
 * To support concurrent access, updates to the store must be concurrency-controlled. Every concurrent agent must have its
 * own {@code PlanRepository} reference, so that the reads and writes of each agent may be tracked analogously to
 * <a href="https://en.wikipedia.org/wiki/Load-link/store-conditional">load-link/store-conditional</a> semantics.
 */
public interface PlanRepository {
  // Queries
  Map<PlanId, Plan> getAllPlans();
  Plan getPlan(PlanId planId) throws NoSuchPlanException;
  long getPlanRevision(PlanId planId) throws NoSuchPlanException;
  RevisionData getPlanRevisionData(PlanId planId) throws NoSuchPlanException;
  Map<ActivityInstanceId, ActivityInstance> getAllActivitiesInPlan(PlanId planId) throws NoSuchPlanException;

  // Mutations
  CreatedPlan createPlan(NewPlan plan) throws MissionModelRepository.NoSuchMissionModelException;
  PlanTransaction updatePlan(PlanId planId) throws NoSuchPlanException;
  void deletePlan(PlanId planId) throws NoSuchPlanException;

  ActivityInstanceId createActivity(PlanId planId, ActivityInstance activity) throws NoSuchPlanException;
  void deleteAllActivities(PlanId planId) throws NoSuchPlanException;

  Map<String, Constraint> getAllConstraintsInPlan(PlanId planId) throws NoSuchPlanException;

  long addExternalDataset(PlanId planId, Timestamp datasetStart, ProfileSet profileSet) throws NoSuchPlanException;

  record CreatedPlan(PlanId planId, List<ActivityInstanceId> activityIds) {}

  interface PlanTransaction {
    void commit() throws NoSuchPlanException;

    PlanTransaction setName(String name);
    PlanTransaction setStartTimestamp(Timestamp timestamp);
    PlanTransaction setEndTimestamp(Timestamp timestamp);
    PlanTransaction setConfiguration(Map<String, SerializedValue> configuration);
  }

  interface ActivityTransaction {
    void commit() throws NoSuchPlanException, NoSuchActivityInstanceException;

    ActivityTransaction setType(String type);
    ActivityTransaction setStartTimestamp(Timestamp timestamp);
    ActivityTransaction setParameters(Map<String, SerializedValue> parameters);
  }
}
