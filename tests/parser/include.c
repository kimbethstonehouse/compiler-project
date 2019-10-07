#include "file"

// should fail, no string literal following
////#include
////
////// should fail, int literal following
////#include123
//
//// should fail, identifier following
//#includeinclude

// structdecl
struct s {
};

// vardecl
int x;

// should fail, expecting fundecl
#include "file"

// fundecl
void main() {
}

// should fail, expecting EOF
#include "file"