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
import io.github.atris.thunderbolt.semiring.TropicalSemiring;
import io.github.atris.thunderbolt.utils.FstUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author John Salatas jsalatas@users.sourceforge.net
 */
public class ConnectTest {

  @Test
  public void testConnect() {
    MutableFst fst = Convert.importFst("data/tests/algorithms/connect/A",
                                       TropicalSemiring.INSTANCE);
    MutableFst connectSaved = Convert.importFst("data/tests/algorithms/connect/expected",
                                                TropicalSemiring.INSTANCE);
    Connect.apply(fst);

    assertTrue(FstUtils.fstEquals(fst, connectSaved, FstUtils.LOG_REPORTER));

  }

  @Test
  public void testConnectNoOp() {
    MutableFst fst = Convert.importFst("data/tests/algorithms/connect/B");
    MutableFst connectSaved = Convert.importFst("data/tests/algorithms/connect/B");
    Connect.apply(fst);

    assertTrue(FstUtils.fstEquals(fst, connectSaved, FstUtils.LOG_REPORTER));
  }

  @Test
  public void testConnectWithStateSymbols() {
    MutableFst fst = Convert.importFst("data/tests/algorithms/connect2/A",
                                       TropicalSemiring.INSTANCE);
    assertTrue(fst.isUsingStateSymbols());
    MutableFst connectSaved = Convert.importFst("data/tests/algorithms/connect2/expected",
                                                TropicalSemiring.INSTANCE);
    Connect.apply(fst);
    assertTrue(fst.isUsingStateSymbols());

    assertTrue(FstUtils.fstEquals(fst, connectSaved, FstUtils.LOG_REPORTER));

  }
}
