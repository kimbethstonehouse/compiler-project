// EXPECTED FAIL
// two struct decls with the same name
struct s {
    int x;
};

struct s {
    int y;
};