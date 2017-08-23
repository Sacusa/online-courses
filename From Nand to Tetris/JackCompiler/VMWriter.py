#!/usr/bin/python

class VMWriter(object):
    """ VM Writer
    Provides the API to write Jack VM commands into the given file.
    """

    SEGMENT_LIST = ['constant', 'argument', 'local', 'static', 'this', 'that', 'pointer', 'temp']
    OP_COMMAND_MAP = {'+':'add', '-':'sub', '=':'eq', '>':'gt', '<':'lt', '&':'and', '|':'or'}

    def __init__(self, filename):
        """ Open the filename to write VM code into
        """
        self.output_file = open(filename, 'w')

    def write_push(self, segment, index):
        """ Writes "push <segment> <index>"
        """
        if segment not in VMWriter.SEGMENT_LIST:
            raise RuntimeError('invalid segment: ' + segment)
        self.output_file.write('push ' + segment + ' ' + str(index) + '\n')

    def write_pop(self, segment, index):
        """ Writes "pop <segment> <index>"
        """
        if segment not in VMWriter.SEGMENT_LIST:
            raise RuntimeError('invalid segment: ' + segment)
        self.output_file.write('pop ' + segment + ' ' + str(index) + '\n')

    def write_arithmetic(self, command):
        """ Writes "<command>" for arithmetic and logical operators.
        For "not" and "neg", pass them explicitly.
        """
        if command in VMWriter.OP_COMMAND_MAP:
            self.output_file.write(VMWriter.OP_COMMAND_MAP[command] + '\n')
        elif command == 'neg' or command == 'not':
            self.output_file.write(command + '\n')
        elif command == '*':
            self.write_call('Math.multiply', 2)
        elif command == '/':
            self.write_call('Math.divide', 2)
        else:
            raise RuntimeError('invalid command: ' + command)

    def write_label(self, label):
        """ Writes "label <label>"
        """
        self.output_file.write('label ' + label + '\n')

    def write_goto(self, label):
        """ Writes "goto <label>"
        """
        self.output_file.write('goto ' + label + '\n')

    def write_if(self, label):
        """ Writes "if-goto <label>"
        """
        self.output_file.write('if-goto ' + label + '\n')

    def write_call(self, name, nArgs):
        """ Writes "call <name> <nArgs>"
        """
        self.output_file.write('call ' + name + ' ' + str(nArgs) + '\n')

    def write_function(self, name, nLocals):
        """ Writes "function <name> <nLocals>"
        """
        self.output_file.write('function ' + name + ' ' + str(nLocals) + '\n')

    def write_return(self):
        """ Writes "return"
        """
        self.output_file.write('return\n')

    def close(self):
        """ Closes the output VM file.
        """
        self.output_file.close()
