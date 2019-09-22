// 1-6 should all be lexed as valid identifiers
int appleCount = 1;
int x = 2;
int year1988 = 3;
int an_underscore = 4;
int miXTURE_ = 5;
int _underscore = 6;
int 1988year = 7; // should be lexed as an int then an identifier