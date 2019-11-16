int x;

void main() {
    x = 4;

    // print 4
    print_i(x);

    {
        int x;
        x = 7;

        // print 7
        print_i(x);

        {
            x = 6;
            // print 6
            print_i(x);

            {
                int x;
                x = 123;
                // print 123
                print_i(x);
            }
        }
    }

    // print 4
    print_i(x);
}