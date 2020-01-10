// Example of how to write an LLVM pass
// For more information see: http://llvm.org/docs/WritingAnLLVMPass.html
//#define DEBUG_TYPE "myPass"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/InstIterator.h"
#include "llvm/IR/Instructions.h"

#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"
#include "llvm/Transforms/Utils/Local.h"

#include <vector>
#include <map>

using namespace llvm;
using namespace std;

namespace {
  struct MyPass : public FunctionPass {
    static char ID;
    MyPass() : FunctionPass(ID) {}

    bool removeDeadInstructions(Function &F) {
      bool cutInstruction = false;
      errs() << "Function " << F.getName() << "\n";
      SmallVector<Instruction*, 64> Worklist;

      for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
          if (isInstructionTriviallyDead(&*I)) {
            // store dead instructions for later elimination
            Worklist.push_back(&*I);
            cutInstruction = true;
          }
      }

      // eliminate the dead instructions
      while (!Worklist.empty()) {
        Instruction* I = Worklist.pop_back_val();
        I->eraseFromParent();
      }

      return cutInstruction;
    }

    virtual bool runOnFunction(Function &F) {
      bool instructionsRemoved = true; 

      while (instructionsRemoved) {
        instructionsRemoved = removeDeadInstructions(F);
      }

      return true;
    }
  };
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My simple dead code elimination pass");

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });