struct s {
    int age;
    char gender;
    int number;
    int intArr[3];
    char charArr[2];
};

void bar(struct s k, int a, char b, char l, struct s f) {
    print_i(k.age);
    print_c('\n');

    print_c(k.gender);
    print_c('\n');

    print_i(k.number);
    print_c('\n');

    print_i(k.intArr[0]);
    print_c('\n');

    print_i(k.intArr[1]);
    print_c('\n');

    print_i(k.intArr[2]);
    print_c('\n');

    print_c(k.charArr[0]);
    print_c('\n');

    print_c(k.charArr[1]);
    print_c('\n');

    print_i(a);
    print_c('\n');

    print_c(b);
    print_c('\n');

    print_c(l);
    print_c('\n');

    print_i(f.age);
    print_c('\n');

    print_c(f.gender);
    print_c('\n');

    print_i(f.number);
    print_c('\n');

    print_i(f.intArr[0]);
    print_c('\n');

    print_i(f.intArr[1]);
    print_c('\n');

    print_i(f.intArr[2]);
    print_c('\n');

    print_c(f.charArr[0]);
    print_c('\n');

    print_c(f.charArr[1]);
    print_c('\n');
}

void main() {
    struct s k;
    int a;
    char b;
    char l;
    struct s f;

    k.age = 20;
    k.gender = 'M';
    k.number = 50;
    k.intArr[0] = 1;
    k.intArr[1] = 2;
    k.intArr[2] = 3;
    k.charArr[0] = 'Z';
    k.charArr[1] = 'Y';
    a = 1;
    b = 'A';
    l = 'P';
    f.age = 50;
    f.gender = 'F';
    f.number = 80;
    f.intArr[0] = 3;
    f.intArr[1] = 2;
    f.intArr[2] = 1;
    f.charArr[0] = 'X';
    f.charArr[1] = 'W';

    bar(k, a, b, l, f);
}


//void main() {
//    int x;
//    int y;
//    char c;
//
//    {
//        int x;
//
//        {
//            int x;
//        }
//    }
//}

//.text
//
//
//### entering visit fundecl
//.globl main
//main:
//move $fp,$sp
//subi $sp,$sp,12
//subi $sp,$sp,4
//subi $sp,$sp,4
//func_main_end:
//addi $sp,$sp,20
//li $v0 10
//syscall
