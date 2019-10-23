// EXPECTED PASS
// char to int cast
void main() {
    int* a;
    int b[10];

    (int) 'c';

    // int pointer to char pointer
    (char*) a;

    // int array to int pointer
    (int*) b;
}