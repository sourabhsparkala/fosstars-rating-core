package com.sap.sgs.phosphor.fosstars.model.score.oss;

import static com.sap.sgs.phosphor.fosstars.TestUtils.assertScore;
import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.NUMBER_OF_COMMITS_LAST_THREE_MONTHS;
import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS;
import static com.sap.sgs.phosphor.fosstars.model.other.Utils.setOf;
import static com.sap.sgs.phosphor.fosstars.model.score.oss.ProjectActivityScore.LOGISTIC_FOR_NUMBER_OF_COMMITS;
import static com.sap.sgs.phosphor.fosstars.model.score.oss.ProjectActivityScore.LOGISTIC_FOR_NUMBER_OF_CONTRIBUTORS;
import static org.junit.Assert.assertEquals;

import com.sap.sgs.phosphor.fosstars.model.Score;
import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.value.UnknownValue;
import java.util.Set;
import org.junit.Test;

public class ProjectActivityScoreTest {

  private static final ProjectActivityScore PROJECT_ACTIVITY = new ProjectActivityScore();
  private static final double delta = 0.01;

  @Test
  public void logisticForNumberOfCommits() {
    assertEquals(0.84, LOGISTIC_FOR_NUMBER_OF_COMMITS.value(0), delta);
    assertEquals(1.19, LOGISTIC_FOR_NUMBER_OF_COMMITS.value(10), delta);
    assertEquals(3.55, LOGISTIC_FOR_NUMBER_OF_COMMITS.value(50), delta);
    assertEquals(5.45, LOGISTIC_FOR_NUMBER_OF_COMMITS.value(80), delta);
    assertEquals(6.25, LOGISTIC_FOR_NUMBER_OF_COMMITS.value(100), delta);
  }

  @Test
  public void logisticForNumberOfContributors() {
    assertEquals(1.39, LOGISTIC_FOR_NUMBER_OF_CONTRIBUTORS.value(0), delta);
    assertEquals(1.55, LOGISTIC_FOR_NUMBER_OF_CONTRIBUTORS.value(1), delta);
    assertEquals(2.14, LOGISTIC_FOR_NUMBER_OF_CONTRIBUTORS.value(5), delta);
    assertEquals(2.66, LOGISTIC_FOR_NUMBER_OF_CONTRIBUTORS.value(10), delta);
    assertEquals(3.03, LOGISTIC_FOR_NUMBER_OF_CONTRIBUTORS.value(20), delta);
  }

  @Test
  public void calculate() {
    assertScore(Score.MIN, PROJECT_ACTIVITY, setOf(
            UnknownValue.of(NUMBER_OF_COMMITS_LAST_THREE_MONTHS),
            UnknownValue.of(NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS)));

    assertScore(Score.MIN, PROJECT_ACTIVITY, setOf(
            NUMBER_OF_COMMITS_LAST_THREE_MONTHS.value(0),
            UnknownValue.of(NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS)));

    assertScore(Score.MIN, PROJECT_ACTIVITY, setOf(
            UnknownValue.of(NUMBER_OF_COMMITS_LAST_THREE_MONTHS),
            NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS.value(0)));

    assertScore(Score.MIN, PROJECT_ACTIVITY, values(0, 0));

    // for one contributor
    assertScore(2.42, PROJECT_ACTIVITY, values(1, 1));
    assertScore(2.74, PROJECT_ACTIVITY, values(10, 1));
    assertScore(3.75, PROJECT_ACTIVITY, values(30, 1));
    assertScore(6.44, PROJECT_ACTIVITY, values(70, 1));
    assertScore(7.80, PROJECT_ACTIVITY, values(100, 1));
    assertScore(8.63, PROJECT_ACTIVITY, values(200, 1));

    // for more many contributors
    assertScore(2.89, PROJECT_ACTIVITY, values(10, 2));
    assertScore(3.90, PROJECT_ACTIVITY, values(30, 2));
    assertScore(6.60, PROJECT_ACTIVITY, values(70, 2));
    assertScore(7.95, PROJECT_ACTIVITY, values(100, 2));
    assertScore(8.78, PROJECT_ACTIVITY, values(200, 2));

    // for more many contributors
    assertScore(3.33, PROJECT_ACTIVITY, values(10, 5));
    assertScore(4.34, PROJECT_ACTIVITY, values(30, 5));
    assertScore(7.03, PROJECT_ACTIVITY, values(70, 5));
    assertScore(8.39, PROJECT_ACTIVITY, values(100, 5));
    assertScore(9.22, PROJECT_ACTIVITY, values(200, 5));
    assertScore(3.85, PROJECT_ACTIVITY, values(10, 10));
    assertScore(4.86, PROJECT_ACTIVITY, values(30, 10));
    assertScore(7.55, PROJECT_ACTIVITY, values(70, 10));
    assertScore(8.91, PROJECT_ACTIVITY, values(100, 10));
    assertScore(9.74, PROJECT_ACTIVITY, values(200, 10));

    // I wish all open-source projects were like that
    assertScore(Score.MAX, PROJECT_ACTIVITY, values(10000, 500));

    // or, even like that
    assertScore(Score.MAX, PROJECT_ACTIVITY, values(Integer.MAX_VALUE, Integer.MAX_VALUE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void commitsWithoutContributors() {
    PROJECT_ACTIVITY.calculate(values(1, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void contributorsWithoutCommits() {
    PROJECT_ACTIVITY.calculate(values(0, 1));
  }

  @Test
  public void moreContributorsThanCommits() {
    PROJECT_ACTIVITY.calculate(values(1, 2));
  }
  
  @Test
  public void moreCommitsThanContributors() {
    PROJECT_ACTIVITY.calculate(values(2, 1));
  }

  private static Set<Value> values(int commits, int contributors) {
    return setOf(
        NUMBER_OF_COMMITS_LAST_THREE_MONTHS.value(commits),
        NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS.value(contributors));
  }
}