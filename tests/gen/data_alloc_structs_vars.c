struct a {
    int b;          // 4
    void* c;        // 4
    char d;         // 4
    int e[10];      // 40
    void* g[1];     // 4
    char f[10];     // 12
};

int b;              // 4
struct a c;         // 68
char* d;            // 4
int* e[5];          // 20
char f;             // 1

//.data
//b: .space 4
//c: .space 68
//d: .space 4
//e: .space 20
//f: .space 1
//
//.text
//main:
//li $v0 10
//syscall