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
        return 11;
    }

    {
        if (1) {
            while (1) {
                if (1) {
                    {
                        {
                            {
                                return 14;
                            }
                        }
                    }
                } else {
                    return 14;
                }
            }
        }
    }
}