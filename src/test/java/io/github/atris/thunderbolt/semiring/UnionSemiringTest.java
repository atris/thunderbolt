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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnionSemiringTest {
  private WeightGenerator<UnionSemiring.UnionWeight<Double>> weightGen;
  private UnionSemiring<Double, PrimitiveSemiringAdapter> ring;
  private GallicSemiring gallicRing;
  private TropicalSemiring weightRing;
  private UnionSemiring<GallicSemiring.GallicWeight, GallicSemiring> gallicUnionRing;
  private UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> zero;

  @Before
  public void setUp() throws Exception {
    weightGen = WeightGenerator.makeUnion(0xEEF123, false);
    weightRing = TropicalSemiring.INSTANCE;
    gallicRing = new GallicSemiring(weightRing, GallicSemiring.GallicMode.RESTRICT_GALLIC);
    ring = UnionSemiring.makeForNaturalOrdering(new PrimitiveSemiringAdapter(TropicalSemiring.INSTANCE));
    UnionSemiring.MergeStrategy<GallicSemiring.GallicWeight> merge = (a, b) -> {
      Preconditions.checkArgument(a.getLabels().equals(b.getLabels()), "cant merge different labels");
      return GallicSemiring.GallicWeight.create(a.getLabels(), weightRing.plus(a.getWeight(), b.getWeight()));
    };
    gallicUnionRing = UnionSemiring.makeForOrdering(gallicRing, GallicSemiring.SHORTLEX_ORDERING, merge);
    zero = gallicUnionRing.zero();
  }

  @Test
  public void testUnionSemiring() {
    SemiringTester<UnionSemiring.UnionWeight<Double>> tester = new SemiringTester<>(weightGen);
    tester.setRandValuesToTest(200);
    tester.assertSemiring(ring);
  }

  @Test
  public void testUnionDivide() {
    // union divide is restricted to only single element unionweights
    SemiringTester<UnionSemiring.UnionWeight<Double>> tester = new SemiringTester<>(WeightGenerator.makeUnion(0x123BDD, true));
    tester.setRandValuesToTest(200);
    tester.assertDivide(ring);
  }

  @Test
  public void testWithAppend() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(43.0, 10, 12);
    GallicSemiring.GallicWeight gw3 = GallicSemiring.GallicWeight.createFromGiven(20.0, 10, 12);
    // append to empty
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> r1 = gallicUnionRing.withAppended(zero, gw1);
    assertEquals(ImmutableList.of(gw1), r1.getWeights());
    // append to one, no merge
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> r2 = gallicUnionRing.withAppended(r1, gw2);
    assertEquals(ImmutableList.of(gw1, gw2), r2.getWeights());
    // append to two, with merge
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> r3 = gallicUnionRing.withAppended(r1, gw3);
    assertEquals(ImmutableList.of(gw1, GallicSemiring.GallicWeight.createFromGiven(20.0, 10, 12)), r3.getWeights());
  }

  @Test(expected = IllegalStateException.class)
  public void testWithAppendFailsOutOfOrder() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(43.0, 10);
    // ooo should throw
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight>
        result = gallicUnionRing.withAppended(UnionSemiring.UnionWeight.createSingle(gw1), gw2);
  }

  @Test
  public void testPlus1() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(43.0, 10);
    GallicSemiring.GallicWeight gw1b = GallicSemiring.GallicWeight.createFromGiven(13.0, 10);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    GallicSemiring.GallicWeight gw3 = GallicSemiring.GallicWeight.createFromGiven(43.0, 10, 12);
    GallicSemiring.GallicWeight gw3b = GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 12);
    GallicSemiring.GallicWeight gw4 = GallicSemiring.GallicWeight.createFromGiven(43.0, 13, 1);
    GallicSemiring.GallicWeight gw5 = GallicSemiring.GallicWeight.createFromGiven(43.0, 14, 1);
    assertPlus(zero, UnionSemiring.UnionWeight.createSingle(gw1), gw1);

    // both sides
    assertPlus(UnionSemiring.UnionWeight.createSingle(gw1), UnionSemiring.UnionWeight.createSingle(gw2), gw1, gw2);
    assertPlus(UnionSemiring.UnionWeight.createFromGiven(gw1, gw3), UnionSemiring.UnionWeight.createSingle(gw2), gw1, gw2, gw3);
    assertPlus(UnionSemiring.UnionWeight.createFromGiven(gw1, gw3), UnionSemiring.UnionWeight.createFromGiven(gw2, gw4), gw1, gw2, gw3, gw4);
    assertPlus(UnionSemiring.UnionWeight.createFromGiven(gw1, gw2), UnionSemiring.UnionWeight.createFromGiven(gw3, gw4), gw1, gw2, gw3, gw4);

    // with merge
    assertPlus(UnionSemiring.UnionWeight.createFromGiven(gw1, gw3, gw4), UnionSemiring.UnionWeight
            .createFromGiven(gw2, gw3b, gw5),
      gw1, gw2, gw3b, gw4, gw5);
    assertPlus(UnionSemiring.UnionWeight.createFromGiven(gw1b, gw3), UnionSemiring.UnionWeight.createFromGiven(gw1, gw3b), gw1b, gw3b);
  }

  @Test
  public void testTimes1() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(43.0, 9);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(13.0, 10);
    GallicSemiring.GallicWeight gw3 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    // times concats left to right in the gallic... so its not commutative
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(56.0, 9, 10)),
      gallicUnionRing.times(UnionSemiring.UnionWeight.createFromGiven(gw1), UnionSemiring.UnionWeight.createFromGiven(gw2)).getWeights());
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(56.0, 10, 9)),
      gallicUnionRing.times(UnionSemiring.UnionWeight.createFromGiven(gw2), UnionSemiring.UnionWeight.createFromGiven(gw1)).getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(85.0, 9, 10, 11),
      GallicSemiring.GallicWeight.createFromGiven(55.0, 10, 10, 11)),
      gallicUnionRing.times(UnionSemiring.UnionWeight.createFromGiven(gw1, gw2), UnionSemiring.UnionWeight.createFromGiven(gw3)).getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(85.0, 10, 11, 9),
      GallicSemiring.GallicWeight.createFromGiven(55.0, 10, 11, 10)),
      gallicUnionRing.times(
          UnionSemiring.UnionWeight.createFromGiven(gw3), UnionSemiring.UnionWeight.createFromGiven(gw1, gw2)).getWeights());
  }

  @Test
  public void testDivideLeftSingular() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(43.0, 9);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(13.0, 10);
    GallicSemiring.GallicWeight gw3 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    GallicSemiring.GallicWeight gw4 = GallicSemiring.GallicWeight.createFromGiven(50.0, 10, 12, 13);
    // divide is only defined for union semiring when one side is a single valued union
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(30.0)),
      gallicUnionRing.divide(UnionSemiring.UnionWeight.createFromGiven(gw1), UnionSemiring.UnionWeight.createFromGiven(gw2)).getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(29.0, 11)),
      gallicUnionRing.divide(UnionSemiring.UnionWeight.createFromGiven(gw3), UnionSemiring.UnionWeight.createFromGiven(gw2)).getWeights());

    assertEquals(ImmutableList.of(
        GallicSemiring.GallicWeight.createFromGiven(8.0, 13), GallicSemiring.GallicWeight.createFromGiven(7.0, 12, 13)),
      gallicUnionRing.divide(
          UnionSemiring.UnionWeight.createFromGiven(gw4), UnionSemiring.UnionWeight.createFromGiven(gw1, gw2, gw3))
        .getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(-7.0)),
      gallicUnionRing.divide(
          UnionSemiring.UnionWeight.createFromGiven(gw1), UnionSemiring.UnionWeight.createFromGiven(gw2, gw3, gw4))
        .getWeights());
  }

  @Test
  public void testDivideRightSingular() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(43.0, 9);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(13.0, 10);
    GallicSemiring.GallicWeight gw3 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    GallicSemiring.GallicWeight gw4 = GallicSemiring.GallicWeight.createFromGiven(50.0, 10, 12, 13);
    // divide is only defined for union semiring when one side is a single valued union
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(-30.0)),
      gallicUnionRing.divide(UnionSemiring.UnionWeight.createFromGiven(gw2), UnionSemiring.UnionWeight.createFromGiven(gw1)).getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(-29.0)),
      gallicUnionRing.divide(UnionSemiring.UnionWeight.createFromGiven(gw2), UnionSemiring.UnionWeight.createFromGiven(gw3)).getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(-37.0)),
      gallicUnionRing.divide(UnionSemiring.UnionWeight.createFromGiven(gw1, gw2, gw3), UnionSemiring.UnionWeight.createFromGiven(gw4))
        .getWeights());

    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(-30.0),
      GallicSemiring.GallicWeight.createFromGiven(-1.0, 11),
      GallicSemiring.GallicWeight.createFromGiven(7.0, 12, 13)
    ), gallicUnionRing.divide(UnionSemiring.UnionWeight.createFromGiven(gw2, gw3, gw4), UnionSemiring.UnionWeight.createFromGiven(gw1))
      .getWeights());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDivideFailsMultiple() {
    GallicSemiring.GallicWeight gw1 = GallicSemiring.GallicWeight.createFromGiven(43.0, 9);
    GallicSemiring.GallicWeight gw2 = GallicSemiring.GallicWeight.createFromGiven(13.0, 10);
    GallicSemiring.GallicWeight gw3 = GallicSemiring.GallicWeight.createFromGiven(42.0, 10, 11);
    GallicSemiring.GallicWeight gw4 = GallicSemiring.GallicWeight.createFromGiven(50.0, 10, 12, 13);
    gallicUnionRing.divide(
        UnionSemiring.UnionWeight.createFromGiven(gw1, gw2), UnionSemiring.UnionWeight.createFromGiven(gw3, gw4));
  }

  @Test
  public void testDivideZero() {
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight>
        uw1 = UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(43.0, 9));
    assertEquals(zero, gallicUnionRing.divide(uw1, zero));
    assertEquals(zero, gallicUnionRing.divide(zero, uw1));
  }

  @Test
  public void testCommonDivisor1() {
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result = gallicUnionRing.commonDivisor(
        UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0)), result.getWeights());

    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result2 = gallicUnionRing.commonDivisor(
        UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0)), result2.getWeights());
  }

  @Test
  public void testCommonDivisor2() {
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0, 10)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0, 10)), result.getWeights());

    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result2 = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0, 10)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0, 10)), result2.getWeights());

    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result3 = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0, 11)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0)), result3.getWeights());
  }

  @Test
  public void testCommonDivisor3() {
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11, 12)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0, 10)), result.getWeights());

    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result2 = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11, 12)),
      UnionSemiring.UnionWeight.createSingle(GallicSemiring.GallicWeight.createFromGiven(10.0, 11, 12)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(10.0)), result2.getWeights());
  }

  @Test
  public void testCommonDivisor4() {
    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createFromGiven(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11, 12), GallicSemiring.GallicWeight
          .createFromGiven(9.0, 10)),
      UnionSemiring.UnionWeight.createFromGiven(GallicSemiring.GallicWeight.createFromGiven(10.0, 10, 11), GallicSemiring.GallicWeight
          .createFromGiven(11.0, 10, 11, 13)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(9.0, 10)), result.getWeights());

    UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> result2 = gallicUnionRing.commonDivisor(
      UnionSemiring.UnionWeight.createFromGiven(GallicSemiring.GallicWeight.createFromGiven(22.0, 10, 11, 12), GallicSemiring.GallicWeight
          .createFromGiven(9.0, 10)),
      UnionSemiring.UnionWeight.createFromGiven(
          GallicSemiring.GallicWeight.createFromGiven(7.0, 13), GallicSemiring.GallicWeight
              .createFromGiven(11.0, 10, 11, 13)));
    assertEquals(ImmutableList.of(GallicSemiring.GallicWeight.createFromGiven(7.0)), result2.getWeights());
  }

  private void assertPlus(
      UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> a, UnionSemiring.UnionWeight<GallicSemiring.GallicWeight> b, GallicSemiring.GallicWeight... expected) {
    ImmutableList<GallicSemiring.GallicWeight> expectedList = ImmutableList.copyOf(expected);
    assertEquals(expectedList, gallicUnionRing.plus(a, b).getWeights());
    assertEquals(expectedList, gallicUnionRing.plus(b, a).getWeights());
  }
}
