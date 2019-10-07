// EXPECTED PASS
// no vardecl
int main() {
    while (1) {
        x = 1;
    }
}

// no stmt
int main() {
    int x;
}

// vardecl and stmt
int main() {
    int x;
    while (1) {
        x = 1;
    }
}

// multiple vardecl and stmt
int main() {
    int x;
    char y;
    struct str s;

    x = 11;
    y = 'C';
    s = "string";
}

// EXPECTED FAIL
// stmt then vardecl
int main() {
    x = 4;
    int x;
}

// unterminated braces
int main() {
    x = 4;