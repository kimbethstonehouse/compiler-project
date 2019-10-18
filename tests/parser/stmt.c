// EXPECTED PASS
// while
void main() {
    while (1) {}
    // exp + exp
    while(x+5) {}
    // string literal
    while("string") {
        // nested while
        while (1) {
            while (1)  {
                while (1) {
                }
            }
        }
    }

    // while while
    while (1) while (1) {}
}

// if
void main() {
    // no else
    if (1) {}
    // with else
    if (1) {} else {}
    // if if
    if (1) if (1) {} else {}
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
//void main() {
//    while (char c) {}
//}
//
//// double else
//void main() {
//    if (1) {} else {} else {}
//}
//
//// return no semicolon
//void main() {
//    return
//}
//
//// exp no semicolon
//void main() {
//    1 + 2
//}