/*
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.github.atris.thunderbolt.utils;

import com.github.atris.thunderbolt.MutableFst;
import com.github.atris.thunderbolt.io.Convert;
import com.github.atris.thunderbolt.semiring.TropicalSemiring;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class FstUtilsTest {

  @Test
  public void shouldExecuteLogReporter() throws Exception {
    // just a smoke test that the log reporting infrastructure doesn't explode
    MutableFst read = Convert.importFst("data/openfst/cyclic", TropicalSemiring.INSTANCE);
    MutableFst read2 = Convert.importFst("data/openfst/basic", TropicalSemiring.INSTANCE);
    FstUtils.fstEquals(read, read2, FstUtils.LOG_REPORTER);
  }

  @Test
  public void shouldGiveBlankReportWhenEqual() throws Exception {
    MutableFst read = Convert.importFst("data/openfst/cyclic", TropicalSemiring.INSTANCE);
    MutableFst read2 = Convert.importFst("data/openfst/cyclic", TropicalSemiring.INSTANCE);

    StringReporter reporter = new StringReporter();
    FstUtils.fstEquals(read, read2, reporter);
    String report = reporter.toString();
    assertTrue(StringUtils.isBlank(report));
  }

  @Test
  public void shouldGiveNonBlankReportWhenNotEqual() throws Exception {
    MutableFst read = Convert.importFst("data/openfst/cyclic", TropicalSemiring.INSTANCE);
    MutableFst read2 = Convert.importFst("data/openfst/basic", TropicalSemiring.INSTANCE);

    StringReporter reporter = new StringReporter();
    FstUtils.fstEquals(read, read2, reporter);
    String report = reporter.toString();
    assertTrue(StringUtils.isNotBlank(report));
  }

  private static class StringReporter implements FstUtils.EqualsReporter {

    private final StringBuilder buffer = new StringBuilder();

    @Override
    public String toString() {
      return buffer.toString();
    }

    @Override
    public void report(String msg, Object a, Object b) {
      String aa = Objects.toString(a, "<null>");
      String bb = Objects.toString(b, "<null>");
      buffer.append("Equals difference: ")
          .append(msg)
          .append(" ")
          .append(aa)
          .append(" ")
          .append(bb)
          .append("\n");
    }
  }
}
