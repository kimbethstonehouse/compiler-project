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
//.text
//main:
//move $fp,$sp
//addi $sp,$sp,-20
//li $v0 10
//syscall
