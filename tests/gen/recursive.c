int factorial(int i) {

   if(i <= 1) {
      return 1;
   }

   return i * factorial(i - 1);
}

int main() {
   int i;
   i = 3;
   print_i(factorial(10));
}



//int fibonacci(int i) {
//    if (i==0) {
//        return 0;
//    }
//
//    if (i == 1) {
//        return 1;
//    }
//
//    return fibonacci(i-1) + fibonacci(i-2);
//}
//
//void main() {
//    int i;
//    i = fibonacci(10);
//    print_i(i);
//}