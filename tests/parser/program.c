// include, should pass
#include "file"

// structdecl, should pass
struct s {
    int x;
};

// vardecl, should pass
int x;

// should fail, expecting fundecl or EOF
#include "file"

// should fail, expecting fundecl or EOF
struct s {
    int x;
};

// fundecl, should pass
void main() {
}

// should fail, expecting EOF
int x;