void main() {
    int* p;
    int i;
    int* f;
    p = (int*) mcmalloc(sizeof(int));
    f = (int*) mcmalloc(sizeof(int));

    // should print 5
    *p = 5;
    print_i(*p);

    // should print 8
    *p = 8;
    print_i(*p);

    i=11;
    *f=i;
    // should print 1111
    print_i(*f); //
    print_i(i);
}