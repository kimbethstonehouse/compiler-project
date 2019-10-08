// EXPECTED PASS
int x;
int x[1];

char x;
char x[12];

void x;
void x[414];

struct x y;
struct x y[11];

int* x;
int* x[1];

char* x;
char* x[12];

void* x;
void* x[414];

struct x* y;
struct x* y[11];

// EXPECTED FAIL
int x*;
int x*[1];

char x*;
char x*[12];

void x*;
void x*[414];

struct x y*;
struct x y*[11];

// no sc
int x

// no type
x;