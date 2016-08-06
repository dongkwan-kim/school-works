'''
CS311 pre-project 1-2
Python 3.5
'''

import re

class MealyMachine:

    def __init__(self, f):
        g = re.compile("\w+")
        for line in open(f, "r"):
            parsed = g.findall(line)
            if parsed[0] == 'state':
                self.state = parsed[1:]
            elif parsed[0] == "in_alphabet":
                self.in_alphabet = parsed[1:]
            elif parsed[0] == "out_alphabet":
                self.out_alphabet = parsed[1:]
            elif parsed[0] == "init_state":
                self.init_state = parsed[1]
            elif parsed[0] == "t_func":
                self.t_func = transition_function(line)
            elif parsed[0] == "o_func":
                self.o_func = output_function(line)

    def printStr(self, string):
        pnt_state = self.init_state
        out_str = ""
        for char in string:
            out_str += self.o_func.get(pnt_state, char)
            pnt_state =self.t_func.get(pnt_state, char)
        print(out_str)

class general_function:
    g = re.compile("\w+ \| \w+ -> \w+")

    def __init__(self, input_data):
        self.input_data = input_data
        func_list = self.g.findall(self.input_data)

        self.g_dic = {}
        for fu in func_list:
            lst = fu.split(" ")
            if lst[0] in self.g_dic.keys():
                self.g_dic[lst[0]][lst[2]] = lst[4]
            else:
                self.g_dic[lst[0]] = {}
                self.g_dic[lst[0]][lst[2]] = lst[4]

    def printFunc(self):
        for (i, alpha_f) in self.g_dic.items():
            for (alpha, f) in alpha_f.items():
                print("{0} | {1} -> {2}".format(i, alpha, f))


class transition_function(general_function):
    # present_state | input -> Next_state
    def __init__(self, input_data):
        general_function.__init__(self, input_data)

    def get(self, present_state, input):
        try:
            return self.g_dic[present_state][input]
        except:
            error_msg = "Invalid transition function >>> present_state: {0}  input: {1}".format(present_state, input)
            raise(Exception(error_msg))

class output_function(general_function):
    # present_state | input -> output_alphabet
    def __init__(self, input_data):
        general_function.__init__(self, input_data)

    def get(self, present_state, input):
        try:
            return self.g_dic[present_state][input]
        except:
            error_msg = "Invalid output function >>> present_state: {0}  input: {1}".format(present_state, input)
            raise(Exception(error_msg))


# Test code
if __name__ == '__main__':

    while(True):
        test_string = str(input("type string: "))
        test_MM = MealyMachine("input.txt")
        test_MM.printStr(test_string)
