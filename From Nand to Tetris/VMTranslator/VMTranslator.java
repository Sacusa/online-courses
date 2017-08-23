package VMTranslator;

import java.io.*;
import java.util.*;

import VMTranslator.Parser.CommandType;

public class VMTranslator {

    public static void main (String[] args) {
        // check argument
        if (args.length != 1) {
            printUsage();
        }
        String inputPathName = args[0];
        File inputPath = new File(inputPathName);

        // if file, simply translate
        if (inputPath.isFile()) {
            translateFile(inputPathName, true);
        }

        // if directory, translate all files in it into a single file
        else if (inputPath.isDirectory()) {
            File [] files = inputPath.listFiles();

            // assembly code buffer
            String assemblyFileBuffer = "";

            // translate vm files into corresponding asm files,
            // and merge them into a single asm file
            boolean firstFile = true;
            for (File file : files) {
                String filename = file.toString();

                // skip ahead if "file" is a not a file
                if (!file.isFile()) {
                    continue;
                }

                // if .vm file, translate it
                if (filename.endsWith(".vm")) {
                    if (firstFile) {
                        // add bootstrap code to the first file
                        translateFile(filename, true);
                        firstFile = false;
                    }
                    else {
                        translateFile(filename, false);
                    }
                }

                // open the newly generated file
                filename = filename.replace(".vm", ".asm");
                File outputFile = new File(filename);
                BufferedReader outputFileBuffer = null;
                try {
                    outputFileBuffer = new BufferedReader(new FileReader(outputFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // read the file into assemblyFileBuffer
                try {
                    String text = null;
                    while ((text = outputFileBuffer.readLine()) != null) {
                        assemblyFileBuffer += text + "\n";
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
                        if (outputFileBuffer != null) {
                            outputFileBuffer.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // delete the asm file
                outputFile.delete();
            }

            // extract the directory name, for the asm file
            String [] tokens = inputPathName.split("/");
            String assemblyFileName = inputPathName + "/" + tokens[tokens.length - 1] + ".asm";

            // create the final asm file, and delete if already exists
            File assemblyFile = new File(assemblyFileName);
            if (assemblyFile.exists()) {
                assemblyFile.delete();
            }

            // write data to the file
            BufferedWriter assemblyFileWriter = null;
            try {
                assemblyFileWriter = new BufferedWriter(new FileWriter(assemblyFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assemblyFileWriter.write(assemblyFileBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assemblyFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            printUsage();
        }
    }

    /**
     * Displays usage information for the translator.
     */
    private static void printUsage () {
        System.out.println("Usage: java VMTranslator <input.vm> or <directory>");
    }

    private static void translateFile (String filename, boolean writeInit) {
        Parser parser = new Parser(filename);

        // set filename to point to output file
        filename = filename.replace(".vm", ".asm");

        CodeWriter codewriter = null;
        try {
            codewriter = new CodeWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> inputCommandArgs = new ArrayList<String>();

        if (writeInit) {
            codewriter.writeInit();
        }

        // parse commands sequentially, writing them to a file
        while (parser.hasMoreCommands()) {
            parser.advance();
            CommandType inputCommandType = parser.commandType();

            if (inputCommandType != CommandType.RETURN) {
                inputCommandArgs.clear();
                inputCommandArgs.add(parser.arg1());
            }

            switch (inputCommandType) {
            case PUSH:
                inputCommandArgs.add(parser.arg2().toString());
                codewriter.writePush(inputCommandArgs);
                break;

            case POP:
                inputCommandArgs.add(parser.arg2().toString());
                codewriter.writePop(inputCommandArgs);
                break;

            case ARITHMETIC:
                codewriter.writeArithmetic(inputCommandArgs.get(0));
                break;

            case IF:
                codewriter.writeIf(inputCommandArgs.get(0));
                break;

            case GOTO:
                codewriter.writeGoto(inputCommandArgs.get(0));
                break;

            case LABEL:
                codewriter.writeLabel(inputCommandArgs.get(0));
                break;

            case FUNCTION:
                inputCommandArgs.add(parser.arg2().toString());
                codewriter.writeFunction(inputCommandArgs);
                break;

            case CALL:
                inputCommandArgs.add(parser.arg2().toString());
                codewriter.writeCall(inputCommandArgs);
                break;

            case RETURN:
                codewriter.writeReturn();
                break;

            case INVALID:
                System.out.println("Error:" + filename.replace(".asm", ".vm") + ":" +
                        (parser.line() + 1) + ":" + parser.command());
                break;
            }
        }

        // close output file
        codewriter.close();
    }
}
