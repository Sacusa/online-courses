#!/usr/bin/python
import sys
import os
import CompilationEngine

input_path = sys.argv[1]
input_files = []

# if the input path is a directory, compile all the contained jack files
if os.path.isdir(input_path):
    for dir_file in os.listdir(input_path):
        if os.path.isfile(input_path + '/' + dir_file) and dir_file.endswith('.jack'):
            input_files.append(input_path + '/' + dir_file)
# else, compile the given file only
elif os.path.isfile(input_path) and input_path.endswith('.jack'):
    input_files.append(input_path)

for input_file in input_files:
    engine = CompilationEngine.CompilationEngine(input_file, input_file.rstrip('.jack') + '.vm')
