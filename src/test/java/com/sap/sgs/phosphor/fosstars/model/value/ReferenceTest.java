package com.sap.sgs.phosphor.fosstars.model.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

public class ReferenceTest {

  @Test
  public void serialization() throws IOException {
    Reference reference = new Reference("test", new URL("https://blog/post/1"));
    ObjectMapper mapper = new ObjectMapper();
    byte[] bytes = mapper.writeValueAsBytes(reference);
    assertNotNull(bytes);
    assertTrue(bytes.length > 0);
    Reference clone = mapper.readValue(bytes, Reference.class);
    assertEquals(reference, clone);
  }

}