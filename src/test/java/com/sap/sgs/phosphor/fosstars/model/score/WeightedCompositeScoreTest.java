package com.sap.sgs.phosphor.fosstars.model.score;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sap.sgs.phosphor.fosstars.model.Confidence;
import com.sap.sgs.phosphor.fosstars.model.Feature;
import com.sap.sgs.phosphor.fosstars.model.Parameter;
import com.sap.sgs.phosphor.fosstars.model.Score;
import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.Weight;
import com.sap.sgs.phosphor.fosstars.model.feature.DoubleFeature;
import com.sap.sgs.phosphor.fosstars.model.score.WeightedCompositeScore.WeightedScore;
import com.sap.sgs.phosphor.fosstars.model.value.ScoreValue;
import com.sap.sgs.phosphor.fosstars.model.weight.MutableWeight;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class WeightedCompositeScoreTest {

  @Test
  public void scores() {
    Score score = new WeightedScoreImpl();
    assertEquals(0, score.features().size());
    assertEquals(2, score.subScores().size());
    assertTrue(score.subScores().contains(new FirstScore()));
    assertTrue(score.subScores().contains(new SecondScore()));
  }

  @Test
  public void calculate() {
    Score score = new WeightedScoreImpl();

    Value<Double> firstValue = FirstScore.FEATURE.value(FirstScore.VALUE);
    Value<Double> secondValue = SecondScore.FEATURE.value(SecondScore.VALUE);

    double firstScoreValue = new FirstScore().calculate(firstValue).get();
    double secondScoreValue = new SecondScore().calculate(secondValue).get();
    double weightedFirstScore = 0.8 * firstScoreValue;
    double weightedSecondScore = 0.3 * secondScoreValue;
    double weightSum = 0.8 + 0.3;
    double expectedScore = (weightedFirstScore + weightedSecondScore) / weightSum;

    ScoreValue scoreValue = score.calculate(firstValue, secondValue);
    assertEquals(expectedScore, scoreValue.get(), 0.01);
    assertEquals(Confidence.MAX, scoreValue.confidence(), 0.01);
    assertEquals(2, scoreValue.usedValues().size());
    for (Value value : scoreValue.usedValues()) {
      if (value.feature() instanceof FirstScore) {
        assertEquals(firstScoreValue, value.get());
        continue;
      }
      if (value.feature() instanceof SecondScore) {
        assertEquals(secondScoreValue, value.get());
        continue;
      }
      fail("Unexpected score: " + value.feature().name());
    }
  }

  @Test
  public void equalsAndHashCode() {
    Score one = new WeightedScoreImpl();
    Score two = new WeightedScoreImpl();
    assertEquals(one, two);
    assertEquals(one.hashCode(), two.hashCode());
    assertNotEquals(null, one);
    assertEquals(one, one);
  }

  @Test
  public void equalWeightedScores() {
    MutableWeight mutableWeightOne = new MutableWeight(0.7);
    MutableWeight mutableWeightTwo = new MutableWeight(0.7);
    assertEquals(mutableWeightOne, mutableWeightTwo);

    WeightedCompositeScore.WeightedScore one = new WeightedCompositeScore.WeightedScore(
        new FirstScore(), mutableWeightOne);
    WeightedCompositeScore.WeightedScore two = new WeightedCompositeScore.WeightedScore(
        new FirstScore(), mutableWeightTwo);
    assertEquals(one, two);
    assertEquals(one.hashCode(), two.hashCode());

    // check if WeightedScore considers only scores in equals() and hashCode()
    mutableWeightOne.value(0.5);
    assertNotEquals(mutableWeightOne, mutableWeightTwo);
    assertEquals(one, two);
    assertEquals(one.hashCode(), two.hashCode());
  }

  @Test
  public void validValue() {
    Score score = new WeightedScoreImpl();
    Value<Double> value = score.value(7.54);
    assertNotNull(value);
    assertEquals(7.54, value.get(), 0.001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeValue() {
    new WeightedScoreImpl().value(-3.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooBigValue() {
    new WeightedScoreImpl().value(42.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zeroWeights() {
    MutableWeight mutableWeightOne = new MutableWeight(0);
    MutableWeight mutableWeightTwo = new MutableWeight(0);

    WeightedCompositeScore.WeightedScore one = new WeightedCompositeScore.WeightedScore(
        new FirstScore(), mutableWeightOne);
    WeightedCompositeScore.WeightedScore two = new WeightedCompositeScore.WeightedScore(
        new SecondScore(), mutableWeightTwo);

    Set<WeightedScore> weightedScores = new HashSet<>();
    weightedScores.add(one);
    weightedScores.add(two);

    WeightedCompositeScore score = new WeightedCompositeScore("test", weightedScores);
    score.calculate();
  }

  @Test(expected = NullPointerException.class)
  public void nullName() {
    new WeightedCompositeScore(null, new FirstScore());
  }

  @Test(expected = NullPointerException.class)
  public void nullScoreList() {
    new WeightedCompositeScore("test", (Score[]) null);
  }

  @Test(expected = NullPointerException.class)
  public void nullScoreSet() {
    new WeightedCompositeScore("test", (Set<WeightedScore>) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyScoreList() {
    new WeightedCompositeScore("test", new Score[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyScoreSet() {
    new WeightedCompositeScore("test", new HashSet<>());
  }

  @Test
  public void allScoresPreCalculated() {
    Score score = new WeightedScoreImpl();

    double firstValue = 3.0;
    double secondValue = 2.0;

    ScoreValue firstPreCalculatedScoreValue = new ScoreValue(
        new FirstScore(), firstValue, Confidence.MAX, Collections.emptyList());
    ScoreValue secondPreCalculatedScoreValue = new ScoreValue(
        new SecondScore(), secondValue, Confidence.MAX, Collections.emptyList());
    ScoreValue scoreValue = score.calculate(
        firstPreCalculatedScoreValue, secondPreCalculatedScoreValue);
    assertNotNull(scoreValue);

    double weightedSum = firstValue * WeightedScoreImpl.FIRST_WEIGHT
        + secondValue * WeightedScoreImpl.SECOND_WEIGHT;
    double weightSum = WeightedScoreImpl.FIRST_WEIGHT + WeightedScoreImpl.SECOND_WEIGHT;
    double expectedScore = weightedSum / weightSum;

    assertEquals(expectedScore, scoreValue.get(), 0.001);
  }

  @Test
  public void oneScoresPreCalculated() {
    Score score = new WeightedScoreImpl();

    double firstValue = 3.0;

    ScoreValue preCalculatedScoreValue = new ScoreValue(
        new FirstScore(), firstValue, Confidence.MAX, Collections.emptyList());
    ScoreValue scoreValue = score.calculate(preCalculatedScoreValue);
    assertNotNull(scoreValue);

    double weightedSum = firstValue * WeightedScoreImpl.FIRST_WEIGHT
        + SecondScore.VALUE * WeightedScoreImpl.SECOND_WEIGHT;
    double weightSum = WeightedScoreImpl.FIRST_WEIGHT + WeightedScoreImpl.SECOND_WEIGHT;
    double expectedScore = weightedSum / weightSum;

    assertEquals(expectedScore, scoreValue.get(), 0.001);
  }

  @Test
  public void parameters() {
    WeightedScoreImpl score = new WeightedScoreImpl();
    Set<Score> subScores = score.subScores();
    assertNotNull(subScores);
    assertEquals(2, subScores.size());

    List<Weight> weights = score.parameters();
    assertNotNull(weights);
    assertEquals(2, weights.size());

    double weightSum = 0.0;
    for (Weight weight : weights) {
      weightSum += weight.value();
    }
    assertEquals(WeightedScoreImpl.FIRST_WEIGHT + WeightedScoreImpl.SECOND_WEIGHT, weightSum, 0.1);
  }

  @Test
  public void notImmutableByDefault() {
    WeightedScoreImpl score = new WeightedScoreImpl();
    assertFalse(score.isImmutable());
    for (Parameter parameter : score.parameters()) {
      final double w = 1.0;
      assertNotEquals(w, parameter.value());
      parameter.value(w);
      assertEquals(w, parameter.value(), 0.1);
    }
  }

  @Test
  public void makeImmutable() {
    WeightedScoreImpl score = new WeightedScoreImpl();
    score.makeImmutable();
    assertTrue(score.isImmutable());
    for (Parameter parameter : score.parameters()) {
      try {
        parameter.value(0.1);
        fail("Oh no! The parameter should not be modifiable!");
      } catch (UnsupportedOperationException e) {
        // expected
      }
    }
  }

  private static class FirstScore extends AbstractScore {

    private static final Feature<Double> FEATURE = new DoubleFeature("first feature");

    private static final double VALUE = 0.2;

    FirstScore() {
      super("First score");
    }

    @Override
    public Set<Feature> features() {
      return Collections.emptySet();
    }

    @Override
    public Set<Score> subScores() {
      return Collections.emptySet();
    }

    @Override
    public ScoreValue calculate(Value... values) {
      return scoreValue(VALUE, values);
    }

    @Override
    public int hashCode() {
      return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return obj.getClass() == getClass();
    }
  }

  private static class SecondScore extends FeatureBasedScore {

    private static final Feature<Double> FEATURE = new DoubleFeature("second feature");

    private static final double VALUE = 0.5;

    SecondScore() {
      super("Second score", FEATURE);
    }

    @Override
    public ScoreValue calculate(Value... values) {
      return scoreValue(VALUE, values);
    }

    @Override
    public int hashCode() {
      return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return obj.getClass() == getClass();
    }
  }

  private static class WeightedScoreImpl extends WeightedCompositeScore {

    private static final double FIRST_WEIGHT = 0.8;
    private static final double SECOND_WEIGHT = 0.3;

    WeightedScoreImpl() {
      super("Test score", init());
    }

    private static Set<WeightedScore> init() {
      Set<WeightedScore> scores = new HashSet<>();
      scores.add(new WeightedScore(new FirstScore(), new MutableWeight(FIRST_WEIGHT)));
      scores.add(new WeightedScore(new SecondScore(), new MutableWeight(SECOND_WEIGHT)));
      return scores;
    }
  }

}