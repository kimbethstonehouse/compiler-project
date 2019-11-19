package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;

    public int getErrorCount() {
        return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character (" + c + ") at " + line + ":" + col);
        error++;
    }


    public Token nextToken() {
        Token result;
        try {
            result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        // skip comments
        // single line
        if (c == '/' && scanner.peek() == '/') {
            scanner.next();
            c = scanner.next();

            while (c != '\n') {
                c = scanner.next();
            }

            return next();
        }

        // multiline
        if (c == '/' && scanner.peek() == '*') {
            scanner.next();
            c = scanner.next();

            while (!(c == '*' && scanner.peek() == '/')) {
                try { c = scanner.next(); }
                catch (EOFException e) {
                    // unterminated comment
                    error(c, line, column);
                    return new Token(TokenClass.INVALID, line, column);
                }
            }

            scanner.next();
            return next();
        }

        // identifier, types and keywords
        if (Character.isLetter(c) || c == '_') {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            c = scanner.peek();

            while (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
                scanner.next();
                c = scanner.peek();
            }

            switch (sb.toString()) {
                case "int": return new Token(TokenClass.INT, line, column);
                case "void": return new Token(TokenClass.VOID, line, column);
                case "char": return new Token(TokenClass.CHAR, line, column);
                case "if": return new Token(TokenClass.IF, line, column);
                case "else": return new Token(TokenClass.ELSE, line, column);
                case "while": return new Token(TokenClass.WHILE, line, column);
                case "return": return new Token(TokenClass.RETURN, line, column);
                case "struct": return new Token(TokenClass.STRUCT, line, column);
                case "sizeof": return new Token(TokenClass.SIZEOF, line, column);
                default: return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            }
        }

        // delimiters
        if (c == '{') return new Token(TokenClass.LBRA, line, column);
        if (c == '}') return new Token(TokenClass.RBRA, line, column);
        if (c == '(') return new Token(TokenClass.LPAR, line, column);
        if (c == ')') return new Token(TokenClass.RPAR, line, column);
        if (c == '[') return new Token(TokenClass.LSBR, line, column);
        if (c == ']') return new Token(TokenClass.RSBR, line, column);
        if (c == ';') return new Token(TokenClass.SC, line, column);
        if (c == ',') return new Token(TokenClass.COMMA, line, column);

        // include
        if (c == '#') {
            char[] include = {'#', 'i', 'n', 'c', 'l', 'u', 'd', 'e'};

            for (int i = 1; i < include.length; i++) {
                c = scanner.peek();
                if (c == include[i]) {
                    scanner.next();
                } else {
                    error(c, line, column);
                    return new Token(TokenClass.INVALID, line, column);
                }
            }

            return new Token(TokenClass.INCLUDE, line, column);
        }

        // literals
        // string literal
        if (c == '\"') {
            StringBuilder sb = new StringBuilder();

            // if there is no next character,
            // throw unterminated string error
            try { c = scanner.next(); }
            catch (EOFException e) {
                error(c, line, column);
                return new Token(TokenClass.INVALID, sb.toString(), line, column);
            }

            while (c != '\"') {
                // escape character is paired with the character to escape, so
                // deal with them both at the same time to avoid complications
                if (c == '\\') {
                    List<Character> specialChars = Arrays.asList('t', 'b', 'n', 'r', 'f', '\'', '\"', '\\', '0');

                    try {
                        sb.append(c);
                        c = scanner.next();
                        if (!specialChars.contains(c)) {
                            // illegal escape character
                            error(c, line, column);
                            return new Token(TokenClass.INVALID, sb.toString(), line, column);
                        }
                    } catch (EOFException e) {
                        // unterminated string, return immediately
                        error(c, line, column);
                        return new Token(TokenClass.INVALID, sb.toString(), line, column);
                    }
                }

                sb.append(c);
                try { c = scanner.next(); }
                catch (EOFException e) {
                    // unterminated string, return immediately
                    error(c, line, column);
                    return new Token(TokenClass.INVALID, sb.toString(), line, column);
                }
            }

            return new Token(TokenClass.STRING_LITERAL, sb.toString(), line, column);
        }

        // int literal
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            c = scanner.peek();

            while (Character.isDigit(c)) {
                sb.append(c);
                scanner.next();
                c = scanner.peek();
            }

            return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
        }

        // char literal
        if (c == '\'') {
            StringBuilder sb = new StringBuilder();
            try { c = scanner.next(); }
            catch (EOFException e) {
                error(c, line, column);
                return new Token(TokenClass.INVALID, sb.toString(), line, column);
            }

            while (c != '\'') {
                // escape character is always paired with the
                // character to escape - deal with them both
                // at the same time to avoid complications
                if (c == '\\') {
                    // skip escape character
                    sb.append(c);
                    try { c = scanner.next(); }
                    catch (EOFException e) {
                        error(c, line, column);
                        return new Token(TokenClass.INVALID, sb.toString(), line, column);
                    }
                }

                sb.append(c);
                try { c = scanner.next(); }
                catch (EOFException e) {
                    error(c, line, column);
                    return new Token(TokenClass.INVALID, sb.toString(), line, column);
                }
            }

            // handle special characters first
            List<String> specialChars = Arrays.asList("\\t", "\\b", "\\n", "\\r", "\\f",
                    "\\'", "\\\"", "\\\\", "\\0");
            if (specialChars.contains(sb.toString())) { return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column); }
            // then regular characters
            else if (sb.toString().length() == 1) { return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column); }
            // then errors
            else {
                error(c, line, column);
                return new Token(TokenClass.INVALID, sb.toString(), line, column);
            }
        }

        // logical operators
        if (c == '&' && scanner.peek() == '&') {
            scanner.next();
            return new Token(TokenClass.AND, line, column);
        }
        if (c == '|' && scanner.peek() == '|') {
            scanner.next();
            return new Token(TokenClass.OR, line, column);
        }

        // comparisons
        // = and ==
        if (c == '=')
            if (scanner.peek() == '=') {
                scanner.next();
                return new Token(TokenClass.EQ, line, column);
            } else
                return new Token(TokenClass.ASSIGN, line, column);

        // !=
        if (c == '!' && scanner.peek() == '=') {
            scanner.next();
            return new Token(TokenClass.NE, line, column);
        }

        // < and <=
        if (c == '<')
            if (scanner.peek() == '=') {
                scanner.next();
                return new Token(TokenClass.LE, line, column);
            } else
                return new Token(TokenClass.LT, line, column);

        // > and >=
        if (c == '>')
            if (scanner.peek() == '=') {
                scanner.next();
                return new Token(TokenClass.GE, line, column);
            } else
                return new Token(TokenClass.GT, line, column);


        // operators
        if (c == '+') return new Token(TokenClass.PLUS, line, column);
        if (c == '-') return new Token(TokenClass.MINUS, line, column);
        if (c == '*') return new Token(TokenClass.ASTERIX, line, column);
        if (c == '/') return new Token(TokenClass.DIV, line, column);
        if (c == '%') return new Token(TokenClass.REM, line, column);

        // struct member access
        if (c == '.') return new Token(TokenClass.DOT, line, column);

        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }
}