struct p {
    int age;
    char gender;
};

struct p foo() {
    struct p s;
    s.age = 11;
    s.gender = 'F';

    return s;
}

void main() {
    struct p s;
    s = foo();

    print_i(s.age);
    print_c(s.gender);
}

//struct s {
//    int age;
//    char gender;
//    int a[3];
//    int b;
//};
//
//void foo(struct s p) {
//    print_i(p.age);
//    print_c('\n');
//
//    print_c(p.gender);
//    print_c('\n');
//
//    print_i(p.a[0]);
//    print_c('\n');
//
//    print_i(p.a[1]);
//    print_c('\n');
//
//    print_i(p.a[2]);
//    print_c('\n');
//
//    print_i(p.b);
//}
//
//void main() {
//    struct s p;
//
//    p.age = 21;
//    p.gender = 'F';
//    p.a[0] = 0;
//    p.a[1] = 1;
//    p.a[2] = 2;
//    p.b = 500;
//
//    foo(p);
//}