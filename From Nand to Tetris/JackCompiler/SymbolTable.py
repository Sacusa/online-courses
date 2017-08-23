#!/usr/bin/python

class SymbolTable(object):
    """ A symbol table to hold the following info about variables:
    1. kind (static, field, argument, var)
    2. type (int, char, etc.)
    3. index
    """

    def __init__(self):
        """ Create a new symbol table with empty tables and running counters set to 0
        """
        # dictionaries to hold index, type and kind of variable names
        self.index_table = {}
        self.type_table = {}
        self.kind_table = {}

        # running counters for kind of identifier
        self.static_kind_count = 0
        self.field_kind_count = 0
        self.argument_kind_count = 0
        self.var_kind_count = 0

    def reset(self):
        """ Reset the table, i.e. clear the dictionaries and reset the running counters.
        """
        # dictionaries to hold index, type and kind of variable names
        self.index_table = {}
        self.type_table = {}
        self.kind_table = {}

        # running counters for kind of identifier
        self.static_kind_count = 0
        self.field_kind_count = 0
        self.argument_kind_count = 0
        self.var_kind_count = 0

    def define(self, name, type, kind):
        """ Add a new symbol table entry.
        """
        if (kind == 'static'):
            self.index_table[name] = self.static_kind_count
            self.static_kind_count += 1
        elif (kind == 'field'):
            self.index_table[name] = self.field_kind_count
            self.field_kind_count += 1
        elif (kind == 'argument'):
            self.index_table[name] = self.argument_kind_count
            self.argument_kind_count += 1
        elif (kind == 'var'):
            self.index_table[name] = self.var_kind_count
            self.var_kind_count += 1
        else:
            raise RuntimeError('invalid identifier kind: ' + kind)

        self.type_table[name] = type
        self.kind_table[name] = kind

    def var_count(self, kind):
        """ Returns the current value of the running counter of kind.
        """
        if kind == 'static':
            return self.static_kind_count
        elif kind == 'field':
            return self.field_kind_count
        elif kind == 'argument':
            return self.argument_kind_count
        elif kind == 'var':
            return self.var_kind_count
        else:
            return None

    def kind_of(self, name):
        """ Returns the kind of variable name. None, if it does not exist.
        """
        return self.kind_table.get(name)

    def type_of(self, name):
        """ Returns the type of variable name. None, if it does not exist.
        """
        return self.type_table.get(name)

    def index_of(self, name):
        """ Returns the index of variable name. None, if it does not exist.
        """
        return self.index_table.get(name)

    def __str__(self):
        result = ''
        for key in self.index_table:
            result += key + ' ' + self.kind_table[key] + ' ' + self.type_table[key] + ' ' + \
                      str(self.index_table[key]) + '\n'
        return result
