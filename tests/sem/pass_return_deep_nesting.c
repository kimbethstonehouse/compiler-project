// EXPECTED PASS
void main() {

    if (1) {
        return;
    }


    while (1) {
        while (1) {
            if (1) {
                return;
            }
        }
    }

    {
        return;
    }

    {
        if (1) {
            while (1) {
                if (1) {
                    {
                        {
                            {
                                return;
                            }
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }
}

int foo() {
   return 1;

    if (1) {
        return 1;
   }
}