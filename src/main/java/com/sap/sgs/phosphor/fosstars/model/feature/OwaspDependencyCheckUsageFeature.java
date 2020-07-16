package com.sap.sgs.phosphor.fosstars.model.feature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sap.sgs.phosphor.fosstars.model.value.OwaspDependencyCheckUsage;
import com.sap.sgs.phosphor.fosstars.model.value.OwaspDependencyCheckUsageValue;
import java.util.Objects;

/**
 * A feature which holds a {@link OwaspDependencyCheckUsage} value.
 */
public class OwaspDependencyCheckUsageFeature extends AbstractFeature<OwaspDependencyCheckUsage> {

  /**
   * Initializes a feature.
   */
  @JsonCreator
  public OwaspDependencyCheckUsageFeature() {
    super("How OWASP Dependency Check is used");
  }

  /**
   * Creates a value of the features.
   *
   * @param value of type {@link OwaspDependencyCheckUsage}.
   * @return A value of the feature.
   */
  public OwaspDependencyCheckUsageValue value(OwaspDependencyCheckUsage value) {
    return new OwaspDependencyCheckUsageValue(this, value);
  }

  @Override
  public OwaspDependencyCheckUsageValue parse(String string) {
    Objects.requireNonNull(string, "String can't be null");
    return value(OwaspDependencyCheckUsage.valueOf(string));
  }
}