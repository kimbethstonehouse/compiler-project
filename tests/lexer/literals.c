"\t"

"" // gcc says ok

// string literals
"\b"
"\n"
"\r"
"\f"
"\'"
"\""
"\\"
"\0"
"String \\hello \\"
"String with naughty characters in \n"
"\" hi dear\" she said"

// int literals
0
12093
721
00127

// char literals
// allowed
'\t'
'\b'
'\n'
'\r'
'\f'
'\''
'\"'
'\\'
'\0'
'A'
'c'
'9'
'_'
'A'

// errors
//'xxx'
//'AB'
//'\np'
//
//"string with invalid \a escape chars"
//"unclosed string
//'' // gcc says invalid
