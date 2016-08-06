'''
CS311 pre-project 1-1
Python 3.5
'''

import re

class DFA:

    def __init__(self, f):
        g = re.compile("\w+")
        for line in open(f, "r"):
            parsed = g.findall(line)
            if parsed[0] == 'state':
                self.state = parsed[1:]
            elif parsed[0] == "alphabet":
                self.alphabet = parsed[1:]
            elif parsed[0] == "init_state":
                self.init_state = parsed[1]
            elif parsed[0] == "final_state":
                self.final_state = parsed[1:]
            elif parsed[0] == "t_func":
                self.t_func = transition_function(line)


    def isAccepted(self, string):
        if not string.isalnum():
            print("String: only alphabet, number, hangeul")
            return False

        if self.getDest(string) in self.final_state:
            return True
        else:
            return False

    def getDest(self, string):
        pnt_state = self.init_state
        for char in string:
            pnt_state = self.t_func.get(pnt_state, char)
        return pnt_state

class transition_function:
    # present_state | input -> Next_state
    g = re.compile("\w+ \| \w+ -> \w+")
    g_dic = {}

    def __init__(self, input_data):

        self.input_data = input_data
        func_list = self.g.findall(self.input_data)

        for fu in func_list:
            lst = fu.split(" ")
            if lst[0] in self.g_dic.keys():
                self.g_dic[lst[0]][lst[2]] = lst[4]
            else:
                self.g_dic[lst[0]] = {}
                self.g_dic[lst[0]][lst[2]] = lst[4]

    def get(self, present_state, input):
        try:
            return self.g_dic[present_state][input]
        except:
            print("Invalid variable >>> present_state: {0}  input: {1}".format(present_state, input))
            return None

    def printFunc(self):
        for (i, alpha_f) in self.g_dic.items():
            for (alpha, f) in alpha_f.items():
                print("{0} | {1} -> {2}".format(i, alpha, f))

# Test code
if __name__ == '__main__':
    while(True):
        test_string = str(input("type string: "))
        test_DFA = DFA("input.txt")
        print(test_DFA.isAccepted(test_string))
