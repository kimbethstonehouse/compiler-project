void main() {
    int x;
    int y;
    char c;

    {
        int x;

        {
            int x;
        }
    }
}

//.data
//
//
//.text
//
//
//.globl main
//main:
//move $fp,$sp
//addi $sp,$sp,-12
//addi $sp,$sp,-4
//addi $sp,$sp,-4
//subi $sp,$sp,-4
//subi $sp,$sp,-4
//subi $sp,$sp,-12
//func_main_end:
//li $v0 10
//syscall
