char* str;
//char hello[5];

void main() {
    char hello[6];

    // print hello world
    print_s((char*) "Hello world!\n");

    // print hello
    hello[0] = 'h';
    hello[1] = 'e';
    hello[2] = 'l';
    hello[3] = 'l';
    hello[4] = 'o';
    hello[5] = '\n';
    print_s((char*) hello);

    // print hello world
    str = (char*) "Hello world!\n";
    print_s(str);
}