struct a {
 int b;
};

struct c {
    struct a a[5];
};

struct a a[5];

int main() {
    struct c cons;

    cons.a[0];
}