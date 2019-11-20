void main() {
    int a;
    char b;

    a = 1;
    b = 'A';

    // should print 1AHello
    print_i(a);
    print_c(b);
    print_s((char*) "Hello");

    a = read_i();
    b = read_c();


    // then the input given for a and b
    print_i(a);
    print_c(b);
}



