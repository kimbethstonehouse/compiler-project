struct a {
    int b;          // 4
    char c;         // 4
    int f[10];      // 40
};

int x;              // 4
struct a p;         // 48
char* f;            // 4
int* g[5];          // 20
char c;             // 1

//.data
//x: .space 4
//p: .space 48
//f: .space 4
//g: .space 20
//c: .space 1
//
//.text
//main:
//li $v0 10
//syscall