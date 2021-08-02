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
package io.github.atris.thunderbolt;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FrozenSymbolTableTest {

  @Test
  public void shouldGetExisting() throws Exception {
    MutableSymbolTable table = new MutableSymbolTable();
    int a = table.getOrAdd("A");
    FrozenSymbolTable frozen = new FrozenSymbolTable(table);
    assertEquals(a, frozen.get("A"));
    assertEquals(a, frozen.getOrAdd("A"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailOnAdd() throws Exception {
    MutableSymbolTable table = new MutableSymbolTable();
    int a = table.getOrAdd("A");
    FrozenSymbolTable frozen = new FrozenSymbolTable(table);
    assertEquals(a, frozen.get("A"));
    frozen.getOrAdd("B");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldFailOnPut() throws Exception {
    MutableSymbolTable table = new MutableSymbolTable();
    int a = table.getOrAdd("A");
    FrozenSymbolTable frozen = new FrozenSymbolTable(table);
    assertEquals(a, frozen.get("A"));
    frozen.put("B", 42);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFreezeViaFactoryMethod() throws Exception {
    MutableSymbolTable table = new MutableSymbolTable();
    int a = table.getOrAdd("A");
    FrozenSymbolTable frozen = FrozenSymbolTable.freeze(table);
    assertEquals(a, frozen.get("A"));
    assertEquals(a, frozen.getOrAdd("NOTTHERE"));
  }
}
