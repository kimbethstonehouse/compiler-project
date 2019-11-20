.data

str0: .asciiz "Hello world!"
str1: .asciiz "Kim"

.text


### entering visit fundecl main
.globl main
main:
move $fp,$sp
subi $sp,$sp,4

### entering visit while
li $t9,0
beqz $t9,while_end0
while_body0:
subi $sp,$sp,0
la $t9 str0
li $t8,0
beqz $t8,while_end0
j while_body0
while_end0:

la $t8 str1

### entering visit while
li $s7,1
beqz $s7,while_end1
while_body1:
subi $sp,$sp,0

### entering visit while
li $s7,2
beqz $s7,while_end2
while_body2:
subi $sp,$sp,0
li $s7,2
beqz $s7,while_end2
j while_body2
while_end2:

li $s7,1
beqz $s7,while_end1
j while_body1
while_end1:


### entering visit while
li $s7,1
beqz $s7,while_end3
while_body3:
subi $sp,$sp,0

### entering visit while
li $s7,2
beqz $s7,while_end4
while_body4:
subi $sp,$sp,0

### entering visit while
li $s7,3
beqz $s7,while_end5
while_body5:
subi $sp,$sp,0
li $s7,3
beqz $s7,while_end5
j while_body5
while_end5:

li $s7,2
beqz $s7,while_end4
j while_body4
while_end4:

li $s7,1
beqz $s7,while_end3
j while_body3
while_end3:

func_main_end:
addi $sp,$sp,4
li $v0 10
syscall

