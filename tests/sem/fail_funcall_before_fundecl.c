// EXPECTED FAIL
// funcall before fundecl
void main() {
    int x;
    foo(x);
}

void foo(int x) {
    x = 14;
}