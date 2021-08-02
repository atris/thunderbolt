/*
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.atris.thunderbolt.operations;

import io.github.atris.thunderbolt.MutableFst;
import io.github.atris.thunderbolt.io.Convert;
import io.github.atris.thunderbolt.semiring.ProbabilitySemiring;
import io.github.atris.thunderbolt.utils.FstUtils;
import io.github.atris.thunderbolt.Fst;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John Salatas jsalatas@users.sourceforge.net
 */
public class RemoveEpsilonTest {

  @Test
  public void testRemoveEpsilon() {
    System.out.println("Testing RmEpsilon...");

    MutableFst fst = Convert.importFst("data/tests/algorithms/rmepsilon/A",
                                       new ProbabilitySemiring());
    MutableFst fstRmEps = Convert.importFst("data/tests/algorithms/rmepsilon/expected",
                                            new ProbabilitySemiring());
    Fst rmEpsilon = RemoveEpsilon.remove(fst);

    if (!FstUtils.fstEquals(fstRmEps, rmEpsilon, 0.0000001)) {
      Assert.fail("Should be " + fstRmEps.toString() + " but was " + rmEpsilon.toString());
    }
  }
}
