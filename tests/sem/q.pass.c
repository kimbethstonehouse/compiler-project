// ALL EXPECTED PASS
// 13
//struct a {
//    int a;
//};

// 12
//struct b {
//    int b;
//};
//
//struct a {
//    int b;
//};
//
//struct a a;

// 11
struct person {
    char* name;
    int age;

    struct person* children;
};

// 10
//struct sa {
//	int b;
//};
//
//struct sa name() {
//	struct sa that;
//	return that;
//}
//
//int main() {
//	return name().b;
//}

// 9
//int basket[10];
//
//void main() {
//	basket[1] = 2;
//}

// 8
//struct person {
//	int age;
//};
//
//struct person bob;
//
//int main() {
//	bob.age = 2;
//	return bob.age;
//}

// 7
//void main() {
//	int i;
//	int v;
//	int c;
//
//	i = sizeof(int) + sizeof(int*);
//	v = sizeof(void) + sizeof(void*);
//	c = sizeof(char) + sizeof(char*);
//}

// 6
//void main() {
//	if (1) {
//		while (1) {
//			{
//			}
//		}
//	}
//}

// 5
//void none() {}
//void main() {
//	return none();
//}

// 4
//void main() {
//    int i;
//    char c;
//    i=(int)c;
//}

//3
//void t_ps() {
//    return print_s((char*) "me");
//}
//
//void t_pi() {
//    return print_i(1);
//}
//
//void t_pc() {
//    return print_c('c');
//}
//
//char t_rc() {
//    return read_c();
//}
//
//int t_ri() {
//    return read_i();
//}
//
//void* t_mcm() {
//    return mcmalloc(16);
//}

// 2
//void main() {
//    int i;
//    int* p;
//    i=0;
//    *p=i;
//}

// 1
//int a[2];
//
//int that() {
//    return 1;
//}
//
//void main() {
//    a[that()];
//}