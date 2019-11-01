char* jen;
char* kim;

void main() {
        "Hello, world!";
        jen = (char*) "Jen";
        jen[2] = 'p';
        kim = (char*) "Jen";
        kim = (char*) "Kim";
}

//.data
//jen: .space 4
//kim: .space 4
//str0: .asciiz "Hello, world!"
//str1: .asciiz "Jen"
//str2: .asciiz "Jen"
//str3: .asciiz "Kim"
//
//.text
//main:
//li $v0 10
//syscall