int main() {
    int first;
    int last;
    int middle;
    int n;
    int search;
    int array[5];
    n = 5;
    array[0] = 10;
    array[1] = 20;
    array[2] = 30;
    array[3] = 40;
    array[4] = 50;

   print_s((char*)"Enter value to find\n");
   search = read_i();

   first = 0;
   last = n - 1;
   middle = (first+last)/2;

   while (first <= last) {
      if (array[middle] < search)
         first = middle + 1;
      else if (array[middle] == search) {
         print_s((char *)"found.\n");
         return 0;
      }
      else
         last = middle - 1;

      middle = (first + last)/2;
   }
   if (first > last)
      print_s((char *)"Not found!\n");

   return 0;
}