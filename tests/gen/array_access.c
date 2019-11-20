struct s {
    char p[4];
    int x;
    int q[11];
};

int a[14];

void main() {
    int c[10];
    char b[12];
    int* id;
    int* iq;
    int d[17];
    struct s str;
    char f;

    a[1] = 1;
    b[5] = 'C';

    // GLOBAL INT ARRAY
    // should print 1
    print_i(a[1]);

    // LOCAL CHAR ARRAY
    // should print C
    print_c(b[5]);

    // POINTER
    // should print 8
    id = (int*) d;
    id[10] = 8;
    print_i(id[10]);

    // STRUCT ACCESS ARRAY
    // should print D
    str.p[2] = 'D';
    f = str.p[2];
    print_c(f);

    // STRUCT ACCESS POINTER
    // should print 456 twice
    iq = (int*) str.q;
    iq[1] = 456;
    print_i(iq[1]);
    print_i(str.q[1]);
}
