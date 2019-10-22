// EXPECTED FAIL
// field name does not exist
struct s {
    int x;
};

void main() {
    struct s a;
    a.p = 1;
}