// EXPECTED PASS
// 1
//int x;

// 2
//void main() {
//    int x;
//
//    x = 1;
//}

// 3
//void foo() {
//    x = 2;
//    {
//        x = 3;
//    }
//}

// 4
//void x() {
//    int x;
//}

// 5
//int i;
//int j;
//void main(int i) {
//  int j;
//  i = 3;
//  while(i == j){
//    char j;
//    j = '4';
//  }
//  j = 3;
//}

// 6
//void hello(){}
//void main() {
//    int hello;
//}

// 7
//struct hello {
//    int x;
//};
//
//void main() {
//    int hello;
//}

// 8
//struct hello {
//    int hello;
//};

// 9
//struct hello {
//    int x;
//};
//
//struct hello x;
//
//void main() {
//    x.x;
//}

// 10
//struct a {
//    int a;
//};
//
//void a() {}