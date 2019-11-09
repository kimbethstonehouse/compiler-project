void main() {
    int x;

    while(0) {
        "Hello world!";
    }

    "Kim";

    while (1) {
        while(2) {
        }
    }
}

//.data
//
//str0: .asciiz "Hello world!"
//str1: .asciiz "Kim"
//
//.text
//
//main:
//move $fp,$sp
//
//li $t9,0
//beqz $t9,while_end0
//while_body0:
//li $t9,0
//beqz $t9,while_end0
//j while_body0
//while_end0:
//
//
//li $t9,1
//beqz $t9,while_end1
//while_body1:
//
//li $t9,2
//beqz $t9,while_end2
//while_body2:
//li $t9,2
//beqz $t9,while_end2
//j while_body2
//while_end2:
//
//li $t9,1
//beqz $t9,while_end1
//j while_body1
//while_end1:
//
//addi $sp,$sp,-4
//
//li $v0 10
//syscall