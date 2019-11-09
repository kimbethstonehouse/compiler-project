struct a {
    int b;          // 4
    void* c;        // 4
    char d;         // 4
    int e[10];      // 40
    void* g[1];     // 4
    char f[10];     // 12
};

struct a b;

void main() {
    struct a c;

    b.b;
    c.d;

    c.g[1];
}