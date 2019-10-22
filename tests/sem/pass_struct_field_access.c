// EXPECTED PASS
// valid field access
struct s {
    int x;
};

void main() {
    struct s a;
    a.x = 1;
}