package gov.nasa.jpl.aerie.merlin.server.services;

import gov.nasa.jpl.aerie.merlin.server.exceptions.NoSuchPlanException;
import gov.nasa.jpl.aerie.merlin.server.models.Constraint;
import gov.nasa.jpl.aerie.merlin.server.models.Plan;
import gov.nasa.jpl.aerie.merlin.server.models.PlanId;
import gov.nasa.jpl.aerie.merlin.server.models.ProfileSet;
import gov.nasa.jpl.aerie.merlin.server.models.Timestamp;

import java.util.Map;

public interface PlanService {
  Plan getPlan(PlanId planId) throws NoSuchPlanException;
  RevisionData getPlanRevisionData(PlanId planId) throws NoSuchPlanException;

  Map<String, Constraint> getConstraintsForPlan(PlanId planId) throws NoSuchPlanException;

  long addExternalDataset(PlanId planId, Timestamp datasetStart, ProfileSet profileSet) throws NoSuchPlanException;
}
