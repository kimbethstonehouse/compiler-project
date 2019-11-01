struct a {
    int x;          // 4
    struct a* s;    // 4
};

struct a p;     // 8

//.data
//p: .space 8
//
//.text
//main:
//li $v0 10
//syscall
