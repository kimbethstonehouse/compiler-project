int a[14];

void main() {
    int c[10];
    char b[12];

    a[1];
    b[5];
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
