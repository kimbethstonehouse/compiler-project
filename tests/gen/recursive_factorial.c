int factorial(int i) {

   if(i <= 1) {
      return 1;
   }

   return i * factorial(i - 1);
}

int main() {
   int i;
   i = read_i();
   print_i(factorial(i));
}
