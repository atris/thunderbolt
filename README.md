# thunderbolt

Thunderbolt is an implementation/port of the OpenFST forum.

## Quick Start
The basic abstractions of Fst, State, Arc, and SymbolTable have conceptual analogs in OpenFST.

In Thunderbolt, there are Mutable and Immutable implementations of each. As you programmatically build up your WFSTs, you will use the Mutable API. If you want to de/serialize larger models (large WFSTs built from training data that are used to construct lattices) and these models don't need to change, then you can convert the mutable instance into an immutable instance after you are done building it (new ImmutableFst(myMutableFst). ImmutableFsts are likely faster at some operations and also are smarter about reducing unnecessary copying of state.

The MutableFst API is probably the bast place to start. Here is a sample showing how to construct a WFST which shows the basic operations of fsts, states, arcs, and symbols.

MutableFst fst = new MutableFst(TropicalSemiring.INSTANCE);
// by default states are only identified by indexes assigned by the FST, if you want to instead
// identify your states with symbols (and read/write a state symbol table) then call this before
// adding any states to the FST
fst.useStateSymbols();
MutableState startState = fst.newStartState("<start>");
// setting a final weight makes this state an eligible final state
fst.newState("</s>").setFinalWeight(0.0);

// you can add symbols manually to the symbol table
int symbolId = fst.getInputSymbols().getOrAdd("<eps>");
fst.getOutputSymbols().getOrAdd("<eps>");

// add arcs on the MutableState instances directly or using convenience methods on the fst instance
// if using state labels you can pass the labels (if they dont exist, new states will be created)
// params are inputSatate, inputLabel, outputLabel, outputState, arcWeight
fst.addArc("state1", "inA", "outA", "state2", 1.0);

// alternatively (or if no state symbols) you can use the state instances
fst.addArc(startState, "inC", "outD", fst.getOrNewState("state3"), 123.0);
Input and Output
Thunderbolt supports reading/writing the OpenFst text format and our own Thunderbolt binary serialization format (more compact than text). We cannot currently read/write OpenFSTs binary serialization format.

To read/write the text format call methods Convert.importFst(..) and Convert.export(..). Both of these return instances of MutableFst which can be converted into ImmutableFst via new ImmutableFst(myMutableFst).
There are importFst overloads for dealing with either Files or resources from the classpath.

To read/write the binary format call methods FstInputOutput.readFstFromBinaryFile and FstInputOutput.writeFstToBinaryFile (there are overloads for dealing with streams/resources.
Resources are useful if you want to package your serialized model in your jar and just read it from the classpath.
