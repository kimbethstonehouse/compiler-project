.data
arr: .space 12
str: .asciiz "Kim"

.text

li $t0, 'K'
sw $t0, arr

li $t0, 'i'
sw $t0 arr+4

li $t0, 'm'
sw $t0 arr+8

li $v0, 4
la $a0, arr
syscall

li $v0,10
syscall