# Part IV : Introduction to LLVM
The goal of part IV is to write a simple LLVM pass.
If you have no previous experience with C++, we suggest that you take a look at these [C++ for Java programmers](https://www.cs.cmu.edu/afs/cs/academic/class/15494-s12/lectures/c++forjava.pdf) slides.

The project counts for 30% of your grade: 10% for writing a simple dead code elimination pass using the code provided, and 20% for implementing liveness analysis and writing your own method to determine dead code.

## 0. Setup

You will use Git to clone the LLVM sources and cmake to generate the Makefiles to build LLVM on Linux. To get started, clone the LLVM sources into your home directory (or location of your choice). If you clone the entire llvm-project git repo you need about 2GBs of free disk space. If you do a shallow clone (--depth option) it will take about 500MB. We will use 'ug3-ct' as the name of the directory to clone into.

```
cd ~
mkdir ug3-ct
cd ug3-ct
git clone --depth=100 --branch release/9.x https://github.com/llvm/llvm-project
```

You have been given an 50GB of space for this course. The Debug build of LLVM requires around 40GB of disk space! Be careful not to fill up your home directory. If you are using DICE use the 'RelWithDebInfo' cmake build type, which uses less space.

Create a directory called 'build' where you will build LLVM. This directory can be located anywhere EXCEPT under your LLVM source directory. We will place it under 'ug3-ct' in this document.

If you are using DICE, the correct version of Cmake is installed as 'cmake3'. If you are using your own machine you may need to install Cmake from http://cmake.org. After installation the name of the binary will be 'cmake'.

```
mkdir build
cd build
cmake3 -DLLVM_TARGETS_TO_BUILD=X86 -DLLVM_ENABLE_PROJECTS=clang -DCMAKE_BUILD_TYPE=RelWithDebInfo -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=true ../llvm-project/llvm
```

After Cmake finishes creating the Makefiles the next step is to actually build LLVM. This can take anywhere from 10 minutes to 1 hour depending on your machine. The '-j' option to make specifies the number of threads to use. A good rule of thumb is to use a number that is twice the number of cores in your computer (-j4 for a dual-core machine or -j8 for a quad core machine).

```
make -j4
```

After make finishes you will have a bin directory with the LLVM tools (clang, clang++, llc, etc). Try to compile a simple C example with Clang to LLVM bitcode to make sure it works.

```
echo "int main() { return 1; }" > test.c
~/ug3-ct/build/bin/clang -c -S -emit-llvm -Xclang -disable-O0-optnone test.c -o test.ll
cat test.ll
```

Your test.ll should look SOMETHING like this (this output is from Mac OS X, Linux and Windows will be slightly different).

```
; ModuleID = 'test.c'
source_filename = "test.c"
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-macosx10.12.0"

; Function Attrs: nounwind ssp uwtable
define i32 @main() #0 {
  %1 = alloca i32, align 4
  store i32 0, i32* %1, align 4
  ret i32 1
}

attributes #0 = { nounwind ssp uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"PIC Level", i32 2}
!1 = !{!"Apple LLVM version 8.0.0 (clang-800.0.42.1)"}
```

Congratulations you have just built LLVM!


## 1. Writing a LLVM Pass

You have LLVM and are able to compile C programs. The next step is to create a pass of your own. There is example code for an LLVM pass in the CT git repository on GitLab. If you have not done so already, clone the git repo as follows.

```
cd ~/ug3-ct
git clone https://git.ecdf.ed.ac.uk/cdubach/ct-19-20/
```

Change to the directory for the example pass and take a look at the source. It does nothing except print the name of whatever function it encounters.

```
cd ct-19-20/src/llvm-pass
cat src/MyPass.cpp
```

Let's build the pass now. Create a build directory and change to the directory.

```
mkdir build
cd build
```

Before running Cmake and building the pass, you need to set LLVM_DIR to your LLVM build directory, i.e. ~/ug3-ct/build. Otherwise when you build the pass it will try to build with the version of LLVM that
is already installed on DICE and fail.

```
export LLVM_DIR=~/ug3-ct/build
```

You're ready to create the Makefiles and build the pass.

```
cmake3 ..
make
```

There should be a shared library for your new pass in src/libMyPass.so. When you compile a program with LLVM it will load your pass and automatically call it. You'll need a C file to use as a test. You can use the test.c you created in Step 0 or create a new file.

```
~/ug3-ct/build/bin/clang -Xclang -load -Xclang src/libMyPass.so ~/ug3-ct/build/test.c
I saw a function called main!
```

Congratulations you've just created an LLVM pass and successfully executed it with your own LLVM!


## 2. Write a Pass to Count Instructions

Use the pass above and the [lecture notes from 
class](http://www.inf.ed.ac.uk/teaching/courses/ct/slides-16-17/llvm/4-lab3_intro.pdf) to write a simple pass to print the number of instructions in a function.

NOTE!! LLVM will run your pass on EACH function. You do not need to use any module iterators. Only function and basic block iterators.

The lecture notes are for an older version of LLVM and you need to make the following changes:

Slide 5: Include the C++ header for vector with the other includes and add namespace std so the compiler can find vector.

```
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include <vector>
using namespace llvm;
using namespace std;
```

For your project you only need the code from Slide 5. If you would like to try out the loop analysis code on Slide 17 you need to make the following change. This is completely optional for your project.

Slide 17: The getAnalysis() method to access the LoopInfo structure in runOnFunction() has changed. Here is the correct definition.

```
LoopInfo &LI = getAnalysis<LoopInfoWrapperPass>().getLoopInfo();
```

## 3. Implement a Simple Dead Code Elimination Pass

Add a new method to your instruction counting pass to eliminate dead code. In the C program below, 'd' is dead because it is not used after it's assignment in the program. The assignment to 'c' is dead 
because it's only use is in the assignment to 'd' which is dead.

```
int foo() {
  int a = 7;
  int b = a * 2;
  int c = b - a;   // dead 
  int d = c / a;   // dead
  return b;
}
```

LLVM has a method to detect dead code and a method to remove instructions that you can use in your pass.

```
isInstructionTriviallyDead()
eraseFromParent()
```

You will use the LLVM iterators we discussed in class to find the dead instructions. It is illegal to remove an instruction while you are iterating over them. You need to first identify the instructions that are dead and then in a second loop remove them. Use the LLVM SmallVector data structure to store the dead instructions you find while iterating and a second loop to remove them.

```
SmallVector<Instruction*, 64> Worklist;
```

You need to run LLVM's 'mem2reg' pass before your DCE pass to convert the bitcode into a form that will work with your optimization. Without running 'mem2reg' all instructions will store their destinations operands to the stack and load their source operands from the stack. The memory instructions will block the ability for you to discover dead code. When you run 'mem2reg', you are converting the stack allocated code in non-SSA form, into SSA form with virtual registers.

Use the 'opt' tool to run 'mem2reg' before your DCE pass. Give your pass a command line option called 'mypass'.

```
~/ug3-ct/build/bin/clang -S -emit-llvm -Xclang -disable-O0-optnone dead.c
~/ug3-ct/build/bin/opt -load src/libMyPass.so -mem2reg -mypass dead.ll
``` 

## 4. Implement Iterative Liveness Analysis

For the final part of your project you will replace the isInstructionTriviallyDead() method from LLVM with your own method to identify dead code. This relies on computing liveness which you learned about in class. Refer back to the [lecture slides](https://www.inf.ed.ac.uk/teaching/courses/ct/19-20/slides/llvm-5-liveness.pdf) for the dataflow equations you need to implement and examples.

Refer to the [Final project PDF](https://git.ecdf.ed.ac.uk/cdubach/ct-19-20/blob/master/desc/part4/ug3project.pdf) for more information.

## 5. Submitting Your Project

As with parts 1-3, part 4 will be marked with a set of automated scripts, but we won't be running scripts every day like with previous tests. 

You should modify the two passes stored in the  `src/llvm` directory and push your changes to gitlab. Do not change the cmake files or source file names in the folder or the marking script will fail!!

Here is the structure of the folders in git for the LLVM project:

```
src
`-- llvm
    |-- llvm-pass-simple
    |   |-- CMakeLists.txt
    |   `-- src
    |       |-- CMakeLists.txt
    |       `-- MyPass.cpp
    `-- llvm-pass-final
        |-- CMakeLists.txt
        `-- src
            |-- CMakeLists.txt
            `-- MyPass.cpp
```

You will modify:

- `llvm-pass-simple/src/MyPass.cpp` for your simple dead code elimination pass (LLVM part 3).
- `llvm-pass-final/src/MyPass.cpp` for your iterative liveness analysis and dead code elimination pass (LLVM part 4).

### Pass names

The skeleton code in git uses `-mypass` as the the command-line option for the two pases. Do not change the name of the command-line option or the marking scripts will not be able to call your pass.
