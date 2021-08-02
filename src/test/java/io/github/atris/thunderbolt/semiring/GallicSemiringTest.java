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

package io.github.atris.thunderbolt.semiring;

import io.github.atris.thunderbolt.WeightGenerator;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GallicSemiringTest {

  private WeightGenerator<GallicSemiring.GallicWeight> weightGen;
  private SemiringTester<GallicSemiring.GallicWeight> semiTester;
  private GallicSemiring restrict;
  private GallicSemiring min;
  private double tropicalOne;

  @Before
  public void setUp() throws Exception {
    weightGen = WeightGenerator.makeGallic(0xEEF123);
    semiTester = new SemiringTester<>(weightGen);
    semiTester.setRandValuesToTest(200);
    restrict = new GallicSemiring(TropicalSemiring.INSTANCE, GallicSemiring.GallicMode.RESTRICT_GALLIC);
    min = new GallicSemiring(TropicalSemiring.INSTANCE, GallicSemiring.GallicMode.MIN_GALLIC);
    tropicalOne = TropicalSemiring.INSTANCE.one();
  }

  @Test
  public void testRestrictedSemiring() {
    semiTester.assertSemiringAndDivide(restrict);
  }

  @Test
  public void testMinSemiring() {
    semiTester.assertSemiringAndDivide(min);
  }

  @Test
  public void testNaturalOrdering() {
    GallicSemiring.GallicWeight a = GallicSemiring.GallicWeight.createFromGiven(1.0, 3);
    GallicSemiring.GallicWeight b = GallicSemiring.GallicWeight.createFromGiven(1.0, 1, 2);
    GallicSemiring.GallicWeight c = GallicSemiring.GallicWeight.createFromGiven(1.0, 1, 3);
    GallicSemiring.GallicWeight d = GallicSemiring.GallicWeight.createFromGiven(1.0, 3, 2);
    GallicSemiring.GallicWeight e = GallicSemiring.GallicWeight.createFromGiven(1.0, 1, 2, 3);
    assertEquals(0, GallicSemiring.SHORTLEX_ORDERING.compare(a, a));
    assertEquals(0, GallicSemiring.SHORTLEX_ORDERING.compare(e, e));
    assertEquals(-1, GallicSemiring.SHORTLEX_ORDERING.compare(a, e));
    assertEquals(1, GallicSemiring.SHORTLEX_ORDERING.compare(e, a));
    assertEquals(ImmutableList.of(a, b, c, d, e), GallicSemiring.SHORTLEX_ORDERING.sortedCopy(Arrays.asList(e, d, a, b, c)));
  }

  @Test
  public void testRestrictPlusWithLabels() {
    // the zero paths are handled by the general semiring tester
    GallicSemiring.GallicWeight result = restrict.plus(GallicSemiring.GallicWeight.createFromGiven(42.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(20.0, 10));
    SemiringTester.assertFuzzy(restrict, result, GallicSemiring.GallicWeight.createFromGiven(20.0, 10));
  }

  @Test
  public void testRestrictPlusWithEmpty() {
    // the zero paths are handled by the general semiring tester
    GallicSemiring.GallicWeight result = restrict.plus(
        GallicSemiring.GallicWeight.createFromGiven(42.0), GallicSemiring.GallicWeight.createFromGiven(20.0));
    SemiringTester.assertFuzzy(restrict, result, GallicSemiring.GallicWeight.createFromGiven(20.0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRestrictPlusFails() {
    // the zero paths are handled by the general semiring tester
    GallicSemiring.GallicWeight result = restrict.plus(GallicSemiring.GallicWeight.createFromGiven(42.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(20.0));
  }

  @Test
  public void testMinPlus1() {
    // the zero paths are handled by the general semiring tester
    GallicSemiring.GallicWeight result = min.plus(
        GallicSemiring.GallicWeight.createFromGiven(42.0), GallicSemiring.GallicWeight.createFromGiven(20.0, 10));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(20.0, 10));
  }

  @Test
  public void testMinPlus2() {
    // the zero paths are handled by the general semiring tester
    GallicSemiring.GallicWeight result = min.plus(
        GallicSemiring.GallicWeight.createFromGiven(42.0), GallicSemiring.GallicWeight.createFromGiven(84.0, 10));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(42.0));
  }

  @Test
  public void testTimes1() {
    GallicSemiring.GallicWeight result = min.times(
        GallicSemiring.GallicWeight.createFromGiven(42.0), GallicSemiring.GallicWeight.createFromGiven(12.0, 10));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(54.0, 10));

    GallicSemiring.GallicWeight result2 = min.times(GallicSemiring.GallicWeight.createFromGiven(12.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(42.0));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(54.0, 10));
  }

  @Test
  public void testTimes2() {
    GallicSemiring.GallicWeight result = min.times(
        GallicSemiring.GallicWeight.createFromGiven(42.0, 11), GallicSemiring.GallicWeight.createFromGiven(12.0, 10));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(54.0, 11, 10));

    GallicSemiring.GallicWeight result2 = min.times(GallicSemiring.GallicWeight.createFromGiven(12.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(42.0, 11));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(54.0, 10, 11));
  }

  @Test
  public void testTimes3() {
    GallicSemiring.GallicWeight result = min.times(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(12.0, 12));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(34.0, 10, 11, 12));

    GallicSemiring.GallicWeight result2 = min.times(GallicSemiring.GallicWeight.createFromGiven(12.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(22.0, 11, 12));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(34.0, 10, 11, 12));
  }

  @Test
  public void testDivide1() {
    GallicSemiring.GallicWeight result = min.divide(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(10.0, 10));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(12.0, 11));

    GallicSemiring.GallicWeight result2 = min.divide(GallicSemiring.GallicWeight.createFromGiven(10.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(22.0, 10, 11));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(-12.0));
  }

  @Test
  public void testDivide2() {
    GallicSemiring.GallicWeight result = min.divide(
        GallicSemiring.GallicWeight.createFromGiven(22.0), GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(12.0));

    GallicSemiring.GallicWeight result2 = min.divide(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(22.0));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(-12.0, 10, 11));
  }

  @Test
  public void testCommonDivisor1() {
    GallicSemiring.GallicWeight result = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(22.0), GallicSemiring.GallicWeight
        .createFromGiven(10.0, 10, 11));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(10.0));

    GallicSemiring.GallicWeight result2 = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(22.0));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(10.0));
  }

  @Test
  public void testCommonDivisor2() {
    GallicSemiring.GallicWeight result = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(22.0, 10), GallicSemiring.GallicWeight
        .createFromGiven(10.0, 10, 11));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(10.0, 10));

    GallicSemiring.GallicWeight result2 = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(22.0, 10));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(10.0, 10));

    GallicSemiring.GallicWeight result3 = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(22.0, 11));
    SemiringTester.assertFuzzy(min, result3, GallicSemiring.GallicWeight.createFromGiven(10.0));
  }

  @Test
  public void testCommonDivisor3() {
    GallicSemiring.GallicWeight result = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11, 12), GallicSemiring.GallicWeight
        .createFromGiven(10.0, 10, 11));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(10.0, 10));

    GallicSemiring.GallicWeight result2 = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), GallicSemiring.GallicWeight
        .createFromGiven(22.0, 10, 11, 12));
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(10.0, 10));
  }

  @Test
  public void testCommonDivisorZero() {
    GallicSemiring.GallicWeight
        result = min.commonDivisor(min.zero(), GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11));
    SemiringTester.assertFuzzy(min, result, GallicSemiring.GallicWeight.createFromGiven(10.0, 10));

    GallicSemiring.GallicWeight
        result2 = min.commonDivisor(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), min.zero());
    SemiringTester.assertFuzzy(min, result2, GallicSemiring.GallicWeight.createFromGiven(10.0, 10));
  }

  @Test
  public void testFactorizeEmpty() {
    Pair<GallicSemiring.GallicWeight, GallicSemiring.GallicWeight> pair = min.factorize(GallicSemiring.GallicWeight.createEmptyLabels(10.0));
    assertEquals(GallicSemiring.GallicWeight.createEmptyLabels(10.0), pair.getLeft());
    assertEquals(GallicSemiring.GallicWeight.createEmptyLabels(TropicalSemiring.INSTANCE.one()), pair.getRight());
  }

  @Test
  public void testFactorizeOneLabel() {
    Pair<GallicSemiring.GallicWeight, GallicSemiring.GallicWeight> pair = min.factorize(
        GallicSemiring.GallicWeight.createFromGiven(10.0, 11));
    assertEquals(GallicSemiring.GallicWeight.createFromGiven(10.0, 11), pair.getLeft());
    assertEquals(GallicSemiring.GallicWeight.createEmptyLabels(tropicalOne), pair.getRight());
  }

  @Test
  public void testFactorizeTwoLabels() {
    Pair<GallicSemiring.GallicWeight, GallicSemiring.GallicWeight> pair = min.factorize(
        GallicSemiring.GallicWeight.createFromGiven(10.0, 11, 12));
    assertEquals(GallicSemiring.GallicWeight.createFromGiven(10.0, 11), pair.getLeft());
    assertEquals(GallicSemiring.GallicWeight.createFromGiven(tropicalOne, 12), pair.getRight());
  }

  @Test
  public void testFactorizeThreeLabels() {
    Pair<GallicSemiring.GallicWeight, GallicSemiring.GallicWeight> pair = min.factorize(
        GallicSemiring.GallicWeight.createFromGiven(10.0, 11, 12, 13));
    assertEquals(GallicSemiring.GallicWeight.createFromGiven(10.0, 11), pair.getLeft());
    assertEquals(GallicSemiring.GallicWeight.createFromGiven(tropicalOne, 12, 13), pair.getRight());
  }
}
