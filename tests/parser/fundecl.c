// EXPECTED PASS
// no params
int main() {}
char main() {}
void main() {}
struct str main() {}

// one param
int* main(int x) {}

// two params
char* main(char x, int x) {}

// struct param
void* main(struct str s) {}

// void param
struct str* main(void x) {}

// pointer params
int main(struct str* s, int* x) {}

// EXPECTED FAIL
// array param
struct str* main(int x[1]) {}

// nested fundecl
void main() {
    void main() {
    }
}