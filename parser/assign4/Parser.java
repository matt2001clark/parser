import java.util.ArrayList;
import java.util.HashMap;
//import tokenizer.Token;
//import tokenizer.Tokenizer;
/**
 * Parse a Jack source file.
 * 
 * @author Matthew Clark
 */
public class Parser {
    // The tokenizer.
    private final Tokenizer lex;
    private String className = "";
    private String sf = "";
    private ArrayList<String> names = new ArrayList<String>();
    HashMap<String, String> dataTypes = new HashMap<String, String>();
    /**
     * Parse a Jack source file.
     * @param lex The tokenizer.
     */
    public Parser(Tokenizer lex)
    {
        this.lex = lex;

    }

    /**
     * Parse a Jack class file.
     * @throws ParsingFailure on failure.
     */
    public void parseClass()
    {
        //throw new ParsingFailure();
        //class
        Token tType = lex.getTokenType();
        lex.advance();
        System.out.println("<class>\n");
        String kw = String.valueOf(lex.getKeyword());
        //check if lex.keyword is 'class'
        if(kw != null && kw.equals("CLASS")) {
            System.out.println("<keyword> class </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'class'. But encountered: " + kw.toLowerCase());
            throw new ParsingFailure();
        }

        //className
        lex.advance();
        String type = String.valueOf(lex.getTokenType());
        String idfr = lex.getIdentifier();
        String tk = type + " " + idfr + " ";
        String td = String.valueOf(lex.getTokenDetails());
        //check token details
        if(type != null && td.equals(tk)) {
            className = lex.getIdentifier();
            System.out.println("<identifier> " + className + " </identifier>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber()+ ": Expected identifier. But encountered: " + td.toLowerCase());
            throw new ParsingFailure();
        }

        //'{'
        lex.advance();
        if(lex.getSymbol() != '#' && lex.getSymbol() == '{') {
            System.out.println("<symbol> { </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '{'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        //classVarDec
        lex.advance();
        tType = lex.getTokenType();
        if(tType.equals(Token.KEYWORD)) {
            sf = String.valueOf(lex.getKeyword());
            //check if vardec is either static or field
            while(sf.equals("STATIC") || sf.equals("FIELD")){
                tType = lex.getTokenType();
                if(tType.equals(Token.SYMBOL)) {
                    break;
                }
                //start vardec;
                CompileClassVarDec();
                tType = lex.getTokenType();
                //check for keyword
                if(tType.equals(Token.KEYWORD)) { 
                    sf = String.valueOf(lex.getKeyword());
                }
            }
        }

        //subroutineDec
        tType = lex.getTokenType();
        if(tType.equals(Token.KEYWORD)) { 
            String t = String.valueOf(lex.getKeyword());
            //check if subroutine is either constructor, function or method
            while(t != null && (t.equals("CONSTRUCTOR") | t.equals("FUNCTION") | t.equals("METHOD"))) {
                //start subroutine;
                CompileSubroutine();
                lex.advance();
                tType = lex.getTokenType();
                //check for keyword
                if(tType.equals(Token.KEYWORD)) { 
                    t = String.valueOf(lex.getKeyword());
                }
                if(tType.equals(Token.SYMBOL)) {
                    break;
                }
            }
        }
        //}
        tType = lex.getTokenType();
        if(tType.equals(Token.SYMBOL)) {
            if(lex.getSymbol() != '#' && lex.getSymbol() == '}') {
                System.out.println("<symbol> } </symbol>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '}'. But encountered: " + lex.getSymbol());
                throw new ParsingFailure();
            }
        }

        System.out.println("</class>\n");
    }

    public void CompileClassVarDec(){

        System.out.println("<classVarDec>\n");
        sf = String.valueOf(lex.getKeyword());
        Token tType = lex.getTokenType();
        String kind = "";
        String type = "";
        String name = "";
        //static || field
        if(sf != null && (sf.equals("STATIC") || sf.equals("FIELD"))) {
            if(sf.equals("STATIC")){
                kind = String.valueOf(Keyword.STATIC);
                System.out.println("<keyword> " + sf.toLowerCase() + " </keyword>\n");
            }
            else if (sf.equals("FIELD")){
                kind = String.valueOf(Keyword.FIELD);
                System.out.println("<keyword> " + sf.toLowerCase() + " </keyword>\n");
            }
        }
        //constructor || function || method
        else if (sf != null && (sf.equals("CONSTRUCTOR") | sf.equals("FUNCTION") | sf.equals("METHOD"))) {
            CompileSubroutine();
        }
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Unexpected keyword: " + sf.toLowerCase());
            throw new ParsingFailure();
        }

        //type
        lex.advance();
        tType = lex.getTokenType();
        //check for keyword
        if(tType.equals(Token.KEYWORD)){
            String t = String.valueOf(lex.getKeyword());
            if(t != null && (t.equals("INT") | t.equals("CHAR") | t.equals("BOOLEAN"))) {
                type = t;
                System.out.println("<keyword> " + t.toLowerCase() + " </keyword>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected variable type. But encountered: " + t.toLowerCase());
                throw new ParsingFailure();
            }
        }
        //other datatypes
        else{
            String dataType = "";
            String tp = String.valueOf(lex.getTokenType());
            String idfr = lex.getIdentifier();
            String tk = tp + " " + idfr + " ";
            String td = String.valueOf(lex.getTokenDetails());

            if(td != null && td.equals(tk)) {
                dataType = idfr;
                System.out.println("<keyword> " + dataType + " </keyword>\n");

            }
        }

        //varName
        lex.advance();
        String tp2 = String.valueOf(lex.getTokenType());
        String idfr2 = lex.getIdentifier();
        String tk2 = tp2 + " " + idfr2 + " ";
        String td2 = String.valueOf(lex.getTokenDetails());
        if(td2 != null && td2.equals(tk2)) {
            name = idfr2;

            //check if name is already used
            if(names.contains(name)){
                System.out.println("Line " + lex.getLineNumber() + ": Identifier '" + name + "' is already used as a field or variable.");
                throw new ParsingFailure();         
            }
            //add name to declared list
            else {
                names.add(name);
                System.out.println("<identifier> " + name + " </identifier>\n");
                lex.advance();
            }

        }

        //;
        if(lex.getSymbol() != '#' && lex.getSymbol() == ';') {
            System.out.println("<symbol> ; </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " +lex.getLineNumber() + ": Expected symbol ';'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        lex.advance();

        System.out.println("</classVarDec>\n");
    }

    public void CompileSubroutine() {

        System.out.println("<subroutineDec>\n"); 

        //constructor || function || method
        String t = String.valueOf(lex.getKeyword());
        if(t != null && (t.equals("CONSTRUCTOR") || t.equals("FUNCTION") || t.equals("METHOD"))) {
            //subType = t;
            System.out.println("<keyword> " + t.toLowerCase() + " </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'constructor', 'function', or 'method'. But encountered: " + t.toLowerCase());
            throw new ParsingFailure();
        }

        //void | type
        lex.advance();
        Token tType = lex.getTokenType();
        if(tType.equals(Token.KEYWORD)){
            t = String.valueOf(lex.getKeyword());
            if(t != null && (t.equals("VOID") | t.equals("INT") | t.equals("CHAR") | t.equals("BOOLEAN"))) {
                System.out.println("<keyword> " + t.toLowerCase() + " </keyword>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected variable type. But encountered: " + t.toLowerCase());
                throw new ParsingFailure();
            }
        }
        //other datatypes
        else{
            String dataType = "";
            String tp = String.valueOf(lex.getTokenType());
            String idfr = lex.getIdentifier();
            String tk = tp + " " + idfr + " ";
            String td = String.valueOf(lex.getTokenDetails());

            if(td != null && td.equals(tk)) {
                dataType = idfr;
                System.out.println("<keyword> " + dataType + " </keyword>\n");

            }
        }

        //subroutineName
        lex.advance();
        String name = "";
        String tp2 = String.valueOf(lex.getTokenType());
        String idfr2 = lex.getIdentifier();
        String tk2 = tp2 + " " + idfr2 + " ";
        String td2 = String.valueOf(lex.getTokenDetails());
        if(td2 != null && td2.equals(tk2)) {
            name = idfr2;
            names.add(name);
            System.out.println("<identifier> " + name + " </identifier>\n");

        }

        //(
        lex.advance();
        if(lex.getSymbol() != '#' && lex.getSymbol() == '(') {
            System.out.println("<symbol> ( </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '('. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        //parameters
        lex.advance();
        compileParameterList();

        //)
        if(lex.getSymbol() != '#' && lex.getSymbol() == ')') {
            System.out.println("<symbol> ) </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol ')'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        //subroutineBody
        System.out.println("<subroutineBody>\n");

        //{
        lex.advance();
        if(lex.getSymbol() != '#' && lex.getSymbol() == '{') {
            System.out.println("<symbol> { </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '{'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        //varDec*
        lex.advance();
        Token type = lex.getTokenType();
        //check for variables
        if(type.equals(Token.KEYWORD)){
            String kw = String.valueOf(lex.getKeyword());
            while(kw != null && kw.equals("VAR")) {
                compileVarDec();
                lex.advance();
                kw = String.valueOf(lex.getKeyword());
            }
            compileStatements();
        }

        //}
        if(lex.getSymbol() != '#' && lex.getSymbol() == '}') {
            System.out.println("<symbol> } </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '}'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        System.out.println("</subroutineBody>\n");
        System.out.println("</subroutineDec>\n");
    }

    public void compileParameterList() {

        System.out.println("<parameterList>\n"); 
        Token tType = lex.getTokenType();
        //check for datatype
        if(tType != null && (tType.equals(Token.KEYWORD)))
        {
            //type
            String kw = String.valueOf(lex.getKeyword());
            if((tType.equals(Token.KEYWORD)) && (kw.equals("INT") | kw.equals("CHAR") | kw.equals("BOOLEAN"))) {
                System.out.println("<keyword> " + kw.toLowerCase() + " </keyword>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected variable type. But encountered: " + kw.toLowerCase());
                throw new ParsingFailure();
            }
        }
        //other datatypes
        else if (tType != null && (tType.equals(Token.IDENTIFIER))){
            String dataType = "";
            String tp0 = String.valueOf(lex.getTokenType());
            String idfr0 = lex.getIdentifier();
            String tk0 = tp0 + " " + idfr0 + " ";
            String td0 = String.valueOf(lex.getTokenDetails());

            if(td0 != null && td0.equals(tk0)) {
                dataType = idfr0;
                System.out.println("<keyword> " + dataType + " </keyword>\n");

            }
        }

        //varName
        if (tType != null && (tType.equals(Token.IDENTIFIER) || tType.equals(Token.KEYWORD))){
            lex.advance();
            String name = "";
            String tp = String.valueOf(lex.getTokenType());
            String idfr = lex.getIdentifier();
            String tk = tp + " " + idfr + " ";
            String td = String.valueOf(lex.getTokenDetails());
            if(td != null && td.equals(tk)) {
                name = idfr;
                names.add(name);
                System.out.println("<identifier> " + name + " </identifier>\n");
                lex.advance();
            }
        }
        //,
        while(lex.getSymbol() != '#' && lex.getSymbol() == ',')
        {
            System.out.println("<symbol> , </symbol>\n");
            lex.advance();
            //datatype
            tType = lex.getTokenType();
            if(tType.equals(Token.KEYWORD)){
                String kw2 = String.valueOf(lex.getKeyword());
                if(tType.equals(Token.KEYWORD) && (kw2.equals("INT") | kw2.equals("CHAR") | kw2.equals("BOOLEAN"))) {
                    System.out.println("<keyword> " + kw2.toLowerCase() + " </keyword>\n");
                }
                //error message
                else {
                    System.out.println("Line " + lex.getLineNumber() + ": Expected variable type. But encountered: " + kw2.toLowerCase());
                    throw new ParsingFailure();
                }
            }
            //other datatypes
            else{
                String dataType2 = "";
                String tp2 = String.valueOf(lex.getTokenType());
                String idfr2 = lex.getIdentifier();
                String tk2 = tp2 + " " + idfr2 + " ";
                String td2 = String.valueOf(lex.getTokenDetails());

                if(td2 != null && td2.equals(tk2)) {
                    dataType2 = idfr2;
                    System.out.println("<keyword> " + dataType2 + " </keyword>\n");

                }
            }

            //varName
            lex.advance();
            String name3 = "";
            String tp3 = String.valueOf(lex.getTokenType());
            String idfr3 = lex.getIdentifier();
            String tk3 = tp3 + " " + idfr3 + " ";
            String td3 = String.valueOf(lex.getTokenDetails());
            if(td3 != null && td3.equals(tk3)) {
                name3 = idfr3;
                names.add(name3);
                System.out.println("<identifier> " + name3 + " </identifier>\n");
                lex.advance();

            }

        }

        System.out.println("</parameterList>\n");

    }

    public void compileVarDec() {

        System.out.println("<varDec>\n");

        String kw = String.valueOf(lex.getKeyword());

        //variable
        if(kw != null && kw.equals("VAR")) {
            System.out.println("<keyword> " + kw.toLowerCase() + " </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'var'. But encountered: " + kw.toLowerCase());
            throw new ParsingFailure();
        }

        //datatype
        lex.advance();

        Token tType = lex.getTokenType();
        String dataType = "";
        if(tType != null && (tType.equals(Token.KEYWORD)))
        {
            //datatype
            dataType = String.valueOf(lex.getKeyword());
            if((tType.equals(Token.KEYWORD)) && (dataType.equals("INT") | dataType.equals("CHAR") | dataType.equals("BOOLEAN"))) {
                System.out.println("<keyword> " + dataType.toLowerCase() + " </keyword>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected variable type. But encountered: " + dataType.toLowerCase());
                throw new ParsingFailure();
            }
            //other datatype
        }
        else if (tType != null && (tType.equals(Token.IDENTIFIER))){
            String tp0 = String.valueOf(lex.getTokenType());
            String idfr0 = lex.getIdentifier();
            String tk0 = tp0 + " " + idfr0 + " ";
            String td0 = String.valueOf(lex.getTokenDetails());

            if(td0 != null && td0.equals(tk0)) {
                dataType = idfr0;
                System.out.println("<keyword> " + dataType + " </keyword>\n");

            }
        }
        //varName
        lex.advance();
        String name2 = "";
        String tp2 = String.valueOf(lex.getTokenType());
        String idfr2 = lex.getIdentifier();
        String tk2 = tp2 + " " + idfr2 + " ";
        String td2 = String.valueOf(lex.getTokenDetails());
        if(td2 != null && td2.equals(tk2)) {
            name2 = idfr2;

            //check if name is already used
            if(names.contains(name2)){
                System.out.println("Line " + lex.getLineNumber() + ": Identifier '" + name2 + "' is already used as a field or variable.");
                throw new ParsingFailure();         
            }
            //declare
            else {
                names.add(name2);
                dataTypes.put(dataType, name2);
                System.out.println("<identifier> " + name2 + " </identifier>\n");
            }

        }

        // , varName
        lex.advance();
        while(lex.getSymbol() != '#' && lex.getSymbol() == ',') {

            System.out.println("<symbol> , </symbol>\n");

            lex.advance();
            String name3 = "";
            String tp3 = String.valueOf(lex.getTokenType());
            String idfr3 = lex.getIdentifier();
            String tk3 = tp3 + " " + idfr3 + " ";
            String td3 = String.valueOf(lex.getTokenDetails());

            if(td3 != null && td3.equals(tk3)) {
                name3 = idfr3;

                //check if name is already used
                if(names.contains(name3)){
                    System.out.println("Line " + lex.getLineNumber() + ": Identifier '" + name3 + "' is already used as a field or variable.");
                    throw new ParsingFailure();         
                }
                //declare
                else {
                    names.add(name3);
                    dataTypes.put(dataType, name3);
                    System.out.println("<identifier> " + name3 + " </identifier>\n");
                    lex.advance();
                }

            }

        }

        //;
        if(lex.getSymbol() != '#' && lex.getSymbol() == ';') {
            System.out.println("<symbol> ; </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol ';'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        System.out.println("</varDec>\n");
    }

    public void compileStatements() {

        System.out.println("<statements>\n");
        String t = String.valueOf(lex.getKeyword());
        Token type = lex.getTokenType();
        //check for statement type
        while(t != null && (t.equals("LET") || t.equals("IF") || t.equals("WHILE") || t.equals("DO") || t.equals("RETURN")) && type.equals(Token.KEYWORD)) {
            if(t.equals("LET")) {
                compileLet();   
                lex.advance();
            }
            else if(t.equals("IF")){
                compileIf();
            }
            else if(t.equals("WHILE")) {
                compileWhile();
                lex.advance();
            }
            else if(t.equals("DO")) {
                compileDo();
                lex.advance();
            }
            else if(t.equals("RETURN")) {
                compileReturn();
                lex.advance();
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'let', 'if', 'while', 'do', or 'return'. But encountered: " + t.toLowerCase());
                throw new ParsingFailure();
            }
            //set new statement type
            if(lex.getTokenType().equals(Token.KEYWORD)){
                t = String.valueOf(lex.getKeyword());
            }
            type = lex.getTokenType();
        }

        System.out.println("</statements>\n");

    }

    public void compileLet() {

        System.out.println("<letStatement>\n");
        String t = String.valueOf(lex.getKeyword());

        //check for let
        if(t != null && t.equals("LET")) {
            System.out.println("<keyword> let </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'let'. But encountered: " + t.toLowerCase());
            throw new ParsingFailure();
        }

        //varName
        lex.advance();
        String name = "";
        String tp = String.valueOf(lex.getTokenType());
        String idfr = lex.getIdentifier();
        String tk = tp + " " + idfr + " ";
        String td = String.valueOf(lex.getTokenDetails());
        if(td != null && td.equals(tk)) {
            name = idfr;

            //check if name is already used
            if(names.contains(name)){
                System.out.println("<identifier> " + name + " </identifier>\n");        
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Variable " + t.toLowerCase() + " not declared");
                throw new ParsingFailure(); 
            }

        }

        // [
        lex.advance();
        if(lex.getSymbol() != '#' && lex.getSymbol() == '[') {

            System.out.println("<symbol> [ </symbol>\n");
            lex.advance();
            compileExpression();

            // ]
            if(lex.getSymbol() != '#' && lex.getSymbol() == ']') {
                System.out.println("<symbol> ] </symbol>\n"); 
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol ']'. But encountered: " + lex.getSymbol());
                throw new ParsingFailure();
            }
            lex.advance();

        }

        // = 
        if(lex.getSymbol() != '#' && lex.getSymbol() == '=') {
            System.out.println("<symbol> = </symbol>\n");
            lex.advance();
            compileExpression();
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '='. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        //;
        if(lex.getSymbol() != '#' && lex.getSymbol() == ';') {
            System.out.println("<symbol> ; </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol ';'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        System.out.println("</letStatement>\n");
    }

    public void compileIf() {

        System.out.println("<ifStatement>\n"); 

        //check for if
        if(lex.getKeyword() != null && lex.getKeyword().equals(Keyword.IF)) {
            System.out.println("<keyword> if </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'if'. But encountered: " + lex.getKeyword());
            throw new ParsingFailure();
        }

        lex.advance();

        //(
        if(lex.getSymbol() != '#' && lex.getSymbol() == '(') {
            System.out.println("<symbol> ( </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '('. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        lex.advance();
        compileExpression();

        // )
        Token type = lex.getTokenType();
        if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ')')) {
            System.out.println("<symbol> ) </symbol>\n");
            lex.advance();

        }

        // & or |
        type = lex.getTokenType();
        char sym = lex.getSymbol();

        if(type.equals(Token.SYMBOL) && ((lex.getSymbol() == '&') || (lex.getSymbol() == '|'))) {
            while(sym == '&' || sym =='|'){

                System.out.println("<symbol> " + lex.getSymbol() + " </symbol>\n");
                lex.advance(); 

                type = lex.getTokenType();
                if(lex.getSymbol() != '#' && lex.getSymbol() == '(') {
                    System.out.println("<symbol> ( </symbol>\n");
                    compileExpression();

                    type = lex.getTokenType();
                    if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ')')) {
                        System.out.println("<symbol> ) </symbol>\n");
                        lex.advance();

                    }
                }
                sym = lex.getSymbol();
            }

            type = lex.getTokenType();
            if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ')')) {
                System.out.println("<symbol> ) </symbol>\n");
                lex.advance();

            }

        }

        //{
        if(lex.getSymbol() != '#' && lex.getSymbol() == '{') {
            System.out.println("<symbol> { </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '{'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        lex.advance();

        compileStatements();

        //}
        if(lex.getSymbol() != '#' && lex.getSymbol() == '}') {
            System.out.println("<symbol> } </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '}'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        lex.advance();

        //else
        type = lex.getTokenType();
        if(type.equals(Token.KEYWORD)){
            if(lex.getKeyword() != null && lex.getKeyword().equals(Keyword.ELSE)) {
                System.out.println("<keyword> else </keyword>\n");

                //{
                lex.advance();
                if(lex.getSymbol() != '#' && lex.getSymbol() == '{') {
                    System.out.println("<symbol> { </symbol>\n");
                }
                //error message
                else {
                    System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '{'. But encountered: " + lex.getSymbol());
                    throw new ParsingFailure();
                }

                lex.advance();
                compileStatements();

                //}
                if(lex.getSymbol() != '#' && lex.getSymbol() == '}') {
                    System.out.println("<symbol> } </symbol>\n");
                }
                //error message
                else {
                    System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '}'. But encountered: " + lex.getSymbol());
                    throw new ParsingFailure();
                }

                lex.advance();

            }

        }
        System.out.println("</ifStatement>\n");
    }

    public void compileWhile() {

        System.out.println("<whileStatement>\n"); 
        //check for while
        if(lex.getKeyword() != null && lex.getKeyword().equals(Keyword.WHILE)) {
            System.out.println("<keyword> while </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'while'. But encountered: " + lex.getKeyword());
            throw new ParsingFailure();
        }

        //(
        lex.advance();
        Token t = lex.getTokenType();
        if(t.equals(Token.SYMBOL)){
            if(lex.getSymbol() != '#' && lex.getSymbol() == '(') {
                System.out.println("<symbol> ( </symbol>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '('. But encountered: " + lex.getSymbol());
                throw new ParsingFailure();
            }
        }

        //compile expression
        lex.advance();
        compileExpression();

        // )
        Token type = lex.getTokenType();
        if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ')')) {
            System.out.println("<symbol> ) </symbol>\n");
            lex.advance();

        }

        //{

        t = lex.getTokenType();
        if(t.equals(Token.SYMBOL)){
            if(lex.getSymbol() != '#' && lex.getSymbol() == '{') {
                System.out.println("<symbol> { </symbol>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '{'. But encountered: " + lex.getSymbol());
                throw new ParsingFailure();
            }
        }

        lex.advance();
        compileStatements();

        //}

        t = lex.getTokenType();
        if(t.equals(Token.SYMBOL)){
            if(lex.getSymbol() != '#' && lex.getSymbol() == '}') {
                System.out.println("<symbol> } </symbol>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '}'. But encountered: " + lex.getSymbol());
                throw new ParsingFailure();
            }
        }

        System.out.println("</whileStatement>\n");

    }

    public void compileDo() {

        System.out.println("<doStatement>\n");

        String k = String.valueOf(lex.getKeyword());
        //check for do
        if(k != null && k.equals("DO")) {
            System.out.println("<keyword> do </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'do'. But encountered: " + k.toLowerCase());
            throw new ParsingFailure();
        }
        lex.advance();

        //subroutineName | varName
        Token t = lex.getTokenType();
        if(t != null && (t.equals(Token.IDENTIFIER))){
            String name = "";
            String tp = String.valueOf(lex.getTokenType());
            String idfr = lex.getIdentifier();
            String tk = tp + " " + idfr + " ";
            String td = String.valueOf(lex.getTokenDetails());
            if(td != null && td.equals(tk)) {
                name = idfr;
                System.out.println("<identifier> " + name + " </identifier>\n");        
            } 
        }
        lex.advance();
        t = lex.getTokenType();
        if(t != null && (t.equals(Token.SYMBOL))){
            // (
            if(lex.getSymbol() != '#' && lex.getSymbol() == '('){
                System.out.println("<symbol> ( </symbol>\n"); 
            }
            // .
            else if(lex.getSymbol() != '#' && lex.getSymbol() == '.'){
                System.out.println("<symbol> . </symbol>\n");

                //subroutineName
                lex.advance();
                t = lex.getTokenType();
                if(t != null && (t.equals(Token.IDENTIFIER))){
                    String name = "";
                    String tp = String.valueOf(lex.getTokenType());
                    String idfr = lex.getIdentifier();
                    String tk = tp + " " + idfr + " ";
                    String td = String.valueOf(lex.getTokenDetails());
                    if(td != null && td.equals(tk)) {
                        name = idfr;
                        System.out.println("<identifier> " + name + " </identifier>\n"); 

                    } 
                }
                else {
                    System.out.println("Line " + lex.getLineNumber() + ": Expected identifier but encountered " + t);
                    throw new ParsingFailure(); 
                }
                // (
                lex.advance();
                if (lex.getSymbol() != '#' && lex.getSymbol() == '('){
                    System.out.println("<symbol> ( </symbol>\n"); 
                }
                //error message
                else {
                    System.out.println("Line " + lex.getLineNumber() +  ": Expected symbol '(' but encountered: " +  lex.getSymbol());
                    throw new ParsingFailure(); 
                }

            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() +  ": Expected symbol '(' or '.' but encountered: " +  lex.getSymbol());
                throw new ParsingFailure(); 
            }

            lex.advance();
            compileExpressionList();

            // )
            Token type = lex.getTokenType();
            if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ')')) {
                System.out.println("<symbol> ) </symbol>\n");
                lex.advance();

            }

            //;
            if(lex.getSymbol() != '#' && lex.getSymbol() == ';') {
                System.out.println("<symbol> ; </symbol>\n");
            }
            //error message
            else {
                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol ';'. But encountered: " + lex.getSymbol());
                throw new ParsingFailure();
            }

            System.out.println("</doStatement>\n");

        }

    }

    public void compileReturn() {
        System.out.println("<returnStatement>\n");

        String k = String.valueOf(lex.getKeyword());
        //check for return
        if(k != null && k.equals("RETURN")) {
            System.out.println("<keyword> return </keyword>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected keyword 'return'. But encountered: " + k.toLowerCase());
            throw new ParsingFailure();
        }
        // object to return
        lex.advance();
        Token t = lex.getTokenType();
        if(t != null && (t.equals(Token.INT_CONST) || t.equals(Token.STRING_CONST) || t.equals(Token.KEYWORD) || 
            t.equals(Token.IDENTIFIER)) || (t.equals(Token.SYMBOL)) && (lex.getSymbol() == '(' || lex.getSymbol() == '-' || lex.getSymbol() == '~' )) {
            compileExpression();
        }

        //;
        if(lex.getSymbol() != '#' && lex.getSymbol() == ';') {
            System.out.println("<symbol> ; </symbol>\n");
        }
        //error message
        else {
            System.out.println("Line " + lex.getLineNumber() + ": Expected symbol ';'. But encountered: " + lex.getSymbol());
            throw new ParsingFailure();
        }

        System.out.println("</returnStatement>\n");

    }

    public void compileExpression() {
        System.out.println("<expression>\n");
        Token type = lex.getTokenType();

        //first term
        compileTerm();
        type = lex.getTokenType();

        if(type.equals(Token.SYMBOL)){
            char op = lex.getSymbol();
            while(op != '#' && (op == '+' || op == '-' || op == '*' || op == '/' || op == '&' || op == '|' || op == '<' ||
                op == '>' || op == '=') && type.equals(Token.SYMBOL)) {
                //operator
                System.out.println("<symbol> " + op + " </symbol>\n");

                lex.advance();
                type = lex.getTokenType();

                if(type.equals(Token.SYMBOL)) {
                    op = lex.getSymbol();
                }

                //second term 
                compileTerm();

                type = lex.getTokenType();

                //set new symbol
                if(type.equals(Token.SYMBOL)) {
                    op = lex.getSymbol();
                }

            }
        }
        System.out.println("</expression>\n");
    }

    public void compileTerm(){
        System.out.println("<term>\n");
        Token type = lex.getTokenType();

        if(type != null) {

            //intConst
            if (type.equals(Token.INT_CONST)){
                int iv = lex.getIntval();
                System.out.println("<integerConstant> " + iv + " </integerConstant>\n");
                lex.advance();

            }
            //StringConst
            else if (type.equals(Token.STRING_CONST)){
                String sv = lex.getStringVal();
                System.out.println("<stringConstant> " + sv + " </stringConstant>\n");
                lex.advance();

            }
            //KeywordConst
            else if(type.equals(Token.KEYWORD)){
                String kw = String.valueOf(lex.getKeyword());
                System.out.println("<keyword> " + kw.toLowerCase() + " </keyword>\n");
                lex.advance();
            }
            //varName, subroutineName and className
            else if(type.equals(Token.IDENTIFIER)) {

                //check name
                String name = "";
                String tp = String.valueOf(lex.getTokenType());
                String idfr = lex.getIdentifier();
                String tk = tp + " " + idfr + " ";
                String td = String.valueOf(lex.getTokenDetails());

                if(td != null && td.equals(tk)) {
                    //varName
                    name = idfr;
                    System.out.println("<identifier> " + name + " </identifier>\n");

                    //varName []
                    lex.advance();
                    type = lex.getTokenType();
                    // [
                    if(type.equals(Token.SYMBOL) && (lex.getSymbol() == '[')) {

                        System.out.println("<symbol> [ </symbol>\n");
                        lex.advance();
                        compileExpression();

                        // ]
                        if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ']')) {
                            System.out.println("<symbol> ] </symbol>\n");
                            lex.advance();
                        }
                        

                        
                    }
                    //subroutine
                    else if(lex.getSymbol() != '#' && (lex.getSymbol() == '(' || lex.getSymbol() == '.')) {

                        // (
                        if(lex.getSymbol() == '(') {
                            System.out.println("<symbol> ( </symbol>\n");
                        }
                        // .
                        else if(lex.getSymbol() == '.') {
                            System.out.println("<symbol> . </symbol>\n");

                            //subroutineName
                            lex.advance();
                            type = lex.getTokenType();
                            if(type.equals(Token.IDENTIFIER)) {
                                String name2 = "";
                                String tp2 = String.valueOf(lex.getTokenType());
                                String idfr2 = lex.getIdentifier();
                                String tk2 = tp2 + " " + idfr2 + " ";
                                String td2 = String.valueOf(lex.getTokenDetails());

                                if(td2 != null && td2.equals(tk2)) {
                                    name2 = idfr2;
                                    System.out.println("<keyword> " + name2 + " </keyword>\n");
                                }
                            }

                            lex.advance();

                            // (
                            if(lex.getSymbol() == '(') {
                                System.out.println("<symbol> ( </symbol>\n");
                                lex.advance();
                            }
                            //error message
                            else{
                                System.out.println("Line " + lex.getLineNumber() + ": Expected symbol '('. But encountered: " + lex.getSymbol());
                                throw new ParsingFailure();
                            }

                        }

                        compileExpressionList();

                        //)
                        type = lex.getTokenType();
                        if(type.equals(Token.SYMBOL) && lex.getSymbol() == ')') {
                            System.out.println("<symbol> ) </symbol>\n");
                            lex.advance();
                        }

                    }

                }
                // (expression)
                else if(type.equals(Token.SYMBOL) && (lex.getSymbol() == '(')) {
                    System.out.println("<symbol> ( </symbol>\n");
                    lex.advance();
                    compileExpression();

                    if(type.equals(Token.SYMBOL) && (lex.getSymbol() == ')')) {
                        System.out.println("<symbol> ) </symbol>\n");
                    }

                    lex.advance();

                }
            }
            // - or ~
            else if(type.equals(Token.SYMBOL) && ((lex.getSymbol() == '-') || (lex.getSymbol() == '~'))){
                System.out.println("<symbol> " + lex.getSymbol() + " </symbol>\n");
                lex.advance();

                // (
                type = lex.getTokenType();
                if(type.equals(Token.SYMBOL) && lex.getSymbol() == '(') {
                    System.out.println("<symbol> ( </symbol>\n");
                    lex.advance();
                }

                compileExpression();

                //)
                type = lex.getTokenType();
                if(type.equals(Token.SYMBOL) && lex.getSymbol() == ')') {
                    System.out.println("<symbol> ) </symbol>\n");
                    lex.advance();
                }
            }
            else if(type.equals(Token.SYMBOL) && lex.getSymbol() == '('){

                System.out.println("<symbol> ( </symbol>\n");
                lex.advance();

                compileExpression();

                type = lex.getTokenType();
                if(type.equals(Token.SYMBOL) && lex.getSymbol() == ')') {
                    System.out.println("<symbol> ) </symbol>\n");
                    lex.advance();
                }

            }
            System.out.println("</term>\n");
        }
    }

    public void compileExpressionList() {

        System.out.println("<expressionList>\n");  
        Token t = lex.getTokenType();
        if(t != null && (t.equals(Token.INT_CONST) || t.equals(Token.STRING_CONST) || t.equals(Token.KEYWORD) || 
            t.equals(Token.IDENTIFIER)) || (t.equals(Token.SYMBOL)) && (lex.getSymbol() == '(' || lex.getSymbol() == '-' || lex.getSymbol() == '~' )) {
            compileExpression();

            while(lex.getSymbol() != '#' && lex.getSymbol() == ',') {
                System.out.println("<symbol> , </symbol>\n");
                lex.advance();
                compileExpression();
            }
        }
        System.out.println("</expressionList>\n");
    }

    /**
     * A ParsingFailure exception is thrown on any form of
     * error detected during the parse.
     */
    public static class ParsingFailure extends RuntimeException
    {

    }

}
