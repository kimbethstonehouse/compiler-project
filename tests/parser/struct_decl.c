#include "minic.studio.h"

// no var decl, should fail
//struct z {
//};

//// one var decl
//struct str {
//  int x;
//};
//
//// two var decl
//struct str {
//  int x;
//  char y[10];
//};

// nested struct decl, should pass
struct str {
  char c;

  struct str s;
  struct str s[11];
  struct str* s;
  struct str* s[12];
};

// vardecl
int x;
int y;

//fundecl
void main() {
    struct str s;

    x = 0;
    y = 1;


    i = x[11];
    x = s.field;
}


// nested struct decl, should fail
//struct str {
//  char c;
//
//  struct n n1 {
//    char d;
//  };
//};


