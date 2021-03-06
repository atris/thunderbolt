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

import io.github.atris.thunderbolt.Arc;
import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

/**
 * Comparator used in {@link ArcSort} for sorting based on output labels
 */
public class OLabelCompare implements Comparator<Arc> {

  public static final OLabelCompare INSTANCE = new OLabelCompare();

  @Override
  public int compare(Arc o1, Arc o2) {
    if (o1 == null) {
      return 1;
    }
    if (o2 == null) {
      return -1;
    }
    return ComparisonChain.start()
            .compare(o1.getOlabel(), o2.getOlabel())
            .compare(o1.getIlabel(), o2.getIlabel())
            .compare(o1.getWeight(), o2.getWeight())
            .compare(o1.getNextState().getId(), o2.getNextState().getId())
            .result();
  }

}
