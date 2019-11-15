void main() {
    char* c;
    int* d;
    char e;
    int f;

    // void pointer to char pointer
    c = (char*) mcmalloc(sizeof(char));
    // void pointer to int pointer
    d = (int*) mcmalloc(sizeof(int));

    // should print C
    *c = 'C';
    print_c(*c);

    // should print N
    *c = 'N';
    print_c(*c);

    // char pointer to int pointer
    // should print 78
    d = (int*) c;
    print_i(*d);

    // int pointer to char pointer
    // should print A
    *d = 65;
    c = (char*) d;
    print_c(*c);

    // char to int
    // should print 70
    e = 'F';
    f = (int) e;
    print_i(f);

    // array to pointer
    //TODO: when youve done arrya access
}