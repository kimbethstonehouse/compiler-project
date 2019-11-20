struct p {
    int age;
    char gender;
};

struct g {
    int age;
    char gender;
    int a[3];
    int b;
};

struct p foo() {
    struct p s;
    s.age = 11;
    s.gender = 'F';

    return s;
}

void main() {
    struct p s;
//    struct g m;

    s = foo();

    // PRINT 11
    print_i(s.age);
    // PRINT F
    print_c(s.gender);

    // PRINT
    // 21
    // F
    // 0
    // 1
    // 2
    // 500
//    m.age = 21;
//    m.gender = 'F';
//    m.a[0] = 0;
//    m.a[1] = 1;
//    m.a[2] = 2;
//    m.b = 500;
//
//    bar(m);
}

//void bar(struct g m) {
//    print_i(m.age);
//    print_c('\n');
//
//    print_c(m.gender);
//    print_c('\n');
//
//    print_i(m.a[0]);
//    print_c('\n');
//
//    print_i(m.a[1]);
//    print_c('\n');
//
//    print_i(m.a[2]);
//    print_c('\n');
//
//    print_i(m.b);
//}