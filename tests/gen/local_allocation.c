void main() {
    int x;
    int y;

    {
        int x;

        {
            int x;
        }
    }
}

//.data
//
//.text
//main:
//move $fp,$sp
//addi $sp,$sp,-16
//li $v0 10
//syscall
