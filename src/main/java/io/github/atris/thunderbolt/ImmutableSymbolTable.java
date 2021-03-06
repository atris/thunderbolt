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

package io.github.atris.thunderbolt;

/**
 * Immutable version of a symbol table; this in particular is effectively immutable because no one
 * can change it and it is safely published
 * NOTE: All Immutable* classes are thread safe
 * @author Atri Sharma
 */
public class ImmutableSymbolTable extends AbstractSymbolTable {

  public ImmutableSymbolTable(SymbolTable table) {
    super(table);
  }
}
