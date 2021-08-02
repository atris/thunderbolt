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
import io.github.atris.thunderbolt.Arc;
import io.github.atris.thunderbolt.Fst;
import io.github.atris.thunderbolt.MutableState;
import io.github.atris.thunderbolt.State;
import io.github.atris.thunderbolt.semiring.Semiring;


/**
 * Reverse operation.
 *
 * @author John Salatas jsalatas@users.sourceforge.net
 */
public class Reverse {

  /**
   * Reverses an fst
   *
   * @param infst the fst to reverse
   * @return the reversed fst
   */
  public static MutableFst reverse(Fst infst) {
    infst.throwIfInvalid();

    MutableFst fst = ExtendFinal.apply(infst);

    Semiring semiring = fst.getSemiring();

    MutableFst result = new MutableFst(fst.getStateCount(), semiring);
    result.setInputSymbolsAsCopy(fst.getInputSymbols());
    result.setOutputSymbolsAsCopy(fst.getOutputSymbols());
    MutableState[] stateMap = initStateMap(fst, semiring, result);

    for (int i = 0; i < fst.getStateCount(); i++) {
      State oldState = fst.getState(i);
      MutableState newState = stateMap[oldState.getId()];
      for (int j = 0; j < oldState.getArcCount(); j++) {
        Arc oldArc = oldState.getArc(j);
        MutableState newNextState = stateMap[oldArc.getNextState().getId()];
        double newWeight = semiring.reverse(oldArc.getWeight());
        result.addArc(newNextState, oldArc.getIlabel(), oldArc.getOlabel(), newState, newWeight);
      }
    }
    return result;
  }

  private static MutableState[] initStateMap(MutableFst fst, Semiring semiring, MutableFst result) {
    MutableState[] stateMap = new MutableState[fst.getStateCount()];
    for (int i = 0; i < fst.getStateCount(); i++) {
      State state = fst.getState(i);
      MutableState newState = result.newState();
      newState.setFinalWeight(semiring.zero());
      stateMap[state.getId()] = newState;
      if (semiring.isNotZero(state.getFinalWeight())) {
        result.setStart(newState);
      }
    }
    stateMap[fst.getStartState().getId()].setFinalWeight(semiring.one());
    return stateMap;
  }
}
