// EXPECTED PASS
void main() {
    // (exp)
    (3+4);

    // ident
    golf2000;

    // int
    11;

    // - exp
    -40;
    -(3+4);

    // char
    'c';

    // string
    "string";

    // exp op exp
     3 > 4;
     3 >= 4;
     3 < 4;
     3 <= 4;
     3 != 4;
     3 == 4;
     3 + 4;
     3 - 4;
     3 / 4;
     3 * 4;
     3 % 4;
     3 || 4;
     3 && 4;

     // array access
     x[4];
     // nested
     x[x[4]];
     // array exp op exp
     x[11+8];

     // field access
     x.field;

     // valueat
     *p;
     // pointer pointer
     ***p;

     // funcall
     x();
     x(y, z);

     // sizeof
     sizeof (int);
     sizeof (struct str);

     // typecast
     (int) 4;

     // nested field access
     x.field.field;

     // nested array access
     a[x][x];
}
