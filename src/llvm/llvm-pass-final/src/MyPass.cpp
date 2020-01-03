// Example of how to write an LLVM pass
// For more information see: http://llvm.org/docs/WritingAnLLVMPass.html

#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"

using namespace llvm;

namespace {
struct MyPass : public FunctionPass {
  static char ID;
  MyPass() : FunctionPass(ID) {}

  bool runOnFunction(Function &F) override {
    printLiveness(F);
    return true;
  }
  
  void printLiveness(Function &F) {
    for (Function::iterator bb = F.begin(), end = F.end(); bb != end; bb++) {
      for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; i++) {
        // skip phis
        if (dyn_cast<PHINode>(i))
          continue;
        
        errs() << "{";
        
        /* UNCOMMENT AND ADAPT FOR YOUR "IN" SET
        auto operatorSet = in[&*i];
        for (auto oper = operatorSet.begin(); oper != operatorSet.end(); oper++) {
          auto op = *oper;
          if (oper != operatorSet.begin())
            errs() << ", ";
          (*oper)->printAsOperand(errs(), false);
        }
        */
        
        errs() << "}\n";
      }
    }
    errs() << "{}\n";
  }
  
};
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My liveness analysis and dead code elimination pass");
