#!/usr/bin/python
import os
import re

class JackTokenizer(object):
    """ Jack Tokenizer
    It converts a Jack source file into constituent tokens, which can then be
    accessed by the provided API.
    """

    # regex strings for matchin with tokens
    KEYWORD_RE = r'(class)|(constructor)|(function)|(method)|(field)|' + \
                 r'(static)|(var)|(int)|(char)|(boolean)|(void)|(true)|' + \
                 r'(false)|(null)|(this)|(let)|(do)|(if)|(else)|(while)|' + \
                 r'(return)'
    SYMBOL_RE = r'\{|\}|\(|\)|\[|\]|\.|\,|\;|\+|\-|\*|\/|\&|\||\<|\>|\=|\~'
    INT_CONST_RE = r'\d+'
    STRING_CONST_RE = r'"[^("|\n)]*"'
    IDENTIFIER_RE = r'[^\d]\w*'
    COMMENT_RE = r'(//(.*))|(/\*(.*?)\*/)'

    def __init__(self, input_file_name):
        """ Parses the file input_file_name into tokens.
        The tokens can then be accessed via the rest of the API.
        """
        self.tokens = []
        self.current_token_index = -1

        # check if input_file_name exists
        if not os.path.isfile(input_file_name):
            raise IOError(input_file_name + ' does not exist')

        # open the input file
        input_file = open(input_file_name, 'r')

        # read the file into a buffer
        input_file_buffer = input_file.readlines()
        input_file.close()

        # keep track of line number for error reporting
        line_number = 0

        # keep track of multiline comments
        comment_block = False

        for line in input_file_buffer:
            line_number += 1

            # skip over multiline comments
            if comment_block:
                comment_block_end = line.find('*/')
                if comment_block_end == -1:
                    continue
                else:
                    line = line[comment_block_end + 2:]
                    comment_block = False

            # backup copy of line for error reporting
            line_copy = line[:]

            # remove whitespaces and newlines
            line = line.strip()
            line = line.strip("\r\n")   # CRLF for Windows line ending
            line = line.strip("\n")     # LF for *nix line ending

            # remove single line comments
            line = re.sub(JackTokenizer.COMMENT_RE, '', line)

            # extract tokens from beginning of the line
            # and delete them after adding to list
            while line:
                if line.startswith('/*'):
                    comment_block = True
                    break

                token = self.next_token(line)
                if token:
                    self.tokens.append(token)
                    line = line[len(token):]
                else:
                    raise SyntaxError('ERROR line ' + str(line_number) + \
                                      ': ' + line_copy)
                line = line.strip()

    def has_more_tokens(self):
        """ Returns true if there are more tokens in the buffer.
        False, otherwise.
        """
        return (self.current_token_index + 1) < len(self.tokens)

    def advance(self):
        """ Advances the current token index.
        """
        self.current_token_index += 1

    def token_type(self):
        """ Returns a string describing the type of current token.
        It can be one of:
        -- KEYWORD
        -- SYMBOL
        -- INT_CONST
        -- STRING_CONST
        -- IDENTIFIER
        """
        token = self.tokens[self.current_token_index]

        ident_match = self.match_regex(JackTokenizer.IDENTIFIER_RE, token)
        if ident_match and (self.match_regex(JackTokenizer.KEYWORD_RE, token) == ident_match):
            return 'KEYWORD'
        elif self.match_regex(JackTokenizer.SYMBOL_RE, token):
            return 'SYMBOL'
        elif self.match_regex(JackTokenizer.INT_CONST_RE, token):
            return 'INT_CONST'
        elif self.match_regex(JackTokenizer.STRING_CONST_RE, token):
            return 'STRING_CONST'
        elif ident_match:
            return 'IDENTIFIER'

    def current_token(self):
        """ Returns the value of the current token.
        """
        return self.tokens[self.current_token_index]

    """
    The API ends here. Following methods are used internally by other methods,
    and shall not be used explicitly.
    """
    def next_token(self, line):
        """ Returns the first token that occurs in the 'line'.
        If no token occurs, None is returned.
        """
        # try matching with keyword
        token = self.match_regex(JackTokenizer.KEYWORD_RE, line)
        if token:
            # make sure it is not an identifier
            ident_token = self.match_regex(JackTokenizer.IDENTIFIER_RE, line)
            if token == ident_token:
                return token

        # try matching with symbol
        token = self.match_regex(JackTokenizer.SYMBOL_RE, line)
        if token:
            return token

        # try matching with integer constant
        token = self.match_regex(JackTokenizer.INT_CONST_RE, line)
        if token:
            return token

        # try matching with string constant
        token = self.match_regex(JackTokenizer.STRING_CONST_RE, line)
        if token:
            return token

        # try matching with identifier
        token = self.match_regex(JackTokenizer.IDENTIFIER_RE, line)
        if token:
            return token

        # no match found
        return None

    def match_regex(self, regex, line):
        """ Matches 'line' against the provided 'regex'.
        If the 'line' starts with the matched string, the matched string is
        returned. Else, None is returned.
        """
        match_re = re.search(regex, line)
        if match_re:
            token = match_re.group()
            if line.startswith(token):
                return token
        return None
