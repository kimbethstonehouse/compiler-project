// EXPECTED PASS
// nested block
void main() {
    {}
}

// nested block with vardecl and stmt
void main() {
    {
        int x;
        x = 4;
    }
}

// while
void main() {
    while (1) {}
    // exp + exp
    while(x+5) {}
    // string literal
    while("string") {}
}

// if
void main() {
    // no else
    if (1) {}
    // with else
    if (1) {} else {}
}

// return
void main() {
    // double return
    return;
    return;
    // return exp
    return x+5;
}

// exp = exp
void main() {
    1 = 2;
    3 + 5 = x + y;
    x < 11 = 40;
}

// exp
void main() {
    1;
    3 + 5;
    i[11];
    p.field;
}

// EXPECTED FAIL
// vardecl instead of exp
void main() {
    while (char c) {}
}

// double else
void main() {
    if (1) {} else {} else {}
}

// return no semicolon
void main() {
    return
}

// exp no semicolon
void main() {
    1 + 2
}