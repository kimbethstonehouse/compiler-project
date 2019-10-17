#include "testinclude"
#include "multipleinclude"

struct xyz {
    int x;
    int a[2];
};
struct teststruct {
    char testchar;
    int* xyzpointertest;
    char multchar;
};

struct multiplestruct {
    int y;
};

int globalinttest;
char globalchartest;
int arraytest[4];

void main() {
    int blockvartest;
    char* blockcharpointertest;

    while (blockvartest>4) {
        if (blockvartest == 3) {
            blockvartest = 10;
        } else {
            blockvartest = blockvartest + 1;
            globalinttest = globalinttest + 3;
        }
    }

    testfuncvar = testfunc(blockvartest, globalchartest, globalinttest);

    return blockvartest;
}

int testfunc(int x, char c, int y) {
    int test;
    int* p;

    test = 3 * 4;
    test = 3+4;
    test = 3 - 4;
    test = 3/4;
    test = 3 % 4;

    if(test < 5) {
        if(test >= 2 && test <=4) {
            while (test != 10 || test != 3) {
                test + 1;
            }
        }
    }

    *p;

    sizeof(int);

    return arraytest[2];

    xyz.x;

    (char)test;

}