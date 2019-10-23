// ALL EXPECTED PASS

//3
void t_ps() {
    return print_s((char*) "me");
}

void t_pi() {
    return print_i(1);
}

void t_pc() {
    return print_c('c');
}

char t_rc() {
    return read_c();
}

int t_ri() {
    return read_i();
}

void* t_mcm() {
    return mcmalloc(16);
}

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