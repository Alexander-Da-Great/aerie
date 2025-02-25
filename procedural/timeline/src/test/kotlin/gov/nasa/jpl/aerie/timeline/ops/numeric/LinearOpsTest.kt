package gov.nasa.jpl.aerie.timeline.ops.numeric

import gov.nasa.jpl.aerie.timeline.Duration.Companion.seconds
import gov.nasa.jpl.aerie.timeline.collections.profiles.Real
import gov.nasa.jpl.aerie.timeline.payloads.LinearEquation
import gov.nasa.jpl.aerie.timeline.payloads.Segment
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

class LinearOpsTest {
  @Test
  fun shift() {
    val result = Real(
        Segment(seconds(0)..seconds(1), LinearEquation(seconds(0), 1.0, 2.0))
    ).shift(seconds(5)).collect()

    assertIterableEquals(
        listOf(Segment(seconds(5)..seconds(6), LinearEquation(seconds(5), 1.0, 2.0))),
        result
    )
  }
}
