package gov.nasa.jpl.aerie.timeline.collections

import gov.nasa.jpl.aerie.timeline.BaseTimeline
import gov.nasa.jpl.aerie.timeline.Interval
import gov.nasa.jpl.aerie.timeline.Timeline
import gov.nasa.jpl.aerie.timeline.ops.NonZeroDurationOps
import gov.nasa.jpl.aerie.timeline.ops.SerialOps
import gov.nasa.jpl.aerie.timeline.ops.coalesce.CoalesceIntervalsOp
import gov.nasa.jpl.aerie.timeline.util.preprocessList
import gov.nasa.jpl.aerie.timeline.util.sorted

/** A coalescing timeline of [Intervals][Interval] with no extra data. */
data class Windows(private val timeline: Timeline<Interval, Windows>):
    Timeline<Interval, Windows> by timeline,
    SerialOps<Interval, Windows>,
    CoalesceIntervalsOp<Windows>,
    NonZeroDurationOps<Interval, Windows>
{
  constructor(vararg intervals: Interval): this(intervals.asList())
  constructor(intervals: List<Interval>): this(BaseTimeline(::Windows, preprocessList(intervals) { true }))

  /** Calculates the union of this and another [Windows]. */
  infix fun union(other: Windows) = unsafeOperate { opts ->
    val combined = collect(opts) + other.collect(opts)
    combined.sorted()
  }

  /** Calculates the intersection of this and another [Windows]. */
  infix fun intersection(other: Windows) =
      unsafeMap2(::Windows, other) { _, _, i -> i }

  /** Calculates the complement; i.e. highlights everything that is not highlighted in this timeline. */
  fun complement() = unsafeOperate { opts ->
    val result = mutableListOf(opts.bounds)
    for (interval in collect(opts)) {
      result += result.removeLast() - interval
    }
    result
  }
}
