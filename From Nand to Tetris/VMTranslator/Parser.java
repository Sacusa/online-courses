package VMTranslator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

/**
 * Parses a Hack VM file into its lexical components.
 * The parsed lexical components can then be converted into respective assembly code.
 * 
 * @author sacusa
 *
 */
public class Parser {

    // Datatype definition
    // Represents the type of VM command. Also includes a representation for invalid commands.
    enum CommandType {ARITHMETIC, PUSH, POP, LABEL, GOTO, IF, FUNCTION, RETURN, CALL, INVALID};

    private Integer currentLine;
    private final List<String> commands;
    // Abstraction function:
    //   represnt the VM code file, along with information of current line number, i.e. the line
    //   being parsed.
    // Rep invariant:
    //   currentLine >= -1
    // Safety from rep exposure:
    //   neither of the rep values are returned

    /**
     * Reads the filename into the commands List.
     * Requires the filename to be a valid Hack VM file.
     * 
     * @param filename  The Hack VM code file name.
     */
    public Parser(String filename) {
        currentLine = -1;
        commands = new ArrayList<String>();

        // read the input file into commands
        File inputFile = new File(filename);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String text = null;

            // regex pattern to remove comments
            Pattern commentPattern = Pattern.compile("\\s*//(.*)");

            while ((text = reader.readLine()) != null) {
                Matcher m = commentPattern.matcher(text);
                
                // add only non-empty, non-comment lines
                if (!m.matches() && !text.isEmpty()) {
                    commands.add(text);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        checkRep();
    }

    /**
     * Checks that the rep invariant(documented above) is maintained.
     */
    private void checkRep() {
        if (currentLine < -1) {
            throw new RuntimeException("invalid line number%n");
        }
    }

    /**
     * Returns true if there are more commands in the opened file,
     * otherwise false.
     * 
     * @return  True, if more commands exist, else false.
     */
    public boolean hasMoreCommands() {
        return ((currentLine + 1) < commands.size());
    }

    /**
     * If the Parser is not at the last line, advances the line number.
     * Else, does nothing.
     */
    public void advance() {
        if (hasMoreCommands()) {
            ++currentLine;
        }
        
        checkRep();
    }

    /**
     * Returns the line number currently being parsed.
     * @return  Current line number.
     */
    public Integer line() {
        return new Integer(currentLine);
    }
    
    /**
     * Returns the current command being parsed.
     * @return  Current command.
     */
    public String command() {
        return commands.get(currentLine);
    }
    /**
     * Returns the type of the current command.
     * 
     * Command types are documented above, as enum type CommandType.
     * If the command is an invalid command, returns CommandType.INVALID.
     * 
     * @return  CommandType of the current command.
     */
    public CommandType commandType() {
        // regex strings for error detection and command type determination
        final String inlineCommentPattern = "(\\s*(//.*)?)";
        final String tablesPattern = "(local|argument|this|that|constant|static|temp|pointer)";
        final String arithmeticPattern = "(add|sub|neg|eq|gt|lt|and|or|not)" + inlineCommentPattern;
        final String pushPattern = "push\\s+" + tablesPattern + "\\s+\\d+" + inlineCommentPattern;
        final String popPattern = "pop\\s+" + tablesPattern + "\\s+\\d+" + inlineCommentPattern;
        final String labelPattern = "label\\s+[^\\s]+" + inlineCommentPattern;
        final String gotoPattern = "goto\\s+[^\\s]+" + inlineCommentPattern;
        final String ifgotoPattern = "if-goto\\s+[^\\s]+" + inlineCommentPattern;
        final String functionPattern = "function\\s+[^\\s]+\\s+\\d+" + inlineCommentPattern;
        final String callPattern = "call\\s+[^\\s]+\\s+\\d+" + inlineCommentPattern;
        final String returnPattern = "return" + inlineCommentPattern;

        String currentCommand = commands.get(currentLine);

        // check if arithmetic command
        if (Pattern.matches(arithmeticPattern, currentCommand)) {
            return CommandType.ARITHMETIC;
        }

        // check if push command
        if (Pattern.matches(pushPattern, currentCommand)) {
            return CommandType.PUSH;
        }

        // check if pop command
        if (Pattern.matches(popPattern, currentCommand)) {
            return CommandType.POP;
        }
        
        // check if label
        if (Pattern.matches(labelPattern, currentCommand)) {
            return CommandType.LABEL;
        }
        
        // check if goto command
        if (Pattern.matches(gotoPattern, currentCommand)) {
            return CommandType.GOTO;
        }
        
        // check if if-goto command
        if (Pattern.matches(ifgotoPattern, currentCommand)) {
            return CommandType.IF;
        }
        
        // check if functiond definition
        if (Pattern.matches(functionPattern, currentCommand)) {
            return CommandType.FUNCTION;
        }
        
        // check if call command
        if (Pattern.matches(callPattern, currentCommand)) {
            return CommandType.CALL;
        }
        
        // check if return command
        if (Pattern.matches(returnPattern, currentCommand)) {
            return CommandType.RETURN;
        }

        // no matching pattern, invalid command
        return CommandType.INVALID;
    }

    /**
     * Returns the first argument of the current command. If the command is an arithmetic command,
     * returns the command instead.
     * 
     * SHOULD NOT be called if the current command is CommandType.RETURN.
     * 
     * @return  Command itself, in case of arithmetic command. The first argument of the command
     *          otherwise.
     */
    public String arg1() {
        String currentCommand = commands.get(currentLine);
        String [] tokens = currentCommand.split(" ");

        // if arithmetic command, return the command itself
        if (commandType() == CommandType.ARITHMETIC) {
            return tokens[0];
        }

        // else, return the first argument
        return tokens[1];
    }

    /**
     * Returns the second argument of the current command. Should only be called if the current
     * command is one of the following:
     * 
     * - CommandType.PUSH
     * - CommandType.POP
     * - CommandType.FUNCTION
     * - CommandType.CALL
     * 
     * @return  The second argument of the current command, subject to above conditions.
     */
    public Integer arg2() {
        String [] tokens = commands.get(currentLine).split("\\s");
        return Integer.parseInt(tokens[2]);
    }

}
