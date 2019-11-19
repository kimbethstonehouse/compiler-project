#include "minic-stdlib.h"

struct s {
    int age;
    char gender;
    int number;
    int intArr[3];
    char charArr[17];
};

void foo(struct s k) {
    print_c(k.charArr[0]);
    print_c(k.charArr[1]);
    print_c(k.charArr[2]);
    print_c(k.charArr[3]);
    print_c(k.charArr[4]);
    print_c(k.charArr[5]);
    print_c(k.charArr[6]);
    print_c(k.charArr[7]);
    print_c(k.charArr[8]);
    print_c(k.charArr[9]);
    print_c(k.charArr[10]);
    print_c(k.charArr[11]);
    print_c(k.charArr[12]);
    print_c(k.charArr[13]);
    print_c(k.charArr[14]);
    print_c(k.charArr[15]);
    print_c(k.charArr[16]);
}

int main() {
//    int size = sizeof(void);
//    print_i(size);

    struct s k;

    k.age = 20;
    k.gender = 'M';
    k.number = 50;
    k.intArr[0] = 1;
    k.intArr[1] = 2;
    k.intArr[2] = 3;
    k.charArr[0] = 'A';
    k.charArr[1] = 'B';
    k.charArr[2] = 'C';
    k.charArr[3] = 'D';
    k.charArr[4] = 'E';
    k.charArr[5] = 'F';
    k.charArr[6] = 'G';
    k.charArr[7] = 'H';
    k.charArr[8] = 'I';
    k.charArr[9] = 'J';
    k.charArr[10] = 'K';
    k.charArr[11] = 'L';
    k.charArr[12] = 'M';
    k.charArr[13] = 'N';
    k.charArr[14] = 'O';
    k.charArr[15] = 'P';
    k.charArr[16] = 'Q';


//    print_c(k.charArr[0]);
//    print_c(k.charArr[1]);
//    print_c(k.charArr[2]);
//    print_c(k.charArr[3]);
//    print_c(k.charArr[4]);
//    print_c(k.charArr[5]);
//    print_c(k.charArr[6]);
//    print_c(k.charArr[7]);
//    print_c(k.charArr[8]);
//    print_c(k.charArr[9]);
//    print_c(k.charArr[10]);
//    print_c(k.charArr[11]);
//    print_c(k.charArr[12]);
//    print_c(k.charArr[13]);
//    print_c(k.charArr[14]);
//    print_c(k.charArr[15]);
//    print_c(k.charArr[16]);


//    print_s((char*) k.charArr);

    foo(k);
}


//char c;
//char d;
//
//int main() {
//    c = 'A';
//    d = c;
//}


//int foo(int a, int b, int c, int d, int e){
//    print_i(a);
//    print_i(b);
//    print_i(c);
//    print_i(d);
//    print_i(e);
//    return e;
//}
//
//void main(){
//    int a;
//    int b;
//    int c;
//    int d;
//    int e;
//    int res;
//    a=0;
//    b=1;
//    c=2;
//    d=3;
//    e=4;
//    res = foo(a,b,c,d,e);
//    print_i(res);
//}

//int foo(int a, int b, int c, int d, int e){
//    print_i(a);
//    print_i(b);
//    print_i(c);
//    print_i(d);
//    print_i(e);
//    return e;
//}
//
//void main(){
//    int a;
//    int b;
//    int c;
//    int d;
//    int e;
//    int res;
//    a=0;
//    b=1;
//    c=2;
//    d=3;
//    e=4;
//    foo(a,b,c,d,e);
//    //print_i(e);
//}




//void bar() {
//    return;
//}
//
//void foo() {
//    bar();
//    return;
//}
//
//void main() {
//    foo();
//    return;
//}


//#include <stdio.h>
//
//void main() {
//    if (0) {
//        printf("Hello");
//    }
//}

//void main() {
//        "Hello, world!";
//}