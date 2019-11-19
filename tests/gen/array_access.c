struct s {
    char p[4];
    int x;
    int q[11];
};
int a[14];

void main() {
    int c[10];
    char b[12];
    int* d[17];
    struct s str;
    char f;

    a[1] = 1;
    b[5] = 'C';

    // should print 1
    print_i(a[1]);
    // should print C
    print_c(b[5]);

    // should print 900
    c[6] = 900;
    print_i(c[6]);

    // should print D
    str.p[2] = 'D';
    print_c(str.p[2]);

    // should print D
    f = str.p[2];
    print_c(f);
}

//.data
//
//a: .space 56
//
//.text
//
//main:
//move $fp,$sp
//addi $sp,$sp,-52
//
//la $t8,a
//li $s7,1
//mul $s7,$s7,4
//add $t8,$t8,$s7
//lw $t9,($t8)
//
//
//la $s7,-40($fp)
//li $s6,5
//mul $s6,$s6,1
//sub $s7,$s7,$s6
//lb $t8,($s7)
//
//
//li $v0 10
//syscall
