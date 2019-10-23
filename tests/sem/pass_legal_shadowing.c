// EXPECTED PASS
// legal shadowing
//int x;
//
//void main() {
//    int x;
//
//    x = 1;
//}
//
//void foo() {
//    x = 2;
//    {
//        x = 3;
//    }
//}
//
//void x() {
//    int x;
//}

int i;
int j;
void main(int i) {
  int j;
  i = 3;
  while(i == j){
    char j;
    j = '4';
  }
  j = 3;
}