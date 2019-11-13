int foo(int a, int b, int c, int d, int e){
    print_i(a);
    print_i(b);
    print_i(c);
    print_i(d);
    print_i(e);
    return e;
}

void main(){
    int a;
    int b;
    int c;
    int d;
    int e;
    int res;
    a=0;
    b=1;
    c=2;
    d=3;
    e=4;
    foo(a,b,c,d,e);
    print_i(e);
}




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