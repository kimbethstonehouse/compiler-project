// Example of how to write an LLVM pass
// For more information see: http://llvm.org/docs/WritingAnLLVMPass.html
//#define DEBUG_TYPE "myPass"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
<<<<<<< HEAD
#include "llvm/IR/InstIterator.h"
#include "llvm/IR/Instructions.h"

#include "llvm/Support/raw_ostream.h"
=======
>>>>>>> 28d15e1b98dd25689b973fa9c5b35f948d45db0f
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Support/raw_ostream.h"
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

<<<<<<< HEAD
  bool removeDeadInstructions(Function &F) {
    bool cutInstruction = false;
    errs() << "Function " << F.getName() << "\n";
    SmallVector<Instruction*, 64> Worklist;

    

    for (inst_iterator I = inst_begin(F), E = inst_end(F); I!= E; ++I) {
        if (isInstructionTriviallyDead(&*I)) {
          // errs() << "found dead instruction\n";
          // store dead instructions for later elimination
          Worklist.push_back(&*I);
          cutInstruction = true;
        }
    }

    // eliminate the dead instructions
    while (!Worklist.empty()) {
      Instruction* I = Worklist.pop_back_val();
      // errs() << "removed dead instruction\n";
      I->eraseFromParent();
    }

    return cutInstruction;
  }

  virtual bool runOnFunction(Function &F) {
    bool instructionsRemoved = true; 

    while (instructionsRemoved) {
      instructionsRemoved = removeDeadInstructions(F);
    }

    return false;
=======
  bool runOnFunction(Function &F) override {
    errs() << "I saw a function called " << F.getName() << "!\n";
    return true;
>>>>>>> 28d15e1b98dd25689b973fa9c5b35f948d45db0f
  }
};
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My simple dead code elimination pass");
<<<<<<< HEAD

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });
=======
>>>>>>> 28d15e1b98dd25689b973fa9c5b35f948d45db0f
