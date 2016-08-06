#-*- coding: utf-8 -*-

'''
CS311 project 3
Python 3.5
'''

import re
import R2FA
import HanFA

# regExp
re_consonant = "1+1z+1c+2+q+2z+2zz+2zc+wz+wzz+wzc+a+sz+ac+az+azz+azc+s+w"
re_vowel = "3+3z+33+33z+e+ez+ee+eez+x+d+3d+33d+3zd+33zd"
re_total = "{0}+{1}".format(re_consonant, re_vowel)

class MM_3x4():

    def __init__(self, inDFA):
        self.state = inDFA.state
        self.t_func = inDFA.t_func
        self.init_state = inDFA.init_state
        self.final_state = inDFA.final_state
        self.alphabet = inDFA.alphabet
        self.o_func = {}
        for line in open("3x4tokey.txt", "r"):
            tokens = line.strip().split("\t")
            self.o_func[tokens[0]] = tokens[1]

    def is_accepted(self, string):
        if self.get_dest(string) in self.final_state:
            return True
        else:
            return False

    def translation(self, string):
        trans_string = ""
        sidx = 0
        pnt_state = self.init_state

        for idx in range(len(string)):
            next_state = self.t_func.get(pnt_state, string[idx])
            if next_state in self.final_state:
                try:
                    if self.t_func.get(next_state, string[idx+1]) not in self.final_state:
                        trans_string += self.o_func[string[sidx:(idx+1)]]
                        pnt_state = self.init_state
                        sidx = idx+1
                    else:
                        pnt_state = next_state
                except:
                    trans_string += self.o_func[string[sidx:(idx+1)]]
                    pnt_state = self.init_state
                    sidx = idx+1
        return trans_string

    def token_translation(self, var_str):
        g_an = re.compile("\w+")
        g_nan = re.compile("\W+")
        str_list = g_an.findall(var_str)
        non_str_list = g_nan.findall(var_str)

        trans_list = [self.translation(w) for w in str_list]

        return_str = ""
        if var_str[0].isalnum():
            for idx in range(len(str_list)):
                try:
                    return_str += trans_list[idx]+non_str_list[idx]
                except:
                    return_str += trans_list[idx]
        else:
            for idx in range(len(str_list)):
                try:
                    return_str += non_str_list[idx]+trans_list[idx]
                except:
                    return_str += non_str_list[idx]

        return return_str


def eval_settings(settings):
    print("eval settings...")
    try:
        l_set = [w.strip() for w in open(settings, "r")]
        if l_set[0] != "console":
            l_set[0] = open(l_set[0], "r")
            print("settings.txt found: '{0}', '{1}'".format(l_set[0].name, l_set[1]))
        else:
            print("settings.txt found: '{0}', '{1}'".format(l_set[0], l_set[1]))
        return l_set
    except:
        print("Default settings: 'console', 'with process'")
        return ["console", "with process"]

def open_program():
    for line in open("open.txt", "r"):
        print(line.strip())

def show_keypad():
    print()
    for line in open("keypad.txt", "r"):
        print(line.strip())
    print()

def main(settings):
    trans_MM = MM_3x4(R2FA.MM_3x4tokey(re_total))
    l_set = eval_settings(settings)

    mode = 0
    while mode not in ["1", "2"]:
        mode = str(input("select mode> "))
        if mode not in ["1", "2"]:
            print("This program do not provide 'mode {0}'".format(mode))

    test_hangeul = HanFA.HangeulAutomata("hangeulInput.txt", mode)
    test_string = ""
    while True:
        if l_set[0] == "console":
            input_string = input("mode: {0} type> ".format(mode))
        else:
            input_string = " ".join([w.strip() for w in l_set[0]])
            print("mode: {0} file typed: {1}".format(mode, input_string))
            l_set[0] = "console"

        if input_string == "":
            test_string = ""
        elif input_string == "/q":
            break
        elif "/d" in input_string:
            try:
                test_string = test_string[:-1]
            except:
                pass
        elif input_string == "/c":
            mode = ["2", "1"][int(mode)-1]
            test_hangeul = HanFA.HangeulAutomata("hangeulInput.txt", mode)
        elif input_string == "/k":
            show_keypad()
        else:
            test_string = test_string + input_string

        if l_set[1] == "only result":
            test_hangeul.print_str_3x4(test_string, trans_MM)
        else:
            test_hangeul.print_process_str_3x4(test_string, trans_MM)

if __name__ == '__main__':
    open_program()
    main("settings.txt")