struct a {
    int x;          // 4
    struct a* s;    // 4
};

struct a p;     // 8

//.data
//p: .word 2