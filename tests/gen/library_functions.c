void main() {
    int a;
    char b;

    a = 1;
    b = 'A';

    print_i(a);
    print_c(b);
    print_s((char*) "Hello");

    a = read_i();
    b = read_c();

    print_i(a);
    print_c(b);
}

// should print 1AHello
// then the input given for a and b

//.data
//
//str0: .asciiz "Hello"
//
//.text
//
//
//.globl main
//main:
//move $fp,$sp
//addi $sp,$sp,-8
//li $t9,1
//sw $t9,0($fp)
//li $t8,'A'
//sw $t8,-4($fp)
//
//lw $s7,0($fp)
//li $v0,1
//move $a0,$s7
//syscall
//
//lw $s7,-4($fp)
//li $v0,11
//move $a0,$s7
//syscall
//
//la $s7 str0
//li $v0,4
//move $a0,$s7
//syscall
//
//li $v0,5
//syscall
//move $s7,$v0
//sw $s7,0($fp)
//
//li $v0,12
//syscall
//move $s6,$v0
//sw $s6,-4($fp)
//
//lw $s5,0($fp)
//li $v0,1
//move $a0,$s5
//syscall
//
//lw $s5,-4($fp)
//li $v0,11
//move $a0,$s5
//syscall
//subi $sp,$sp,-8
//func_main_end:
//li $v0 10
//syscall

