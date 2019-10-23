// EXPECTED FAIL
// new scope then return to old scope
//int x;
//
//void y() {
//    char c;
//}
//
//void x() {
//    int p;
//}

// duplicate var decl
//int x;
//int x;

// var and func same name
//int x;
//int x() {}

// shadowing params
//void foo(int i) {
//    int i;
//}

// lecture example, 3 errors
//int i;
//int i;
//
//void main(int j) {
//    int j;
//    int k;
//    int k;
//}

// lecture example
int i;
void i() {
}

