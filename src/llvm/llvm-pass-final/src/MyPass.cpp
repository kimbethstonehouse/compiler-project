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

#include <set>
#include <map>
#include <algorithm>

using namespace llvm;
using namespace std;

namespace {
  map<Value*, set<Value*>> in;
  map<Value*, set<Value*>> out;
  map<Value*, set<Value*>> prevIn;
  map<Value*, set<Value*>> prevOut;
  // map<Value*, set<Value*>> in;
  // map<Value*, set<Value*>> out;
  // map<Value*, set<Value*>> prevIn;
  // map<Value*, set<Value*>> prevOut;

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
    return !I->isTerminator() && !I->mayHaveSideEffects() && !isa<CallInst>(I);
    // TODO: isa call instr? think isTerminator is true for this
  }

bool computeSets(Function &F) {
  do {
    prevIn = in;
    prevOut = out;

    for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; I++) {
      
      set<Value*> uses;
      set<Value*> defs;
      set<Value*> successors;

      Instruction* inst = &*I;

      // prevIn[inst] = in[inst];
      // prevOut[inst] = out[inst];

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
      set<Value*> result;
      set_difference(out[inst].begin(), out[inst].end(), defs.begin(), defs.end(), inserter(result, result.begin()));
      set<Value*> result2;
      set_union(uses.begin(), uses.end(), result.begin(), result.end(), inserter(result2, result2.begin()));
      in[inst] = result2;

      if (inst->isTerminator()) {
        // iterate over all basic blocks that the instruction can branch to
        for (int i = 0; i < I->getNumSuccessors(); i++) {
          BasicBlock* basicBlockSuccessor = I->getSuccessor(i); 
          auto instructionSuccessor = &*(basicBlockSuccessor->begin());
          successors.insert(instructionSuccessor);
          // todo: what if this is a phi node?
        }
      } else {
          auto instructionSuccessor = I;
          ++instructionSuccessor;
          successors.insert(&*instructionSuccessor);
      }

      // out[n] = union over all successors s of in[s]
      set<Value*> result3;
      for (Value* successor : successors) {
        set<Value*> temp;
        set_union(result3.begin(), result3.end(), in[successor].begin(), 
            in[successor].end(), inserter(temp, temp.begin()));
        result3 = temp;
      }

      out[inst] = result3;
    }
  } while (!(prevIn == in && prevOut == out));
}

// void printLiveness(Function &F) {
//   for (Function::iterator bb = F.begin(), end = F.end(); bb != end; bb++) {
//     for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; i++) {
//       // skip phis
//       if (dyn_cast<PHINode>(i))
//         continue;
      
//       errs() << "{";
      
//       auto operatorSet = in[&*i];
//       for (auto oper = operatorSet.begin(); oper != operatorSet.end(); oper++) {
//         auto op = *oper;
//         if (oper != operatorSet.begin())
//           errs() << ", ";
//         (*oper)->printAsOperand(errs(), false);
//       }
      
//       errs() << "}\n";
//     }
//   }
//   errs() << "{}\n";
// }

bool removeDeadInstructions(Function &F) {
  bool cutInstruction = false;
  SmallVector<Instruction*, 64> Worklist;

  
  
  computeSets(F);

  // printLiveness(F);

  for (Function::iterator bb = F.begin(), end = F.end(); bb != end; bb++) {
    for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; i++) {
      // skip phis
      if (dyn_cast<PHINode>(i))
        continue;
      
      errs() << "{";
      
      auto operatorSet = out[&*i];
      for (auto oper = operatorSet.begin(); oper != operatorSet.end(); oper++) {
        auto op = *oper;
        if (oper != operatorSet.begin())
          errs() << ", ";
        (*oper)->printAsOperand(errs(), false);
      }
      
      errs() << "}\n";
    }
  }
  errs() << "{}\n";

  // calculate dead instructions
  for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
    Instruction* inst = &*I; 

    if (out[inst].find(inst) == out[inst].end() && safeToRemove(inst)) {
      errs() << "found dead instruction\n";
      // store dead instructions for later elimination
      Worklist.push_back(inst);
      cutInstruction = true;
    }
  }

  // eliminate the dead instructions
  while (!Worklist.empty()) {
    Instruction* inst = Worklist.pop_back_val();
    errs() << "removed dead instruction\n";
    inst->eraseFromParent();
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

    return true;
  }
};
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My liveness analysis and dead code elimination pass");

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });

