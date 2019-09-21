package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

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
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
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

        // delimiters
        if (c == '{')
            return new Token(TokenClass.LBRA, line, column);
        if (c == '}')
            return new Token(TokenClass.RBRA, line, column);
        if (c == '(')
            return new Token(TokenClass.LPAR, line, column);
        if (c == ')')
            return new Token(TokenClass.RPAR, line, column);
        if (c == '[')
            return new Token(TokenClass.LSBR, line, column);
        if (c == ']')
            return new Token(TokenClass.RSBR, line, column);
        if (c == ';')
            return new Token(TokenClass.SC, line, column);
        if (c == ',')
            return new Token(TokenClass.COMMA, line, column);

        // comparisons
        if (c == '<')
            if (scanner.peek() == '=') {
                c = scanner.next();
                return new Token(TokenClass.LE, line, column);
            } else
                return new Token(TokenClass.LT, line, column);


        // operators
        if (c == '+')
            return new Token(TokenClass.PLUS, line, column);
        if (c == '-')
            return new Token(TokenClass.MINUS, line, column);
        if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);
        // div
        if (c == '%')
            return new Token(TokenClass.REM, line, column);

        // ... to be completed


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }


}
