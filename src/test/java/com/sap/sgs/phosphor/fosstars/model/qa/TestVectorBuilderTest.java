package com.sap.sgs.phosphor.fosstars.model.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.sap.sgs.phosphor.fosstars.model.feature.example.ExampleFeatures;
import com.sap.sgs.phosphor.fosstars.model.math.DoubleInterval;
import com.sap.sgs.phosphor.fosstars.model.rating.example.SecurityRatingExample.SecurityLabelExample;
import com.sap.sgs.phosphor.fosstars.model.value.IntegerValue;
import org.junit.Test;

public class TestVectorBuilderTest {

  @Test
  public void smoke() {
    TestVectorBuilder builder = TestVectorBuilder.newTestVector();
    TestVector vector = builder
        .set(new IntegerValue(ExampleFeatures.NUMBER_OF_COMMITS_LAST_MONTH_EXAMPLE, 10))
        .expectedLabel(SecurityLabelExample.AWESOME)
        .expectedScore(DoubleInterval.init().from(1).to(4).open().make())
        .make();

    assertNotNull(vector);
    assertEquals(1, vector.values().size());
    assertEquals(
        new IntegerValue(ExampleFeatures.NUMBER_OF_COMMITS_LAST_MONTH_EXAMPLE, 10),
        vector.values().iterator().next());
    assertEquals(SecurityLabelExample.AWESOME, vector.expectedLabel());
    assertEquals(DoubleInterval.init().from(1).to(4).open().make(), vector.expectedScore());
  }
}