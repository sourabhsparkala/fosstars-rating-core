package com.sap.sgs.phosphor.fosstars.model.feature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.value.LgtmGrade;
import com.sap.sgs.phosphor.fosstars.model.value.LgtmGradeValue;

/**
 * This feature contains the worse grade assigned by LGTM.
 */
public class LgtmGradeFeature extends AbstractFeature<LgtmGrade> {

  /**
   * Initializes a new feature.
   *
   * @param name A name of the feature.
   */
  @JsonCreator
  public LgtmGradeFeature(@JsonProperty("name") String name) {
    super(name);
  }

  @Override
  public LgtmGradeValue value(LgtmGrade grade) {
    return new LgtmGradeValue(this, grade);
  }

  @Override
  public Value<LgtmGrade> parse(String string) {
    return value(LgtmGrade.parse(string));
  }
}
