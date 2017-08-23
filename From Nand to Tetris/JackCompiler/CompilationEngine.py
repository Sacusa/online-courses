#!/usr/bin/python
import JackTokenizer
import SymbolTable
import VMWriter

class CompilationEngine(object):
    """ Compilation Engine
    Makes use of the JackTokenizer API to compile the tokens into valid VM using SymbolTable
    and VMWriter API.
    """

    OP_LIST = ['+', '-', '*', '/', '&', '|', '<', '>', '=']
    KEYWORD_CONST_LIST = ['true', 'false', 'null', 'this']
    UNARY_OP_LIST = ['-', '~']

    def __init__(self, input_file_name, output_file_name):
        """ Tokenizes the input file and truncates the output file to write XML.
        """
        # parse the input file into tokens
        try:
            self.tokenizer = JackTokenizer.JackTokenizer(input_file_name)
        except IOError as ioerr:
            print ioerr
        except SyntaxError as serr:
            print serr

        self.class_name = ''
        self.label_count = 0
        self.class_symbol_table = SymbolTable.SymbolTable()
        self.subroutine_symbol_table = SymbolTable.SymbolTable()
        self.vmwriter = VMWriter.VMWriter(output_file_name);
        self.compile_class()

    def compile_class(self):
        """ Compiles a complete class definition. Requires the tokenizer to be pointing at the
        keyword 'class'.
        """
        self.tokenizer.advance()
        self.class_symbol_table.reset()

        # consume the keyword 'class'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class compilation')
        self.tokenizer.advance()

        # extract class name
        self.class_name = self.tokenizer.current_token()
        if not self.tokenizer.token_type() == 'IDENTIFIER':
            raise RuntimeError('invalid class name: ' + self.class_name)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class compilation')
        self.tokenizer.advance()

        # consume the symbol '{'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class compilation')
        self.tokenizer.advance()

        # compile class variable declarations
        while (self.tokenizer.token_type() == 'KEYWORD') and ( \
               self.tokenizer.current_token() == 'static' or \
               self.tokenizer.current_token() == 'field'):
            self.compile_class_var_dec()

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in class compilation')
            self.tokenizer.advance()

        # compile subroutine declarations
        while (self.tokenizer.token_type() == 'KEYWORD') and ( \
               self.tokenizer.current_token() == 'constructor' or \
               self.tokenizer.current_token() == 'function' or \
               self.tokenizer.current_token() == 'method'):
            self.compile_subroutine()

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in class compilation')
            self.tokenizer.advance()

    def compile_class_var_dec(self):
        """ Compiles a static or field declaration, as found in class definitions. Requires the
        tokenizer to be pointing at the keywords 'static' or 'field'.
        Updates the class symbol table with the new values.
        """
        # extract variable kind
        kind = self.tokenizer.current_token()

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class variable declarations compilation')
        self.tokenizer.advance()

        # extract variable type
        if (((self.tokenizer.token_type() == 'KEYWORD') and ( \
             self.tokenizer.current_token() == 'int' or
             self.tokenizer.current_token() == 'char' or
             self.tokenizer.current_token() == 'boolean')) or
            (self.tokenizer.token_type() == 'IDENTIFIER')):
             type = self.tokenizer.current_token()
        else:
            raise RuntimeError('invalid variable type: ' + self.tokenizer.current_token())

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class variable declarations compilation')
        self.tokenizer.advance()

        # extract variable name
        name = self.tokenizer.current_token()
        if not self.tokenizer.token_type() == 'IDENTIFIER':
            raise RuntimeError('invalid variable name: ' + name)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class variable declarations compilation')
        self.tokenizer.advance()

        # add variable to symbol table
        self.class_symbol_table.define(name, type, kind)

        # extract multiple variable names, if they exist
        while self.tokenizer.token_type() == 'SYMBOL' and \
              self.tokenizer.current_token() == ',':

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in class variable declarations compilation')
            self.tokenizer.advance()

            # extract variable name
            if not self.tokenizer.token_type() == 'IDENTIFIER':
                raise RuntimeError('invalid variable name: ' + self.tokenizer.current_token())
            name = self.tokenizer.current_token()

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in class variable declarations compilation')
            self.tokenizer.advance()

            # add variable to symbol table
            self.class_symbol_table.define(name, type, kind)

    def compile_subroutine(self):
        """ Compiles a subroutine inside a class. Requires the tokenizer to be pointing at the
        keyword 'method', 'function' or 'constructor'.
        """
        self.subroutine_symbol_table.reset()

        # if constructor, allocate memory for field variables and latch onto 'this'
        is_constructor = False
        if self.tokenizer.current_token() == 'constructor':
            is_constructor = True

        # if method, set this pointer after declaration
        is_method = False
        if self.tokenizer.current_token() == 'method':
            is_method = True
            self.subroutine_symbol_table.define('this', 'pointer', 'argument')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class function declarations compilation')
        self.tokenizer.advance()

        # special handling at the end for void functions
        is_return_void = False
        if ((self.tokenizer.token_type() == 'KEYWORD') and
            (self.tokenizer.current_token() == 'void')):
            is_return_void = True
        elif not (((self.tokenizer.token_type() == 'KEYWORD') and ( \
                    self.tokenizer.current_token() == 'int' or
                    self.tokenizer.current_token() == 'char' or
                    self.tokenizer.current_token() == 'boolean')) or
                   (self.tokenizer.token_type() == 'IDENTIFIER')):
            raise RuntimeError('invalid return type: ' + self.tokenizer.current_token())

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class function declarations compilation')
        self.tokenizer.advance()

        # extract subroutine name
        subroutine_name = self.tokenizer.current_token()
        if not self.tokenizer.token_type() == 'IDENTIFIER':
            raise RuntimeError('invalid function name: ' + subroutine_name)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class function declarations compilation')
        self.tokenizer.advance()

        # consume the symbol '('
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '('):
            raise RuntimeError('invalid token ' + \
                    self.tokenizer.current_token() + '. Expected \'(\'')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class function declarations compilation')
        self.tokenizer.advance()

        # compile parameter list
        self.compile_parameter_list()

        # consume the symbol ')'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class function declarations compilation')
        self.tokenizer.advance()

        # consume the symbol '{'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '{'):
            raise RuntimeError('invalid token ' + \
                    self.tokenizer.current_token() + '. Expected \'(\'')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in class function declarations compilation')
        self.tokenizer.advance()

        # compile variable declarations, if any
        while self.tokenizer.token_type() == 'KEYWORD' and self.tokenizer.current_token() == 'var':
            self.compile_var_dec()

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in class function declarations compilation')
            self.tokenizer.advance()

        # write the initial function code
        self.vmwriter.write_function(self.class_name + '.' + subroutine_name, \
                                     self.subroutine_symbol_table.var_count('var'))
        if is_constructor:
            self.vmwriter.write_push('constant', self.class_symbol_table.var_count('field'))
            self.vmwriter.write_call('Memory.alloc', 1)
            self.vmwriter.write_pop('pointer', 0)
        if is_method:
            self.vmwriter.write_push('argument', 0)
            self.vmwriter.write_pop('pointer', 0)

        # compile statements
        self.compile_statements()

        # consume the symbol '}'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '}'):
            raise RuntimeError('invalid token ' + \
                    self.tokenizer.current_token() + '. Expected \'}\'')

    def compile_parameter_list(self):
        """ Compiles a, possibly empty, parameter list, not including the enclosing parentheses,
        also updating the symbol table. As such, it requires the tokenizer to be pointing at the
        first token following the symbol '('.
        """
        first_parameter = True

        while True:
            # check if list completed
            if self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == ')':
                break

            # consume the symbol ',', if not the first parameter
            if not first_parameter:
                if self.tokenizer.token_type() == 'SYMBOL' and \
                   self.tokenizer.current_token() == ',':

                    # raise error if out of tokens
                    if not self.tokenizer.has_more_tokens():
                        raise RuntimeError('out of tokens in parameter list compilation')
                    self.tokenizer.advance()
                else:
                    raise RuntimeError('invalid token ' + self.tokenizer.current_token() + \
                                        '. Expected \',\'')
            else:
                first_parameter = False

            # extract variable type
            type = self.tokenizer.current_token()
            if not (((self.tokenizer.token_type() == 'KEYWORD') and ( \
                      type == 'int' or
                      type == 'char' or
                      type == 'boolean')) or
                     (self.tokenizer.token_type() == 'IDENTIFIER')):
                raise RuntimeError('invalid variable type: ' + self.tokenizer.current_token())

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in parameter list compilation')
            self.tokenizer.advance()

            # extract variable name
            name = self.tokenizer.current_token()
            if not self.tokenizer.token_type() == 'IDENTIFIER':
                raise RuntimeError('invalid variable name: ' + name)

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in parameter list compilation')
            self.tokenizer.advance()

            # update symbol table
            self.subroutine_symbol_table.define(name, type, 'argument')

    def compile_var_dec(self):
        """ Compiles a variable declaration, updating the subroutine symbol table. Requires the
        tokenizer to be pointing at the keyword 'var'.
        """
        # consume the keyword 'var'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in variable declarations compilation')
        self.tokenizer.advance()

        # extract the variable type
        type = self.tokenizer.current_token()
        if not (((self.tokenizer.token_type() == 'KEYWORD') and ( \
                  type == 'int' or
                  type == 'char' or
                  type == 'boolean')) or
                 (self.tokenizer.token_type() == 'IDENTIFIER')):
            raise RuntimeError('invalid variable type: ' + self.tokenizer.current_token())

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in variable declarations compilation')
        self.tokenizer.advance()

        first_variable = True

        # compile variables, till we reach the symbol ';'
        while not (self.tokenizer.token_type() == 'SYMBOL' and \
                   self.tokenizer.current_token() == ';'):

            # consume the symbol ',', if not the first variable
            if not first_variable:
                if self.tokenizer.token_type() == 'SYMBOL' and \
                   self.tokenizer.current_token() == ',':
                    # raise error if out of tokens
                    if not self.tokenizer.has_more_tokens():
                        raise RuntimeError('out of tokens in variable declarations compilation')
                    self.tokenizer.advance()
                else:
                    raise RuntimeError('invalid token ' + self.tokenizer.current_token() + \
                                        '. Expected \',\'')
            else:
                first_variable = False

            # extract variable name
            name = self.tokenizer.current_token()
            if not self.tokenizer.token_type() == 'IDENTIFIER':
                raise RuntimeError('invalid variable name ' + name)

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in variable declarations compilation')
            self.tokenizer.advance()

            # update symbol table
            self.subroutine_symbol_table.define(name, type, 'var')

        # consume the symbol ';'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == ';'):
            raise RuntimeError('invalid token ' + \
                    self.tokenizer.current_token() + '. Expected \';\'')

    def compile_statements(self):
        """ Compiles a sequence of statements, not including the enclosing parentheses. As such,
        it requires the tokenizer to be pointing at the first token after the symbol '{'.
        """
        while True:
            token = self.tokenizer.current_token()

            # check if the keyword signifies the start of a statement
            if self.tokenizer.token_type() == 'KEYWORD':
                if token == 'do':
                    self.compile_do()
                elif token == 'let':
                    self.compile_let()
                elif token == 'while':
                    self.compile_while()
                elif token == 'return':
                    self.compile_return()
                elif token == 'if':
                    self.compile_if()
                else:
                    break
            else:
                break

    def compile_do(self):
        """ Compiles a do statement. Requires the tokenizer to be pointing at the keyword 'do'.
        """
        # consume the keyword 'do'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in do statement compilation')
        self.tokenizer.advance()

        # extract subroutine name or class name or variable name
        identifier1 = self.tokenizer.current_token()
        subroutine_name = identifier1
        if not (self.tokenizer.token_type() == 'IDENTIFIER'):
            raise RuntimeError('invalid token: ' + identifier1)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in do statement compilation')
        self.tokenizer.advance()

        # check if scope resolution is done
        if self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '.':
            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in do statement compilation')
            self.tokenizer.advance()

            # extract subroutine name
            subroutine_name = self.tokenizer.current_token()
            if not (self.tokenizer.token_type() == 'IDENTIFIER'):
                raise RuntimeError('invalid token: ' + subroutine_name)

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in do statement compilation')
            self.tokenizer.advance()

        # consume the symbol '('
        if not (self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '('):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + '. Expected (')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in do statement compilation')
        self.tokenizer.advance()

        # if identifier1 is a variable, push it as argument 0
        kind = ''
        type = ''
        index = -1
        if identifier1 != subroutine_name:
            kind = self.subroutine_symbol_table.kind_of(identifier1)
            type = self.subroutine_symbol_table.type_of(identifier1)
            index = self.subroutine_symbol_table.index_of(identifier1)
            if kind == None:
                kind = self.class_symbol_table.kind_of(identifier1)
                type = self.class_symbol_table.type_of(identifier1)
                index = self.class_symbol_table.index_of(identifier1)
            if kind != None:
                if kind == 'field':
                    self.vmwriter.write_push('this', index)
                elif kind == 'var':
                    self.vmwriter.write_push('local', index)
                else:
                    self.vmwriter.write_push(kind, index)
        else:
            self.vmwriter.write_push('pointer', 0)

        # compile expressions in argument list
        number_of_args = self.compile_expression_list()

        # consume the symbol ')'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in do statement compilation')
        self.tokenizer.advance()

        # scope resolution was done; prepend class name to subroutine name
        if identifier1 != subroutine_name:
            if kind != None:
                # method call
                self.vmwriter.write_call(type + '.' + subroutine_name, number_of_args + 1)
            else:
                # function call
                self.vmwriter.write_call(identifier1 + '.' + subroutine_name, number_of_args)
        # no scope resolution; direct call
        else:
            self.vmwriter.write_call(self.class_name + '.' + subroutine_name, number_of_args + 1)

        # consume the symbol ';'
        if not (self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == ';'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + '. Expected ;')

        # pop the return value into temp
        self.vmwriter.write_pop('temp', 0)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in do statement compilation')
        self.tokenizer.advance()

    def compile_let(self):
        """ Compiles a let statement. Requires the tokenizer to be pointing at the keyword 'let'.
        """
        # consume the keyword 'let'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in let statement compilation')
        self.tokenizer.advance()

        # extract variable name, kind, type and index
        destination = self.tokenizer.current_token()
        if not self.tokenizer.token_type() == 'IDENTIFIER':
            raise RuntimeError('invalid variable name: ' + destination)
        kind = self.subroutine_symbol_table.kind_of(destination)
        type = self.subroutine_symbol_table.type_of(destination)
        index = self.subroutine_symbol_table.index_of(destination)
        if kind == None:
            kind = self.class_symbol_table.kind_of(destination)
            type = self.class_symbol_table.type_of(destination)
            index = self.class_symbol_table.index_of(destination)
            if kind == None:
                raise RuntimeError('unknown identifier: ' + destination)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in let statement compilation')
        self.tokenizer.advance()

        # check if array index is given
        is_array = False
        if self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '[':
            is_array = True

            # push base address
            if kind == 'field':
                self.vmwriter.write_push('this', index)
            elif kind == 'var':
                self.vmwriter.write_push('local', index)
            else:
                self.vmwriter.write_push(kind, index)

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in let statement compilation')
            self.tokenizer.advance()

            # compute offset
            self.compile_expression()

            # consume the symbol ']'
            if not (self.tokenizer.token_type() == 'SYMBOL' and \
                    self.tokenizer.current_token() == ']'):
                raise RuntimeError('invalid token ' + self.tokenizer.current_token() + \
                                   '. Expected ]')

            # stack top = base + offset
            self.vmwriter.write_arithmetic('+')

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in let statement compilation')
            self.tokenizer.advance()

        # consume the symbol '='
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '='):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected =')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in let statement compilation')
        self.tokenizer.advance()

        self.compile_expression()

        # consume the symbol ';'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == ';'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected ;')

        # store the result
        if is_array:
            self.vmwriter.write_pop('temp', 0)
            self.vmwriter.write_pop('pointer', 1)
            self.vmwriter.write_push('temp', 0)
            self.vmwriter.write_pop('that', 0)
        else:
            if kind == 'field':
                self.vmwriter.write_pop('this', index)
            elif kind == 'var':
                self.vmwriter.write_pop('local', index)
            else:
                self.vmwriter.write_pop(kind, index)

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in let statement compilation')
        self.tokenizer.advance()

    def compile_while(self):
        """ Compiles a while statement. Requires the tokenizer to be pointing at the keyword
        'while'.
        """
        label_count = self.label_count
        self.label_count += 2
        self.vmwriter.write_label(self.class_name + '.WHILE.' + str(label_count))

        # consume the keyword 'while'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in while statement compilation')
        self.tokenizer.advance()

        # consume the symbol '('
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '('):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected (')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in while statement compilation')
        self.tokenizer.advance()

        self.compile_expression()

        # consume the symbol ')'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == ')'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected )')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in while statement compilation')
        self.tokenizer.advance()

        # if !cond, goto end of loop
        self.vmwriter.write_arithmetic('not')
        self.vmwriter.write_if(self.class_name + '.WHILE.' + str(label_count + 1))

        # consume the symbol '{'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '{'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected {')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in while statement compilation')
        self.tokenizer.advance()

        self.compile_statements()

        # consume the symbol '}'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '}'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected }')

        # goto start
        self.vmwriter.write_goto(self.class_name + '.WHILE.' + str(label_count))

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in while statement compilation')
        self.tokenizer.advance()

        # end of loop label
        self.vmwriter.write_label(self.class_name + '.WHILE.' + str(label_count + 1))

    def compile_return(self):
        """ Compiles a return statement. Requires the tokenizer to be pointing at the keyword
        'return'.
        """
        # consume the keyword 'return'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in return statement compilation')
        self.tokenizer.advance()

        # no expression is present, so push 0
        if (self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == ';'):
            self.vmwriter.write_push('constant', 0)
        # expression is present, compile it
        else:
            self.compile_expression()

        # consume the symbol ';'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in return statement compilation')
        self.tokenizer.advance()

        self.vmwriter.write_return()

    def compile_if(self):
        """ Compiles a if statement. Requires the tokenizer to be pointing at the keyword 'if'.
        """
        label_count = self.label_count
        self.label_count += 3

        # consume the keyword 'if'
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in if statement compilation')
        self.tokenizer.advance()

        # consume the symbol '('
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '('):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected (')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in if statement compilation')
        self.tokenizer.advance()

        self.compile_expression()

        # consume the symbol ')'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == ')'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected )')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in if statement compilation')
        self.tokenizer.advance()

        # skip over goto else if the condition is true
        self.vmwriter.write_if(self.class_name + '.IF.' + str(label_count))
        self.vmwriter.write_goto(self.class_name + '.IF.' + str(label_count + 1))
        self.vmwriter.write_label(self.class_name + '.IF.' + str(label_count))

        # consume the symbol '{'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '{'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected {')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in if statement compilation')
        self.tokenizer.advance()

        self.compile_statements()

        # consume the symbol '}'
        if not (self.tokenizer.token_type() == 'SYMBOL' and \
                self.tokenizer.current_token() == '}'):
            raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                               '. Expected }')

        # raise error if out of tokens
        if not self.tokenizer.has_more_tokens():
            raise RuntimeError('out of tokens in if statement compilation')
        self.tokenizer.advance()

        # check if else is present
        if self.tokenizer.token_type() == 'KEYWORD' and self.tokenizer.current_token() == 'else':
            # goto end of if
            self.vmwriter.write_goto(self.class_name + '.IF.' + str(label_count + 2))

            # else label
            self.vmwriter.write_label(self.class_name + '.IF.' + str(label_count + 1))

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in if statement compilation')
            self.tokenizer.advance()

            # consume the symbol '{'
            if not (self.tokenizer.token_type() == 'SYMBOL' and \
                    self.tokenizer.current_token() == '{'):
                raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                   '. Expected {')

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in if statement compilation')
            self.tokenizer.advance()

            self.compile_statements()

            # consume the symbol '}'
            if not (self.tokenizer.token_type() == 'SYMBOL' and \
                    self.tokenizer.current_token() == '}'):
                raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                   '. Expected }')

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in if statement compilation')
            self.tokenizer.advance()

            # end of if label
            self.vmwriter.write_label(self.class_name + '.IF.' + str(label_count + 2))
        
        else:
            # no else; create end of if label
            self.vmwriter.write_label(self.class_name + '.IF.' + str(label_count + 1))

    def compile_expression(self):
        """ Compiles an expression. Requires the tokenizer to be pointing at the first term of the
        expression. Pushes the result on the stack.
        """
        # compile the first term and push it onto stack
        self.compile_term()

        # compile additional ops and terms, if any
        operator = self.tokenizer.current_token()
        while operator in CompilationEngine.OP_LIST:
            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in expression compilation')
            self.tokenizer.advance()

            # compile the next term and push it onto stack
            self.compile_term()

            # write the operator
            self.vmwriter.write_arithmetic(operator)

            operator = self.tokenizer.current_token()

    def compile_term(self):
        """ Compiles a term. Requires the tokenizer to be pointing at the first token of the term.
        Pushes the the value of the term on the stack.
        """
        token = self.tokenizer.current_token()
        token_type = self.tokenizer.token_type()

        # check if integer constant
        if token_type == 'INT_CONST':
            integer_constant = int(token)
            if integer_constant >= 0 and integer_constant <= 32767:
                self.vmwriter.write_push('constant', token)
            else:
                raise RuntimeError('integer out of range: ' + str(integer_constant))

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

        # check if string constant
        elif token_type == 'STRING_CONST':
            token = token.strip('"')

            # allocate memory for the string
            self.vmwriter.write_push('constant', len(token))
            self.vmwriter.write_call('String.new', 1)
            for character in token:
                self.vmwriter.write_push('constant', ord(character))
                self.vmwriter.write_call('String.appendChar', 2)

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

        # check if keyword constant
        elif token in CompilationEngine.KEYWORD_CONST_LIST:
            if token == 'this':
                self.vmwriter.write_push('pointer', 0)
            else:
                self.vmwriter.write_push('constant', 0)
                if token == 'true':
                    self.vmwriter.write_arithmetic('not')

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

        # check if variable or subroutine name
        elif token_type == 'IDENTIFIER':
            # extract kind, type and index info
            kind = self.subroutine_symbol_table.kind_of(token)
            type = self.subroutine_symbol_table.type_of(token)
            index = self.subroutine_symbol_table.index_of(token)
            if kind == None:
                kind = self.class_symbol_table.kind_of(token)
                type = self.class_symbol_table.type_of(token)
                index = self.class_symbol_table.index_of(token)

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

            # check if the identifier is an array
            if self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '[':
                # push base address
                if kind == 'field':
                    self.vmwriter.write_push('this', index)
                elif kind == 'var':
                    self.vmwriter.write_push('local', index)
                else:
                    self.vmwriter.write_push(kind, index)

                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in let statement compilation')
                self.tokenizer.advance()

                # compute offset
                self.compile_expression()

                # consume the symbol ']'
                if not (self.tokenizer.token_type() == 'SYMBOL' and \
                        self.tokenizer.current_token() == ']'):
                    raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                       '. Expected ]')

                # push on the stack value of base[offset]
                self.vmwriter.write_arithmetic('+')
                self.vmwriter.write_pop('pointer', 1)
                self.vmwriter.write_push('that', 0)

                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

            # check if the identifier is a subroutine call
            elif self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '(':
                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

                number_of_args = self.compile_expression_list()

                # consume the symbol ')'
                if not (self.tokenizer.token_type() == 'SYMBOL' and \
                        self.tokenizer.current_token() == ')'):
                    raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                       '. Expected )')

                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

                # call the subroutine
                self.vmwriter.write_call(token, number_of_args)

            # check if subroutine call follows the identifier
            elif self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '.':
                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

                # extract subroutine name
                subroutine_name = self.tokenizer.current_token()
                if not self.tokenizer.token_type() == 'IDENTIFIER':
                    raise RuntimeError('invalid subroutine name: ' + subroutine_name)

                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

                # if token is an object, push it as argument 0
                if kind == 'field':
                    self.vmwriter.write_push('this', index)
                elif kind == 'var':
                    self.vmwriter.write_push('local', index)
                elif kind == 'static' or kind == 'argument':
                    self.vmwriter.write_push(kind, index)

                # consume the symbol '('
                if not (self.tokenizer.token_type() == 'SYMBOL' and \
                        self.tokenizer.current_token() == '('):
                    raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                        '. Expected (')

                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

                number_of_args = self.compile_expression_list()

                # consume the symbol ')'
                if not (self.tokenizer.token_type() == 'SYMBOL' and \
                        self.tokenizer.current_token() == ')'):
                    raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                       '. Expected )')

                # raise error if out of tokens
                if not self.tokenizer.has_more_tokens():
                    raise RuntimeError('out of tokens in term compilation')
                self.tokenizer.advance()

                # write function call
                if kind != None:
                    # method call
                    self.vmwriter.write_call(type + '.' + subroutine_name, number_of_args + 1)
                else:
                    # function call
                    self.vmwriter.write_call(token + '.' + subroutine_name, number_of_args)

            # just a regular variable
            else:
                if kind == 'field':
                    self.vmwriter.write_push('this', index)
                elif kind == 'var':
                    self.vmwriter.write_push('local', index)
                else:
                    self.vmwriter.write_push(kind, index)

        # check if expression
        elif self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == '(':
            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

            self.compile_expression()

            # consume the symbol ')'
            if not (self.tokenizer.token_type() == 'SYMBOL' and \
                    self.tokenizer.current_token() == ')'):
                raise RuntimeError('invalid token: ' + self.tokenizer.current_token() + \
                                   '. Expected )')

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

        # check if a unary operator
        elif self.tokenizer.current_token() in CompilationEngine.UNARY_OP_LIST:
            operator = self.tokenizer.current_token()

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in term compilation')
            self.tokenizer.advance()

            self.compile_term()

            # perform operation
            if operator == '-':
                self.vmwriter.write_arithmetic('neg')
            else:
                self.vmwriter.write_arithmetic('not')

        # nothing matches, invalid term
        else:
            raise RuntimeError('invalid term: ' + self.tokenizer.current_token())

    def compile_expression_list(self):
        """ Compiles a, possibly empty, comma-separated list of expressions, not including the
        enclosing parentheses. Requires the tokenizer to be pointing at the first token after '('.
        """
        number_of_expressions = 0

        # compile first expression, if it exists
        if not (self.tokenizer.token_type() == 'SYMBOL' and self.tokenizer.current_token() == ')'):
            number_of_expressions += 1
            self.compile_expression()

        # compile additonal expressions, if any
        while self.tokenizer.current_token() == ',':
            number_of_expressions += 1

            # raise error if out of tokens
            if not self.tokenizer.has_more_tokens():
                raise RuntimeError('out of tokens in expression list compilation')
            self.tokenizer.advance()

            self.compile_expression()

        return number_of_expressions

    def close(self):
        """ Closes the output VM file.
        """
        self.vmwriter.close()
