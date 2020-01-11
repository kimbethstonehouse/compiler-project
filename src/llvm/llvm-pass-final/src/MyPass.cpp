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
  map<Value*, set<Value*>> liveIn;
  map<Value*, set<Value*>> liveOut;
  map<Value*, set<Value*>> prevIn;
  map<Value*, set<Value*>> prevOut;

  bool safeToRemove(Instruction* I) {
    return !I->isTerminator() && !I->mayHaveSideEffects() && !isa<CallInst>(I);
    // TODO: isa call instr? think isTerminator is true for this
  }

  bool myIsInstructionTriviallyDead(Instruction* I) {
    return liveOut[I].find(I) == liveOut[I].end() && safeToRemove(I);
  }

  bool computeSets(Function &F) {
    do {
      prevIn = liveIn;
      prevOut = liveOut;

      for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; I++) {
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
        set<Value*> result;
        set_difference(liveOut[inst].begin(), liveOut[inst].end(), defs.begin(), defs.end(), inserter(result, result.begin()));
        set_union(uses.begin(), uses.end(), result.begin(), result.end(), inserter(liveIn[inst], liveIn[inst].begin()));

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
          set_union(result3.begin(), result3.end(), liveIn[successor].begin(), 
              liveIn[successor].end(), inserter(temp, temp.begin()));
          result3 = temp;
        }

        liveOut[inst] = result3;
      }
    } while (!(prevIn == liveIn && prevOut == liveOut));
  }

  void printLiveness(Function &F) {
    for (Function::iterator bb = F.begin(), end = F.end(); bb != end; bb++) {
      for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; i++) {
        // skip phis
        if (dyn_cast<PHINode>(i))
          continue;
        
        errs() << "{";
        
        auto operatorSet = liveIn[&*i];
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
  }

  bool removeDeadInstructions(Function &F) {
    bool cutInstruction = false;
    SmallVector<Instruction*, 64> Worklist;
    
    computeSets(F);

    // calculate dead instructions
    for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
      Instruction* inst = &*I; 

      if (myIsInstructionTriviallyDead(inst)) {
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

      computeSets(F);
      printLiveness(F);

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