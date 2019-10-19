// EXPECTED FAIL
// new scope then return to old scope
int x;

void y() {
    char c;
}

void x() {
    int p;
}