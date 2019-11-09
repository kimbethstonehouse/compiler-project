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
//main:
//move $fp,$sp
//addi $sp,$sp,-12
//addi $sp,$sp,-4
//addi $sp,$sp,-4
//
//li $v0 10
//syscall
