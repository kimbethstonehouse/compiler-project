struct a {
    char b[17];     // size 17 but needs to be 20, mod is 1
    char c[26];     // size 26 but needs to be 28, mod is 2
    char d[11];     // size 11 but needs to be 12, mod is 3
};

struct a e;         // 60
char b[17];         // size 17 but needs to be 20, mod is 1
char c[26];         // size 26 but needs to be 28, mod is 2
char d[11];         // size 11 but needs to be 12, mod is 3

//.data
//e: .space 60
//a: .space 20
//b: .space 28
//c: .space 12
//
//.text
//main:
//li $v0 10
//syscall