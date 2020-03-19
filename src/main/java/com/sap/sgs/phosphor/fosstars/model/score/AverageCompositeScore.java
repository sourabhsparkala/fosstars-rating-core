package com.sap.sgs.phosphor.fosstars.model.score;

import static com.sap.sgs.phosphor.fosstars.model.other.Utils.setOf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.sgs.phosphor.fosstars.model.Feature;
import com.sap.sgs.phosphor.fosstars.model.Score;
import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.value.ScoreValue;
import com.sap.sgs.phosphor.fosstars.model.value.ValueHashSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A base class for scores which are based on an average of other scores (sub-scores).
 */
public class AverageCompositeScore extends AbstractScore {

  /**
   * A set of sub-scores.
   */
  private final Set<Score> subScores;

  /**
   * Initializes a new {@link AverageCompositeScore} with a number of sub-scores.
   *
   * @param name A name of the score.
   * @param subScores A number of sub-scores.
   */
  public AverageCompositeScore(String name, Score... subScores) {
    this(name, setOf(subScores));
  }

  /**
   * Initializes a new {@link AverageCompositeScore} with a set of sub-scores.
   *
   * @param name A name of the score.
   * @param subScores A set of sub-scores.
   */
  @JsonCreator
  AverageCompositeScore(
      @JsonProperty("name") String name,
      @JsonProperty("subScores") Set<Score> subScores) {

    super(name);
    Objects.requireNonNull(subScores, "Sub-scores can't be null!");
    if (subScores.isEmpty()) {
      throw new IllegalArgumentException("Sub-scores can't be empty!");
    }
    this.subScores = new HashSet<>(subScores);
  }

  /**
   * The score doesn't use any feature directory
   * so that this method returns an empty set.
   *
   * @return An empty set of features.
   */
  @Override
  public Set<Feature> features() {
    return Collections.emptySet();
  }

  @Override
  @JsonGetter("subScores")
  public Set<Score> subScores() {
    return new HashSet<>(subScores);
  }

  /**
   * Calculate an overall score value as an average of the underlying sub-scores.
   *
   * @param values A number of values.
   * @return An overall score.
   */
  @Override
  public ScoreValue calculate(Value... values) {
    ValueHashSet valueSet = new ValueHashSet(values);
    ScoreValue scoreValue = new ScoreValue(this);

    double subScoreSum = 0.0;
    double confidenceSum = 0.0;
    for (Score subScore : subScores) {
      ScoreValue subScoreValue = calculateIfNecessary(subScore, valueSet);
      scoreValue.usedValues(subScoreValue);
      subScoreSum += subScoreValue.get();
      confidenceSum += subScoreValue.confidence();
    }

    scoreValue.set(subScoreSum / subScores.size());
    scoreValue.confidence(confidenceSum / subScores.size());

    return scoreValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof AverageCompositeScore == false) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AverageCompositeScore that = (AverageCompositeScore) o;
    return Objects.equals(subScores, that.subScores);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subScores);
  }
}