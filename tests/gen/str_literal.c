char* str;
char hello[5];

void main() {
    // print hello world
    print_s((char*) "Hello world!\n");

    // print hello
    hello[0] = 'h';
    hello[1] = 'e';
    hello[2] = 'l';
    hello[3] = 'l';
    hello[4] = 'o';
//    print_s((char*) hello);

    // print hello world
    str = (char*) "Hello world!\n";
    print_s(str);
}