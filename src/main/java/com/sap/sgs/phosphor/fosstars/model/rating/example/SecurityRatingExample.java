package com.sap.sgs.phosphor.fosstars.model.rating.example;

import static com.sap.sgs.phosphor.fosstars.model.score.example.ExampleScores.SECURITY_SCORE_EXAMPLE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.sgs.phosphor.fosstars.model.Label;
import com.sap.sgs.phosphor.fosstars.model.Score;
import com.sap.sgs.phosphor.fosstars.model.Version;
import com.sap.sgs.phosphor.fosstars.model.rating.AbstractRating;
import com.sap.sgs.phosphor.fosstars.model.score.example.SecurityScoreExample;

/**
 * This is a sample implementation of a security rating. Only for demo purposes.
 * The rating is based on SecurityScoreExample.
 */
public class SecurityRatingExample extends AbstractRating {

  public enum SecurityLabelExample implements Label {
    AWFUL, OKAY, AWESOME
  }

  /**
   * Initializes a security rating with SecurityScoreExample.
   */
  @JsonCreator
  SecurityRatingExample(@JsonProperty("version") Version version) {
    super("Security rating (example)", SECURITY_SCORE_EXAMPLE, version);
  }

  @Override
  public SecurityScoreExample score() {
    return (SecurityScoreExample) super.score();
  }

  /**
   * Implements a mapping from a score to a label.
   */
  @Override
  protected SecurityLabelExample label(double score) {
    Score.check(score);

    if (score < 3.0) {
      return SecurityLabelExample.AWFUL;
    }

    if (score < 8.0) {
      return SecurityLabelExample.OKAY;
    }

    return SecurityLabelExample.AWESOME;
  }

}
