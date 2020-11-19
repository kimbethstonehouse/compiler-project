# Compiling Techniques 19-20 #

## Results

I was awarded a mark of 88% (A2) for this coursework. The scoreboard runs a series of hidden test programs on this compiler about twice a day. View my results [here](https://www.inf.ed.ac.uk/teaching/courses/ct/19-20/scoreboard/puppy_results.html).

## About

The aim of this assignment was to write a compiler for a subset of the C language, targeting the MIPS ISA. The compiler is written in Java, and focuses on the lexing, parsing, semantic analysis and code generation phases. It also involved writing an LLVM pass in C++ to perform liveness analysis and dead code elimination.

## Building the project
In order to build the project you must have Ant installed. On DICE machines Ant is already installed.
Your local copy of the ct-19-20 repository contains an Ant build file (`build.xml`).
If you are using an IDE, then you can import the build file.
Otherwise, you can build the project from the commandline by typing:
```
$ ant build
```
This command outputs your compiler in a directory called `bin` within the project structure. Thereafter, you can run your compiler from the commandline by typing:
```
$ java -cp bin Main
```
The parameter `cp` instructs the Java Runtime to include the local directory `bin` when it looks for class files.

You can find a series of tests in the `tests` folder. To run the lexer on one of them, you can type:

```
$ java -cp bin Main -lexer tests/fibonacci.c dummy.out
```


You can clean the `bin` directory by typing:
```
$ ant clean
```
This command effectively deletes the `bin` directory.
