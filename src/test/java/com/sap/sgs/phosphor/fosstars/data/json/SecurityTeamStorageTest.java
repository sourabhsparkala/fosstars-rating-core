package com.sap.sgs.phosphor.fosstars.data.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;

public class SecurityTeamStorageTest {

  @Test
  public void testSpringSecurityOAuth() throws IOException {
    SecurityTeamStorage storage = SecurityTeamStorage.load();
    assertTrue(storage.supported("https://github.com/spring-projects/spring-security-oauth"));
  }

  @Test
  public void testApachePoi() throws IOException {
    SecurityTeamStorage storage = SecurityTeamStorage.load();
    assertTrue(storage.supported("https://github.com/apache/poi"));
  }

  @Test
  public void testUnknown() throws IOException {
    SecurityTeamStorage storage = SecurityTeamStorage.load();
    assertFalse(storage.supported("https://github.com/unknown/project"));
  }

}
