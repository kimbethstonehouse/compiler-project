// Example of how to write an LLVM pass
// For more information see: http://llvm.org/docs/WritingAnLLVMPass.html

#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/InstIterator.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/BasicBlock.h"

#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"
#include "llvm/Transforms/Utils/Local.h"

#include <set>
#include <map>

using namespace llvm;
using namespace std;

namespace {
  map<Value*, set<Value*>> in;
  map<Value*, set<Value*>> out;
  map<Value*, set<Value*>> prevIn;
  map<Value*, set<Value*>> prevOut;

  // TODO: complete
  // TODO: take the instruction as an argument, 
  // return false for instructions that should not be removed
  // like ones with side effects
  // alternatively, have a helper function safeToRemove

  // bool myIsInstructionTriviallyDead(Instruction* I) {
  //   bool truth = out[I].find(I) == out[I].end();
  //   errs() << truth << "\n";
  //   // errs() << out[I] << "\n";
  //   for (auto i = out[I].begin(); i != out[I].end(); ++i) {
  //     errs() << *i << "\n";
  //   }
  //   return out[I].find(I) == out[I].end();
  // }

  bool safeToRemove(Instruction* I) {
    return !I->isTerminator() && !I->mayHaveSideEffects();
    // TODO: isa call instr? think isTerminator is true for this
  }

bool computeSets(Function &F) {
  // for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
  //   instruction* inst = &*I; 
    
  //   for (op_iterator ob = I->op_begin(), oe = I->op_end(), ob++) {

  //   }
  // } 

  // TODO: working backwards through the instructions, compute the sets
    // maps from each instruction to the in-set for that instruction
  
  set<Value*> emptySet;

  do {
    prevIn = in;
    prevOut = out;

    for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
      Instruction* inst = &*I;
      in[inst] = emptySet;
      out[inst] = emptySet;
    }

    for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
      set<Value*> uses;
      set<Value*> defs;
      set<Value*> successors;
      Instruction* inst = &*I;

      // calculate uses (gen) set
      for (int i = 0; i < inst->getNumOperands(); i++) {
        Value* operand = I->getOperand(i); 
        if (isa<Instruction>(operand) || isa<Argument>(operand)) {
          uses.insert(operand);
        }
      }

      // calculate defs (kill) set
      defs.insert(&*I);
      
      // in[n] = uses[n] U (out[n] - defs[n])
      set<Value*> temp;
      set_difference(out[inst].begin(), out[inst].end(), defs.begin(), defs.end(), inserter(temp, temp.begin()));
      set_union(uses.begin(), uses.end(), temp.begin(), temp.end(), inserter(in[inst], in[inst].begin()));

      // todo: this assumes that for all instructions other
      // than a branch, the successsor is the next instruction
      if (isa<BranchInst>(*I)) {
        // iterate over all basic blocks that the instruction can branch to
        for (int i = 0; i < I->getNumSuccessors(); i++) {
          BasicBlock* basicBlockSuccessor = I->getSuccessor(i); 
          auto instructionSuccessor = &*(basicBlockSuccessor->begin());
          successors.insert(instructionSuccessor);
          // todo: what if this is a phi node?
        }
      } else {
          // check that the instruction actually has a successor
          auto instructionSuccessor = I;
          ++instructionSuccessor;
          if (&*instructionSuccessor <= &*E) { // todo: is this right?
            successors.insert(&*instructionSuccessor);
          }
      }

      // out[n] = union over all successors s of in[s]
      set<Value*> newOutSet;
      for (Value* successor : successors) {
        set<Value*> temp;
        set_union(newOutSet.begin(), newOutSet.end(), in[successor].begin(), 
            in[successor].end(), inserter(temp, temp.begin()));
        newOutSet = temp;
      }

      out[inst] = newOutSet;
    }
  } while (prevIn != in || prevOut != out);
}

bool removeDeadInstructions(Function &F) {
    bool cutInstruction = false;
    errs() << "Function1 " << F.getName() << "\n";
    SmallVector<Instruction*, 64> Worklist;


    // 
    computeSets(F);

  //   set<Value*> emptySet;

  // for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
  //     Instruction* inst = &*I;
  //     in[inst] = emptySet;
  //     out[inst] = emptySet;
  // }

  // do {
  //   // prevIn = in;
  //   // prevOut = out;

  //   for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
  //     set<Value*> uses;
  //     set<Value*> defs;
  //     set<Value*> successors;
  //     Instruction* inst = &*I;

  //     prevIn[inst] = in[inst];
  //     prevOut[inst] = out[inst];

  //     // calculate uses (gen) set
  //     for (int i = 0; i < inst->getNumOperands(); i++) {
  //       Value* operand = I->getOperand(i); 
  //       if (isa<Instruction>(operand) || isa<Argument>(operand)) {
  //         uses.insert(operand);
  //       }
  //     }

  //     // calculate defs (kill) set
  //     defs.insert(&*I);
      
  //     // in[n] = uses[n] U (out[n] - defs[n])
  //     set<Value*> temp;
  //     set<Value*> temp2;
  //     // set_difference(out[inst].begin(), out[inst].end(), defs.begin(), defs.end(), temp.begin());
  //     out[inst].erase(&*I);
  //     set_union(uses.begin(), uses.end(), temp.begin(), temp.end(), inserter(temp2, temp2.begin()));
  //     in[inst] = temp2;

  //     // todo: this assumes that for all instructions other
  //     // than a branch, the successsor is the next instruction
  //     if (isa<BranchInst>(*I)) {
  //       // iterate over all basic blocks that the instruction can branch to
  //       for (int i = 0; i < I->getNumSuccessors(); i++) {
  //         BasicBlock* basicBlockSuccessor = I->getSuccessor(i); 
  //         auto instructionSuccessor = &*(basicBlockSuccessor->begin());
  //         successors.insert(instructionSuccessor);
  //         // todo: what if this is a phi node?
  //       }
  //     } else {
  //         // check that the instruction actually has a successor
  //         auto instructionSuccessor = I;
  //         ++instructionSuccessor;
  //         if (&*instructionSuccessor <= &*E) { // todo: is this right?
  //           successors.insert(&*instructionSuccessor);
  //         }
  //     }

  //     // out[n] = union over all successors s of in[s]
  //     set<Value*> newOutSet;
  //     for (Value* successor : successors) {
  //       set<Value*> temp;
  //       set_union(newOutSet.begin(), newOutSet.end(), in[successor].begin(), 
  //           in[successor].end(), inserter(temp, temp.begin()));
  //       newOutSet = temp;
  //     }

  //     out[inst] = newOutSet;
  //   }
  // } while (prevIn != in || prevOut != out);

    // calculate dead instructions
    for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
      Instruction* inst = &*I; 

      if ((out[inst].find(inst) == out[inst].end()) && safeToRemove(inst)) {
        errs() << "found dead instruction\n";
        // store dead instructions for later elimination
        Worklist.push_back(inst);
        cutInstruction = true;
      }
    }

    // eliminate the dead instructions
    while (!Worklist.empty()) {
      Instruction* I = Worklist.pop_back_val();
      errs() << "removed dead instruction\n";
      I->eraseFromParent();
    }

    return cutInstruction;
  }


struct MyPass : public FunctionPass {
  static char ID;
  MyPass() : FunctionPass(ID) {}

  bool runOnFunction(Function &F) override {
    bool instructionsRemoved = false; 

    do {
      instructionsRemoved = removeDeadInstructions(F);
    } while (instructionsRemoved);

    return false;
  }
};
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My liveness analysis and dead code elimination pass");

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });

