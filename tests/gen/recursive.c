int fibonacci(int i) {
    if (i==0) {
        return 0;
    }

    if (i == 1) {
        return 1;
    }

    return fibonacci(i-1) + fibonacci(i-2);
}

void main() {
    int i;
    i = fibonacci(10);
    print_i(i);
}