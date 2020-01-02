// Example of how to write an LLVM pass
// For more information see: http://llvm.org/docs/WritingAnLLVMPass.html
//#define DEBUG_TYPE "myPass"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"

#include <vector>
#include <map>

using namespace llvm;
using namespace std;

namespace {
struct MyPass : public FunctionPass {
  map<string, int> opCounter;
  static char ID;
  MyPass() : FunctionPass(ID) {}
  virtual bool runOnFunction(Function &F) {
    errs() << "Function " << F.getName() << "\n";

    for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
      for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
        if(opCounter.find(i->getOpcodeName()) == opCounter.end()) {
          opCounter[i->getOpcodeName()] = 1;
        } else {
          opCounter[i->getOpcodeName()]++;
        }
      }
    }

    map <string, int>::iterator i = opCounter.begin();
    map <string, int>::iterator e = opCounter.end();
    while (i != e) {
      errs() << i->first << ": " << i->second << "\n";
      i++;
    } 
    errs() << "\n";
    opCounter.clear();
    return false;
  }
};
}

char MyPass::ID = 0;
static RegisterPass<MyPass> X("mypass", "My simple dead code elimination pass");

static RegisterStandardPasses Y(
    PassManagerBuilder::EP_EarlyAsPossible,
    [](const PassManagerBuilder &Builder,
       legacy::PassManagerBase &PM) { PM.add(new MyPass()); });