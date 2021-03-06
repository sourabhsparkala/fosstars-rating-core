package com.sap.sgs.phosphor.fosstars.model.qa;

import static com.sap.sgs.phosphor.fosstars.model.other.Utils.allUnknown;
import static com.sap.sgs.phosphor.fosstars.model.qa.TestVectorBuilder.newTestVector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.sap.sgs.phosphor.fosstars.model.Interval;
import com.sap.sgs.phosphor.fosstars.model.Label;
import com.sap.sgs.phosphor.fosstars.model.Score;
import com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures;
import com.sap.sgs.phosphor.fosstars.model.math.DoubleInterval;
import com.sap.sgs.phosphor.fosstars.model.qa.TestVectorResult.Status;
import org.junit.Test;

public class TestVectorResultTest {

  public enum TestLabel implements Label {
    BAD, GOOD
  }

  private static final Interval ALMOST_MIN
      = DoubleInterval.init().from(Score.MIN).to(0.001).closed().make();

  @Test
  public void smoke() {
    TestVector vector = newTestVector()
        .set(allUnknown(OssFeatures.HAS_SECURITY_TEAM))
        .expectedScore(ALMOST_MIN)
        .expectedLabel(TestLabel.BAD)
        .make();
    TestVectorResult testVectorResult
        = new TestVectorResult(vector, 0, 5.0, Status.PASSED, "Alles kaputt!");
    assertEquals(0, testVectorResult.index);
    assertEquals("Alles kaputt!", testVectorResult.message);
    assertEquals(ALMOST_MIN, testVectorResult.vector.expectedScore());
    assertEquals(TestLabel.BAD, testVectorResult.vector.expectedLabel());
  }

  @Test
  public void equalsAndHashCode() {
    TestVector testVector =
        newTestVector()
            .set(allUnknown(OssFeatures.HAS_SECURITY_TEAM))
            .expectedScore(ALMOST_MIN)
            .expectedLabel(TestLabel.BAD)
            .make();
    TestVector sameTestVector =
        newTestVector()
            .set(allUnknown(OssFeatures.HAS_SECURITY_TEAM))
            .expectedScore(ALMOST_MIN)
            .expectedLabel(TestLabel.BAD)
            .make();
    TestVector differentTestVector =
        newTestVector()
            .set(allUnknown(OssFeatures.HAS_SECURITY_TEAM))
            .expectedScore(ALMOST_MIN)
            .expectedLabel(TestLabel.GOOD)
            .make();

    assertEquals(testVector, sameTestVector);
    assertEquals(testVector.hashCode(), sameTestVector.hashCode());
    assertNotEquals(testVector, differentTestVector);
    assertNotEquals(testVector.hashCode(), differentTestVector.hashCode());
    assertNotEquals(sameTestVector, differentTestVector);
    assertNotEquals(sameTestVector.hashCode(), differentTestVector.hashCode());

    assertEquals(
        new TestVectorResult(testVector, 0, 4.0, Status.PASSED,"Alles kaputt!"),
        new TestVectorResult(testVector, 0, 4.0, Status.PASSED,"Alles kaputt!"));
    assertEquals(
        new TestVectorResult(testVector, 0, 4.0, Status.PASSED,"Alles kaputt!"),
        new TestVectorResult(sameTestVector, 0, 4.0, Status.PASSED,"Alles kaputt!"));
  }

}