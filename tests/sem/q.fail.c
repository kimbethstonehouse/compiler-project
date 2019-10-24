// FAIL - self refer
//struct person {
//    char* name;
//    int age;
//
//    struct person* children;
//};
//
//struct house {
//    struct person occupants[10];
//    struct house garage;
//};

// FAIL - void vardecl
//void a;

// FAIL - value of notdecl
//void main() {
//	*haha;
//}

// FAIL - valueof non pointers
//void main() {
//	*2;
//	*"this";
//	*'a';
//}

//// FAIL - a fuck ton of errors
//void test(int example) {}
//
//void main() {
//	int j;
//
//	a[2];
//	a.x = 2;
//	j = a.x;
//	a(2);
//	test(a.x);
//	test(a[2]);
//}

// FAIL - incorrect params, expect 7 errors at least
// duplicate params decl
//void test_params(int a, char b, void* c, int* d, char* d) {}
//
//void main() {
//    // wrong # args, won't pick up the invalid cast from int to char
//    test_params(1, (char) 2, (char*) "This");
//    // wrong # args, won't pick up the invalid cast from int to char,
//    // or the arraytype instead of char*
//    test_params(1, (char) 2, (int*) ((char*) "This"), "That");
//    // invalid cast from int to char, int* instead of void*
//    test_params(1, (char) 2, (int*) (char*) "This", (char*) "That", "Ours");
//    // wrong # args
//    test_params();
//    // wrong # args
//    test_params("asdf");
//}

// FAIL - duplicate var decl
//struct foo_t {
//  int bar;
//  int bar;
//};

// FAIL - referring to self
//struct a {
//    struct a b;
//};

// FAIL - invalid return type
//struct sa {
//	int b;
//};
//
//struct sb {
//	int b;
//};
//
//struct sa name() {
//	struct sb that;
//	return that;
//}

// FAIL - no field called c
//struct a {
//    int b;
//};
//
//struct a d;
//
//int main() {
//    return d.c;
//}

// FAIL - assign char to int
//void main() {
//    int i;
//    char c;
//    i=c;
//}

// FAIL - mismatched return types, expect 3 errors
//void main() {
//	if (1) {
//		if (0 < 1) {
//			return 'a';
//		} else {
//			return;
//		}
//		return 1;
//	}
//	return "hahahahahaha";
//}

// FAIL - mismatched return types, expect 3 errors
//int main() {
//    if (1) {
//        return;
//    }
//    return 1;
//}
//
//void mine() {
//    if (1) {
//        while (1) {
//            return 1;
//        }
//        return;
//    } else {
//        return 1;
//    }
//    return;
//}

// FAIL - this is worth checking, I'm passing this one
// papa qais thinks this should fail, but I'm not convinced
// gcc says is fine, and a Piazza mentioned that we don't have
// to handle the case where no return is supplied
//int auto_return() {
//	return 1;
//}
//
//int main() {
//	if (1) {
//		auto_return();
//	}
//}

// FAIL - repeated decl, should have four errors
//void test_params(int a, int a, char a) {
//    int a;
//}
//
//void main() {
//    char c;
//    char* c;
//}

// FAIL - true has not been declared
//int main() {
//	if (true) {
//		return 1;
//	}
//	return 0;
//}

// FAIL - mismatching return type
//void main() {
//    return 1;
//}

// FAIL - illegal global shadowing
//struct MyStruct
//{
//    int a;
//};
//
//struct MyStruct
//{
//    int b;
//};

// FAIL - illegal global shadowing, should have one error
// for the struct shadowing and one for the variable/function shadowing
// but NOT one for the variable/function shadowing the structs
//struct MyStruct
//{
//    int a;
//};
//
//struct MyStruct
//{
//    int b;
//};
//
//int MyStruct;
//
//void MyStruct() {}

// FAIL - illegal global shadowing
//void main() {
//
//}
//
//void main() {
//
//}

// FAIL - conditions are not of type int
//void* a;
//
//void main() {
//	if ("") {
//		while ('c') {
//			if (a) {
//
//			}
//		}
//	}
//}

// FAIL - assigments cannot have arrays on the LHS
//char str[6];
//
//void main() {
//    str = "Hello";
//}

// FAIL - library functions already defined
//void print_s(char* s){}
//void print_i(int i){}
//void print_c(char c){}
//char read_c(){return 'a';}
//int read_i(){return 1;}
//void* mcmalloc(int size){}

// FAIL - assignments are not of the correct types
//void main() {
//    int i;
//    i+2=3;
//    4 = 5;
//    sizeof(int) = 5;
//    main() = 5;
//}

// FAIL - array access index is char not int
int c[10];

void main() {
	c['a'];
}