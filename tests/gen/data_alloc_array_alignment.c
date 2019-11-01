char f[17];         // size 17 but needs to be 20, mod is 1
char p[26];         // size 26 but needs to be 28, mod is 2
char e[11];         // size 11 but needs to be 12, mod is 3

//.data
//f: .space 20
//p: .space 28
//e: .space 12
//
//.text
//main:
//li $v0 10
//syscall