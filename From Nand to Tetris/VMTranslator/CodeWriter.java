package VMTranslator;

import java.io.*;
import java.util.*;

/**
 * Converts Hack VM code to assembly code, writing it to an output file.
 * 
 * Requires the VM code to be broken down into its lexical elements, like the way done by Parser
 * in this package.
 */
public class CodeWriter {

    private final BufferedWriter outputFile;
    private final String filename;
    private String currentFunction;
    private static Integer labelCount = 0;
    private static Integer functionReturnCount = 0;
    // Abstraction function:
    //   represent information about the VM code, along with the output file to which the converted
    //   VM code is being written into
    // Rep invariant:
    //   filename is not empty; labelCount >= 0; functionReturnCount >= 0
    // Safety from rep exposure:
    //   all functions have void return type

    /**
     * Initializes a new CodeWriter Object with the output file named filename.
     * 
     * @param filename  The name of output file to which converted assembly code will be written.
     * @throws IOException
     */
    public CodeWriter(String filename) throws IOException {
        // set this.filename to the actual filename only, not the whole path
        String [] tokens = filename.split("/");
        this.filename = tokens[tokens.length - 1].replace(".asm", "");
        
        outputFile = new BufferedWriter(new FileWriter(new File(filename)));
        currentFunction = "";
        checkRep();
    }

    /**
     * Maintains the rep invariant, as documented above.
     */
    private void checkRep() {
        if (filename.isEmpty() || (labelCount < 0) || (functionReturnCount < 0)) {
            throw new RuntimeException("invalid output rep");
        }
    }

    /**
     * Writes the initialization code, i.e.
     * 
     * SP = 256
     * call Sys.init
     * 
     * Here, Sys.init is simply called and no value is saved.
     */
    public void writeInit() {
        String assemblyCode = "@256" + "\n"
                + "D=A" + "\n"
                + "@SP" + "\n"
                + "M=D" + "\n";

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // call Sys.init 0
        writeCall(Arrays.asList("Sys.init", "0"));
    }

    /**
     * Converts the passed arithmetic command into corresponding assembly code.
     * Does nothing if the assembly code is invalid.
     * 
     * @param command  Arithmetic command to convert.
     */
    public void writeArithmetic(String command) {
        // a HashMap for storing the operators of ALU operations
        // e.g. '+' for add
        Map<String, String> aluOperations = new HashMap<String, String>();
        aluOperations.put("add", "+");
        aluOperations.put("sub", "-");
        aluOperations.put("and", "&");
        aluOperations.put("or", "|");

        // a HashMap for storing the jump directive for comparison operations
        // e.g. JEQ for eq
        Map<String, String> compareOperations = new HashMap<String, String>();
        compareOperations.put("eq", "JEQ");
        compareOperations.put("gt", "JGT");
        compareOperations.put("lt", "JLT");

        // a HashMap for storing the single operand operators
        // e.g. '!' for not
        Map<String, String> singleOperandOperations = new HashMap<String, String>();
        singleOperandOperations.put("neg", "-");
        singleOperandOperations.put("not", "!");

        // add, as a comment, the VM line being parsed
        String assemblyCode = "// " + command + "\n";

        switch(command) {
        case "add":
        case "sub":
        case "and":
        case "or":
            assemblyCode += "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "D=M" + "\n"
                    + "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "D=M" + aluOperations.get(command) + "D" + "\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            break;

        case "eq":
        case "gt":
        case "lt":
            assemblyCode += "@SP" + "\n"
                    + "M=M-1"+ "\n"
                    + "A=M" + "\n"
                    + "D=M" + "\n"
                    + "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "D=M-D" + "\n"
                    + "@" + filename + "." + command.toUpperCase() + "$TRUE." + labelCount + "\n"
                    + "D;" + compareOperations.get(command) + "\n"
                    + "D=0" + "\n"
                    + "@" + filename + "." + command.toUpperCase() + "$END." + labelCount + "\n"
                    + "0;JMP" + "\n"
                    + "(" + filename + "." + command.toUpperCase() + "$TRUE." + labelCount + ")\n"
                    + "D=-1" + "\n"
                    + "(" + filename + "." + command.toUpperCase() + "$END." + labelCount + ")\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            ++labelCount;
            break;

        case "neg":
        case "not":
            assemblyCode += "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "D=M" + "\n"
                    + "D=" + singleOperandOperations.get(command) + "D" + "\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            break;
        }

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkRep();
    }

    /**
     * Converts the push instruction with the passed arguments in args into corresponding assembly
     * code.
     * 
     * Requires args.size = 2; args must contain valid push arguments, segment followed by value.
     * 
     * @param args  A List containing the two arguments to push.
     */
    public void writePush(List<String> args) {
        // extract the segment and index
        final String segment = args.get(0);
        final String index = args.get(1);

        // a HashMap for segment pointers
        // e.g. LCL for local, ARG for argument
        Map<String, String> segmentPointer = new HashMap<String, String>();
        segmentPointer.put("local", "LCL");
        segmentPointer.put("argument", "ARG");
        segmentPointer.put("this", "THIS");
        segmentPointer.put("that", "THAT");
        segmentPointer.put("pointer0", "THIS");
        segmentPointer.put("pointer1", "THAT");

        // add, as a comment, the VM line being parsed
        String assemblyCode = "// push " + segment + " " + index + "\n";

        switch(segment) {
        case "constant":
            assemblyCode += "@" + index + "\n"
                    + "D=A" + "\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            break;

        case "local":
        case "argument":
        case "this":
        case "that":
            assemblyCode += "@" + index + "\n"
                    + "D=A" + "\n"
                    + "@" + segmentPointer.get(segment) + "\n"
                    + "A=D+M" + "\n"
                    + "D=M" + "\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            break;

        case "pointer":
        case "static":
            if (segment.equals("pointer")) {
                assemblyCode += "@" + segmentPointer.get(segment + index) + "\n";
            }
            else {
                assemblyCode += "@" + filename + "." + index + "\n";
            }
            assemblyCode += "D=M" + "\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            break;

        case "temp":
            assemblyCode += "@" + index + "\n"
                    + "D=A" + "\n"
                    + "@5" + "\n"
                    + "A=A+D" + "\n"
                    + "D=M" + "\n"
                    + "@SP" + "\n"
                    + "A=M" + "\n"
                    + "M=D" + "\n"
                    + "@SP" + "\n"
                    + "M=M+1" + "\n";
            break;
        }

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkRep();
    }

    /**
     * Converts the pop instruction with the passed arguments in args into corresponding assembly
     * code.
     * 
     * Requires args.size = 2; args must contain valid pop arguments, segment followed by value.
     * 
     * @param args  A List containing the two arguments to push.
     */
    public void writePop(List<String> args) {
        // extract the segment and index
        final String segment = args.get(0);
        final String index = args.get(1);

        // a HashMap for segment pointers
        // e.g. LCL for local, ARG for argument
        Map<String, String> segmentPointer = new HashMap<String, String>();
        segmentPointer.put("local", "LCL");
        segmentPointer.put("argument", "ARG");
        segmentPointer.put("this", "THIS");
        segmentPointer.put("that", "THAT");
        segmentPointer.put("pointer0", "THIS");
        segmentPointer.put("pointer1", "THAT");

        // add, as a comment, the VM line being parsed
        String assemblyCode = "// pop " + segment + " " + index + "\n";

        switch(segment) {
        case "local":
        case "argument":
        case "this":
        case "that":
            assemblyCode += "@" + index + "\n"
                    + "D=A" + "\n"
                    + "@" + segmentPointer.get(segment) + "\n"
                    + "D=D+M" + "\n"
                    + "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "A=M" + "\n"
                    + "A=A+D" + "\n"
                    + "D=A-D" + "\n"
                    + "A=A-D" + "\n"
                    + "M=D" + "\n";
            break;

        case "pointer":
        case "static":
            assemblyCode += "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "D=M" + "\n";

            if (segment.equals("pointer")) {
                assemblyCode += "@" + segmentPointer.get(segment + index) + "\n";
            }
            else {
                assemblyCode += "@" + filename + "." + index + "\n";
            }

            assemblyCode += "M=D" + "\n";
            break;

        case "temp":
            assemblyCode += "@" + index + "\n"
                    + "D=A" + "\n"
                    + "@5" + "\n"
                    + "D=A+D" + "\n"
                    + "@SP" + "\n"
                    + "M=M-1" + "\n"
                    + "A=M" + "\n"
                    + "A=M" + "\n"
                    + "A=A+D" + "\n"
                    + "D=A-D" + "\n"
                    + "A=A-D" + "\n"
                    + "M=D" + "\n";
            break;
        }

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkRep();
    }

    /**
     * Converts the VM label into assembly label. If the label is inside a function, "function$"
     * is prepended to it. Otherwise, nothing is done.
     * 
     * @param label  The label to convert.
     */
    public void writeLabel(String label) {
        String assemblyCode = "// label " + label + "\n";

        // if inside a function, prepend file and function info
        if (!currentFunction.isEmpty()) {
            assemblyCode += "(" + currentFunction + "$" + label + ")\n";
        }
        // else not
        else {
            assemblyCode += "(" + label + ")\n";
        }

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the VM goto into an unconditional jump in assembly. If the goto is inside a
     * function, "function$" is prepended to the label. Otherwise, nothing is done.
     * 
     * @param label  The label to jump to.
     */
    public void writeGoto(String label) {
        String assemblyCode = "// goto " + label + "\n";

        // if inside a function, prepend file and function info
        if (!currentFunction.isEmpty()) {
            assemblyCode += "@" + currentFunction + "$" + label + "\n"
                    + "0;JMP" + "\n";
        }
        // else not
        else {
            assemblyCode += "@" + label + "\n"
                    + "0;JMP" + "\n";
        }

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the VM if-goto into a conditional jump in assembly. If the if-goto is inside a
     * function, "function$" is prepended to the label. Otherwise, nothing is done.
     * 
     * @param label  The label to jump to.
     */
    public void writeIf(String label) {
        String assemblyCode = "// if-goto " + label + "\n"
                + "@SP" + "\n"
                + "M=M-1" + "\n"
                + "A=M" + "\n"
                + "D=M" + "\n"
                + "@" + filename + ".IF$END." + labelCount + "\n"
                + "D;JEQ" + "\n";

        // if inside a function, prepend file and function info
        if (!currentFunction.isEmpty()) {
            assemblyCode += "@" + currentFunction + "$" + label + "\n";
        }
        // else not
        else {
            assemblyCode += "@" + label + "\n";
        }
        
        assemblyCode += "0;JMP" + "\n"
                + "(" + filename + ".IF$END." + labelCount + ")\n";
        ++labelCount;

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Translates a function call into corresponding assembly code. All the state variables, i.e.
     * 
     * - SP
     * - LCL
     * - ARG
     * - THIS
     * - THAT
     * 
     * are saved in the memory, and the function is called.
     * 
     * @param args  The function name and the number of arguments, in that order.
     */
    public void writeCall(List<String> args) {
        String assemblyCode = "// call " + args.get(0) + " " + args.get(1) + "\n"
                + "@" + args.get(0) + "$RET." + functionReturnCount + "\n"
                + "D=A" + "\n"
                + "@SP" + "\n"
                + "M=M+1" + "\n"
                + "A=M-1" + "\n"
                + "M=D" + "\n"
                + "@LCL" + "\n"
                + "D=M" + "\n"
                + "@SP" + "\n"
                + "M=M+1" + "\n"
                + "A=M-1" + "\n"
                + "M=D" + "\n"
                + "@ARG" + "\n"
                + "D=M" + "\n"
                + "@SP" + "\n"
                + "M=M+1" + "\n"
                + "A=M-1" + "\n"
                + "M=D" + "\n"
                + "@THIS" + "\n"
                + "D=M" + "\n"
                + "@SP" + "\n"
                + "M=M+1" + "\n"
                + "A=M-1" + "\n"
                + "M=D" + "\n"
                + "@THAT" + "\n"
                + "D=M" + "\n"
                + "@SP" + "\n"
                + "M=M+1" + "\n"
                + "A=M-1" + "\n"
                + "M=D" + "\n"
                + "@SP" + "\n"
                + "D=M" + "\n"
                + "@" + args.get(1) + "\n"
                + "D=D-A" + "\n"
                + "@5" + "\n"
                + "D=D-A" + "\n"
                + "@ARG" + "\n"
                + "M=D" + "\n"
                + "@SP" + "\n"
                + "D=M" + "\n"
                + "@LCL" + "\n"
                + "M=D" + "\n"
                + "@" + args.get(0) + "\n"
                + "0;JMP" + "\n"
                + "(" + args.get(0) + "$RET." + functionReturnCount + ")\n";
        ++functionReturnCount;

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a return statement into corresponding assembly code. All state variables, i.e.
     * 
     * - SP
     * - LCL
     * - ARG
     * - THIS
     * - THAT
     * 
     * are restored. Furthermore, the return value is pushed onto the caller's stack.
     */
    public void writeReturn() {
        String assemblyCode = "// return" + "\n"
                + "@LCL" + "\n"
                + "D=M" + "\n"
                + "@5" + "\n"
                + "A=D-A" + "\n"
                + "D=M" + "\n"
                + "@retaddr" + "\n"
                + "M=D" + "\n"
                + "@SP" + "\n"
                + "A=M-1" + "\n"
                + "D=M" + "\n"
                + "@ARG" + "\n"
                + "A=M" + "\n"
                + "M=D" + "\n"
                + "@ARG" + "\n"
                + "D=M" + "\n"
                + "@SP" + "\n"
                + "M=D+1" + "\n"
                + "@LCL" + "\n"
                + "A=M-1" + "\n"
                + "D=M" + "\n"
                + "@THAT" + "\n"
                + "M=D" + "\n"
                + "@LCL" + "\n"
                + "D=M" + "\n"
                + "@2" + "\n"
                + "A=D-A" + "\n"
                + "D=M" + "\n"
                + "@THIS" + "\n"
                + "M=D" + "\n"
                + "@LCL" + "\n"
                + "D=M" + "\n"
                + "@3" + "\n"
                + "A=D-A" + "\n"
                + "D=M" + "\n"
                + "@ARG" + "\n"
                + "M=D" + "\n"
                + "@LCL" + "\n"
                + "D=M" + "\n"
                + "@4" + "\n"
                + "A=D-A" + "\n"
                + "D=M" + "\n"
                + "@LCL" + "\n"
                + "M=D" + "\n"
                + "@retaddr" + "\n"
                + "A=M" + "\n"
                + "0;JMP" + "\n";

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a function declaration into corresponding assembly code. Necessary space for the
     * local variables is created and initialized to zero.
     * 
     * @param args  The function name and the number of local variables, in that order.
     */
    public void writeFunction(List<String> args) {
        final String function = args.get(0);
        
        // set current function
        currentFunction = function;

        String assemblyCode = "// function " + function + " " + args.get(1) + "\n"
                + "(" + function + ")\n"
                + "@" + args.get(1) + "\n"
                + "D=A" + "\n"
                + "(" + function + "$START-INIT)" + "\n"
                + "@" + function + "$END-INIT" + "\n"
                + "D;JEQ" + "\n"
                + "@SP" + "\n"
                + "M=M+1" + "\n"
                + "A=M-1" + "\n"
                + "M=0" + "\n"
                + "D=D-1" + "\n"
                + "@" + function + "$START-INIT" + "\n"
                + "0;JMP" + "\n"
                + "(" + function + "$END-INIT" + ")\n";

        // write the assembly code to the output file
        try {
            outputFile.write(assemblyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the output file.
     */
    public void close() {
        try {
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
