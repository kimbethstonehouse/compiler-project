char* a;

void main() {
        "Hello, world!";
        a = (char*) "Jen";
        "Hello";
        "world";
        "Jen";
        "Uday";
        "all work and no play makes jack a dull boy";
        "Jen";
}

//.data
//a: .space 4
//str0: .asciiz "Hello, world!"
//str1: .asciiz "Jen"
//str2: .asciiz "Hello"
//str3: .asciiz "world"
//str4: .asciiz "Jen"
//str5: .asciiz "Uday"
//str6: .asciiz "all work and no play makes jack a dull boy"
//str7: .asciiz "Jen"
//
//.text
//main:
//li $v0 10
//syscall
