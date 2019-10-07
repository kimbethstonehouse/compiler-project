// structdecl, should pass
struct s {
    int x;
};

// vardecl, should pass
int x;

// should fail, expecting fundecl or EOF
#include "file"

// fundecl, should pass
void main() {
}

// should fail, expecting EOF
#include "file"