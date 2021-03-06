package com.sap.sgs.phosphor.fosstars.data;

import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.IS_ECLIPSE;

import com.sap.sgs.phosphor.fosstars.model.ValueSet;
import com.sap.sgs.phosphor.fosstars.model.value.BooleanValue;

/**
 * The data provider tries to figure out if an open-source project belongs to the Eclipse Software
 * Foundation.
 */
public class IsEclipse implements DataProvider {

  /**
   * An organization or username on GitHub.
   */
  private final String where;

  /**
   * Initializes a data provider.
   *
   * @param where An organization or username on GitHub.
   */
  public IsEclipse(String where) {
    this.where = where;
  }

  @Override
  public IsEclipse update(ValueSet values) {
    System.out.println(
        "[+] Figuring out if the project belongs to the Eclipse Software Foundation ...");
    values.update(new BooleanValue(IS_ECLIPSE, "eclipse".equalsIgnoreCase(where)));
    return this;
  }

  @Override
  public DataProvider set(UserCallback callback) {
    return this;
  }
}
