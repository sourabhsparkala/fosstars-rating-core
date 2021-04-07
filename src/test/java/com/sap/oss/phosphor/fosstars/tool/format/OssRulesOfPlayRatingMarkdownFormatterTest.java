package com.sap.oss.phosphor.fosstars.tool.format;

import static com.sap.oss.phosphor.fosstars.model.other.Utils.allUnknown;
import static com.sap.oss.phosphor.fosstars.model.score.oss.OssRulesOfPlayScoreTest.allRulesPassed;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import com.sap.oss.phosphor.fosstars.model.RatingRepository;
import com.sap.oss.phosphor.fosstars.model.ValueSet;
import com.sap.oss.phosphor.fosstars.model.rating.oss.OssRulesOfPlayRating;
import com.sap.oss.phosphor.fosstars.model.rating.oss.OssRulesOfPlayRating.OssRulesOfPlayLabel;
import com.sap.oss.phosphor.fosstars.model.score.oss.OssRulesOfPlayScore;
import com.sap.oss.phosphor.fosstars.model.value.RatingValue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OssRulesOfPlayRatingMarkdownFormatterTest {

  private static final OssRulesOfPlayRating RATING
      = RatingRepository.INSTANCE.rating(OssRulesOfPlayRating.class);

  private static final Path CONFIG_PATH
      = Paths.get("OssRulesOfPlayRatingMarkdownFormatter.config.yml");

  @BeforeClass
  public static void setup() throws IOException {
    String content = "---\n"
        + "ruleIds:\n"
        + "  rl-license_file-1: If a project has a license\n"
        + "  rl-license_file-2: If a project uses an allowed license\n"
        + "  rl-license_file-3: If a license has disallowed text\n"
        + "  rl-readme_file-1: If a project has a README file";
    Files.write(CONFIG_PATH, content.getBytes());
  }

  @Test
  public void testPrintWithCompliantProject() {
    RatingValue ratingValue = RATING.calculate(allRulesPassed());
    assertEquals(OssRulesOfPlayLabel.PASS, ratingValue.label());
    OssRulesOfPlayRatingMarkdownFormatter formatter = new OssRulesOfPlayRatingMarkdownFormatter();
    String text = formatter.print(ratingValue);
    assertNotNull(text);
    assertFalse(text.isEmpty());
    String lowerCaseText = text.toLowerCase();
    assertTrue(lowerCaseText.contains("status"));
    assertTrue(lowerCaseText.contains("pass"));
    assertFalse(lowerCaseText.contains("fail"));
    assertFalse(lowerCaseText.contains("unclear"));
    assertFalse(lowerCaseText.contains("unknown"));
    checkRuleIds(text);
  }

  @Test
  public void testPrintWithNotCompliantProject() {
    ValueSet values = allRulesPassed();
    values.update(OssRulesOfPlayScore.EXPECTED_FALSE.iterator().next().value(true));
    RatingValue ratingValue = RATING.calculate(values);
    assertEquals(OssRulesOfPlayLabel.FAIL, ratingValue.label());
    OssRulesOfPlayRatingMarkdownFormatter formatter = new OssRulesOfPlayRatingMarkdownFormatter();
    String text = formatter.print(ratingValue);
    assertNotNull(text);
    assertFalse(text.isEmpty());
    String lowerCaseText = text.toLowerCase();
    assertTrue(lowerCaseText.contains("status"));
    assertTrue(lowerCaseText.contains("fail"));
    assertTrue(lowerCaseText.contains("violated"));
    assertFalse(lowerCaseText.contains("unclear"));
    assertFalse(lowerCaseText.contains("unknown"));
    checkRuleIds(text);
  }

  @Test
  public void testPrintWithUnclearProject() {
    RatingValue ratingValue = RATING.calculate(allUnknown(RATING.allFeatures()));
    assertEquals(OssRulesOfPlayLabel.UNCLEAR, ratingValue.label());
    OssRulesOfPlayRatingMarkdownFormatter formatter = new OssRulesOfPlayRatingMarkdownFormatter();
    String text = formatter.print(ratingValue);
    assertNotNull(text);
    assertFalse(text.isEmpty());
    String lowerCaseText = text.toLowerCase();
    assertTrue(lowerCaseText.contains("status"));
    assertTrue(lowerCaseText.contains("unclear"));
    assertFalse(lowerCaseText.contains("pass"));
    assertFalse(lowerCaseText.contains("fail"));
    checkRuleIds(text);
  }

  private static void checkRuleIds(String text) {
    assertTrue(text.contains("rl-license_file-1"));
    assertTrue(text.contains("rl-license_file-2"));
    assertTrue(text.contains("rl-license_file-3"));
    assertTrue(text.contains("rl-readme_file-1"));
  }

  @AfterClass
  public static void shutdown() throws IOException {
    FileUtils.forceDeleteOnExit(CONFIG_PATH.toFile());
  }
}