// size 52
struct a {
    int x;
    int y;
    char p;
    int arr[10];
};

void main() {
    int size;

    // prints 4
    size = sizeof(int);
    print_i(size);

    // prints 1
    size = sizeof(char);
    print_i(size);

    // prints 4
    size = sizeof(int*);
    print_i(size);

    // prints 4
    size = sizeof(char*);
    print_i(size);

    // prints 4
    size = sizeof(void*);
    print_i(size);

    // prints 52
    size = sizeof(struct a);
    print_i(size);

    // prints 4
    size = sizeof(struct a*);
    print_i(size);

    // prints 4
    size = sizeof(void);
    print_i(size);

//    size = sizeof(int* a[10])
}