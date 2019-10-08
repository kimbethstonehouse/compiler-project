// EXPECTED PASS
// one vardecl
struct str {
  int x;
};

// two vardecl
struct str {
  int x;
  char y[10];
};

// nested struct decl
struct str {
  char c;

  struct str s;
  struct str s[11];
  struct str* s;
  struct str* s[12];
};

// EXPECTED FAIL
// no braces
struct str
    int x;

// no vardecl
struct str {
};

// nested struct decl
struct str {
  char c;

  struct n {
    char d;
  };
};

// stmt instead of vardecl
struct str {
    x = 1;
};

// no ident
struct {
    int x;
};

// no terminating sc
struct {
    int x;
}


