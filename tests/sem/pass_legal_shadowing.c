// EXPECTED PASS
// legal shadowing
int x;

void main() {
    int x;

    x = 1;
}

void foo() {
    x = 2;
    {
        x = 3;
    }
}

void x() {
    int x;
}