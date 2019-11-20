#include "minic-stdlib.h"

struct a {
    int x;
};

struct b {
    int z;
    struct a a1;
};

void main() {
    struct b bArr[10];
    struct a* ia;
    struct a a2;
    struct b b2;

    // varexpr
    struct b b1;
    // PRINT 0
    b1.z = 0;
    print_i(b1.z);

    // field access
    // PRINT 2
    b1.a1.x = 2;
    print_i(b1.a1.x);

    a2 = b1.a1;
    // PRINT 1
    a2.x = 1;
    print_i(a2.x);

    // TODO: array access doesn't work
//    b2 = bArr[1];
//    b2.z = 2;
//    print_i(b2.z);
//
//    // valueat
    ia = (struct a*) mcmalloc(sizeof(struct a));
    (*ia) = a2;
    // PRINT 3
    (*ia).x = 3;
    print_i((*ia).x);
}