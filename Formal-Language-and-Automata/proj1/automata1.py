__author__ = "DongKwan Kim"
# -*- coding: utf-8 -*-

'''
CS322 project 1
Python 3.5
'''

import re


class Hangeul:
    first = -1
    middle = -1
    last = 0

    prefix = ""
    pre2fix = ""

    A2H = dict([tuple(line.strip().split("\t")) for line in open("alphatohan.txt", "r")])

    union_unicode = list(open("fml_unicode.txt", "r"))
    first_unicode = dict([[l.strip().split("\t")[0], int(l.strip().split("\t")[1])] for l in union_unicode[:19]])
    middle_unicode = dict([[l.strip().split("\t")[0], int(l.strip().split("\t")[1])] for l in union_unicode[19:40]])
    last_unicode = dict([[l.strip().split("\t")[0], int(l.strip().split("\t")[1])] for l in union_unicode[40:]])

    def __init__(self):
        pass

    def clear(self):
        (self.first, self.middle, self.last) = (-1, -1, 0)

    # try는 한글로 시도, except는 영어로 시도
    def set_first(self, s):
        try:
            self.first = self.first_unicode[s]
        except:
            self.first = self.first_unicode[self.A2H[s]]

    def set_middle(self, s):
        try:
            self.middle = self.middle_unicode[s]
        except:
            self.middle = self.middle_unicode[self.A2H[s]]

    def set_last(self, s):
        try:
            self.last = self.last_unicode[s]
        except:
            self.last = self.last_unicode[self.A2H[s]]

    def get_last(self):
        return self.last

    def get_last_hangeul(self):
        l = [(i, a) for (a, i) in self.last_unicode.items()]
        return dict(l)[self.last]

    def set_prefix(self, s):
        self.prefix = s

    def clear_prefix(self):
        self.prefix = ""

    def set_pre2fix(self, s):
        self.pre2fix = s

    def get_pre2fix(self):
        return self.pre2fix

    def set_last_of_pre2fix(self, s):
        self.pre2fix = str(self.pre2fix)
        f_unicode = ((ord(self.pre2fix) - 44032) % 588) % 28
        self.pre2fix = chr(ord(self.pre2fix)-f_unicode+self.last_unicode[s])

    def get_last_of_pre2fix(self):
        l = [(i, a) for (a, i) in self.last_unicode.items()]
        return dict(l)[((ord(self.pre2fix) - 44032) % 588) % 28]

    def clear_pre2fix(self):
        self.pre2fix = ""

    def comb(self):
        if self.first == -1 or self.middle == -1:
            if self.first != -1:
                l = [(i, a) for (a, i) in self.first_unicode.items()]
                return self.prefix+dict(l)[self.first]
            elif self.middle != -1:
                l = [(i, a) for (a, i) in self.middle_unicode.items()]
                return self.prefix+dict(l)[self.middle]
            else:
                return ""
        return self.prefix+chr(self.first*588 + self.middle*28 + self.last + 44032)


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
                self.t_func = TransitionFunction(line)
        self.o_func = OutputFunction()

    def print_str(self, string):
        pnt_state = self.init_state
        out_str = ""
        for char in string:
            out_str += self.o_func.get(pnt_state, char, "", "")
            pnt_state = self.t_func.get(pnt_state, char)
        print(out_str)

    def print_path(self, string):
        pnt_state = self.init_state
        out_str = pnt_state
        for char in string:
            out_str += self.t_func.get(pnt_state, char)
            pnt_state = self.t_func.get(pnt_state, char)
        print(out_str)


class HangeulAutomata(MealyMachine):
    def __init__(self, f, mode):
        MealyMachine.__init__(self, f)
        if mode == "1":
            self.o_func = LastOutputFunction()
        elif mode == "2":
            self.o_func = FirstOutputFunction()

    def print_str(self, string):
        self.o_func.clear_hangeul()
        pnt_state = self.init_state
        out_str = ""
        for char in string:
            next_state = self.t_func.get(pnt_state, char)
            out_str = self.o_func.get(pnt_state, char, out_str, next_state)
            pnt_state = next_state
        print(out_str)

    def print_process_str(self, string):
        self.o_func.clear_hangeul()
        pnt_state = self.init_state
        out_str = ""
        for char in string:
            next_state = self.t_func.get(pnt_state, char)
            out_str = self.o_func.get(pnt_state, char, out_str, next_state)
            pnt_state = next_state
            print(out_str)


class TransitionFunction():
    # present_state | input -> Next_state
    g = re.compile("\w+ \| \w+ -> \w+")

    def __init__(self, input_data):
        self.input_data = input_data
        func_list = self.g.findall(self.input_data)

        self.g_dic = {}
        for fu in func_list:
            lst = fu.split(" ")
            if not lst[0] in self.g_dic.keys():
                self.g_dic[lst[0]] = {}
            self.g_dic[lst[0]][lst[2]] = lst[4]
            self.g_dic[lst[0]][" "] = "S"

    def get(self, present_state, inp):
        if not inp.isalpha():
            return "S"
        try:
            return self.g_dic[present_state][inp]
        except:
            error_msg = "Invalid transition function >>> present_state: {0}  input: {1}".format(present_state, inp)
            raise(Exception(error_msg))


class OutputFunction():
    han = Hangeul()

    def __init__(self):
        pass

    def get(self, present_state, inp, out_str, next_state):
        pass

    def clear_hangeul(self):
        self.han.clear()

    def not_have_func(self, inp, nstate):
        self.han.clear()
        if nstate == "S":
            self.han.set_middle(inp)
        elif nstate == "V":
            self.han.set_first(inp)

    def S(self, inp, nstate):
        self.han.clear()
        if nstate == "V":
            self.han.set_first(inp)
        else:
            self.han.set_middle(inp)

    def V(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.han.set_middle(inp)
        else:
            self.han.set_first(inp)


class LastOutputFunction(OutputFunction):

    def __init__(self):
        OutputFunction.__init__(self)

    def get(self, present_state, inp, out_str, next_state):
        if not inp.isalpha():
            self.han.clear()
            return out_str+inp
        try:
            eval("self.{0}('{1}', '{2}')".format(present_state, inp, next_state))
            han_comb = self.han.comb()
            self.han.clear_prefix()

            if next_state not in ["S", "V"]:
                return out_str[:-1] + han_comb
            else:
                return out_str + han_comb
        except:
            error_msg = "Invalid output function >>> present_state: {0}  input: {1}".format(present_state, inp)
            raise(Exception(error_msg))

    def O(self, inp, nstate):
        if nstate == "I":
            dv = {"l": "ㅚ", "k": "ㅘ", "o": "ㅙ"}
            self.han.set_middle(dv[inp])
        elif nstate in ["K", "N", "R", "L"]:
            self.han.set_last(inp)
        else:
            self.not_have_func(inp, nstate)

    def U(self, inp, nstate):
        if nstate == "I":
            dv = {"l": "ㅟ", "j": "ㅝ", "p": "ㅞ"}
            self.han.set_middle(dv[inp])
        elif nstate in ["K", "N", "R", "L"]:
            self.han.set_last(inp)
        else:
            self.not_have_func(inp, nstate)

    def A(self, inp, nstate):
        if nstate == "I":
            self.han.set_middle("ㅢ")
        elif nstate in ["K", "N", "R", "L"]:
            self.han.set_last(inp)
        else:
            self.not_have_func(inp, nstate)

    def I(self, inp, nstate):
        if nstate in ["K", "N", "R", "L"]:
            self.han.set_last(inp)
        else:
            self.not_have_func(inp, nstate)

    def KNRL2OUAI(self, inp):
        dc = {"ㄳ": ("ㄱ", "ㅅ"), "ㅄ": ("ㅂ", "ㅅ"), "ㄵ": ("ㄴ", "ㅈ"), "ㄶ": ("ㄴ", "ㅎ"),
              "ㄺ": ("ㄹ", "ㄱ"), "ㄻ": ("ㄹ", "ㅁ"), "ㄼ": ("ㄹ", "ㅂ"), "ㄽ": ("ㄹ", "ㅅ"),
              "ㄾ": ("ㄹ", "ㅌ"), "ㄿ": ("ㄹ", "ㅍ"), "ㅀ": ("ㄹ", "ㅎ")}
        temp_last = self.han.get_last_hangeul()
        if temp_last in dc.keys():
            self.han.set_last(dc[temp_last][0])
            temp_last = dc[temp_last][1]
        else:
            self.han.set_last("null")
        self.han.set_prefix(self.han.comb())
        self.han.set_first(temp_last)
        self.han.set_middle(inp)
        self.han.set_last("null")

    def KNRL2V(self, inp):
        self.han.clear()
        self.han.set_first(inp)

    def K(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.KNRL2OUAI(inp)
        elif nstate == "L":
            dc = {"ㄱ": "ㄳ", "ㅂ": "ㅄ"}
            self.han.set_last(dc[self.han.get_last_hangeul()])
        elif nstate == "V":
            self.KNRL2V(inp)

    def N(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.KNRL2OUAI(inp)
        elif nstate == "L":
            dc = {"w": "ㄵ", "g": "ㄶ"}
            self.han.set_last(dc[inp])
        elif nstate == "V":
            self.KNRL2V(inp)

    def R(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.KNRL2OUAI(inp)
        elif nstate == "L":
            dc = {"r": "ㄺ", "a": "ㄻ", "q": "ㄼ",
                  "t": "ㄽ", "x": "ㄾ", "v": "ㄿ", "g": "ㅀ"}
            self.han.set_last(dc[inp])
        elif nstate == "V":
            self.KNRL2V(inp)

    def L(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.KNRL2OUAI(inp)
        elif nstate == "V":
            self.KNRL2V(inp)


class FirstOutputFunction(OutputFunction):

    dc = {("ㄱ", "ㅅ"): "ㄳ", ("ㅂ", "ㅅ"): "ㅄ", ("ㄴ", "ㅈ"): "ㄵ", ("ㄴ", "ㅎ"): "ㄶ",
          ("ㄹ", "ㄱ"): "ㄺ", ("ㄹ", "ㅁ"): "ㄻ", ("ㄹ", "ㅂ"): "ㄼ", ("ㄹ", "ㅅ"): "ㄽ",
          ("ㄹ", "ㅌ"): "ㄾ", ("ㄹ", "ㅍ"): "ㄿ", ("ㄹ", "ㅎ"): "ㅀ"}

    def __init__(self):
        OutputFunction.__init__(self)

    def get(self, present_state, inp, out_str, next_state):
        if not inp.isalpha():
            return self.not_alpha_input(present_state, inp, out_str)
        try:
            eval("self.{0}('{1}', '{2}')".format(present_state, inp, next_state))
            han_comb = self.han.comb()
            self.han.clear_prefix()

            if present_state in ["K", "N", "R", "L"] and next_state in ["V"]:
                self.han.clear_pre2fix()
            elif next_state in ["O", "U", "A", "I"]:
                self.han.clear_pre2fix()

            if present_state in ["K", "N", "R", "L"] and next_state in ["L", "V"]:
                return out_str[:-2] + han_comb
            elif next_state in ["O", "U", "A", "I"]:
                return out_str[:-1] + han_comb
            else:
                return out_str + han_comb
        except:
            error_msg = "Invalid output function >>> present_state: {0}  input: {1}".format(present_state, inp)
            raise(Exception(error_msg))

    def not_alpha_input(self, present_state, inp, out_str):
        temp = out_str
        if present_state in ["K", "N", "R", "L"]:
            if self.han.get_last_of_pre2fix() == "null":
                self.han.set_last_of_pre2fix(self.han.comb())
            else:
                double_last = self.dc[(self.han.get_last_of_pre2fix(), self.han.comb())]
                self.han.set_last_of_pre2fix(double_last)
            temp = out_str[:-2] + self.han.get_pre2fix()
        self.han.clear()
        return temp+inp

    def OUAI2KNRL(self, inp):
        self.han.set_pre2fix(self.han.comb())
        self.han.clear()
        self.han.set_first(inp)

    def O(self, inp, nstate):
        if nstate == "I":
            dv = {"l": "ㅚ", "k": "ㅘ", "o": "ㅙ"}
            self.han.set_middle(dv[inp])
        elif nstate in ["K", "N", "R", "L"]:
            self.OUAI2KNRL(inp)
        else:
            self.not_have_func(inp, nstate)

    def U(self, inp, nstate):
        if nstate == "I":
            dv = {"l": "ㅟ", "j": "ㅝ", "p": "ㅞ"}
            self.han.set_middle(dv[inp])
        elif nstate in ["K", "N", "R", "L"]:
            self.OUAI2KNRL(inp)
        else:
            self.not_have_func(inp, nstate)

    def A(self, inp, nstate):
        if nstate == "I":
            self.han.set_middle("ㅢ")
        elif nstate in ["K", "N", "R", "L"]:
            self.OUAI2KNRL(inp)
        else:
            self.not_have_func(inp, nstate)

    def I(self, inp, nstate):
        if nstate in ["K", "N", "R", "L"]:
            self.OUAI2KNRL(inp)
        else:
            self.not_have_func(inp, nstate)

    def KNR2VL(self, inp):
        self.han.set_last_of_pre2fix(self.han.comb())
        self.han.set_prefix(self.han.get_pre2fix())
        self.han.clear()
        self.han.set_first(inp)

    def K(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.han.set_middle(inp)
        elif nstate in ["L", "V"]:
            self.KNR2VL(inp)

    def N(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.han.set_middle(inp)
        elif nstate in ["L", "V"]:
            self.KNR2VL(inp)

    def R(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.han.set_middle(inp)
        elif nstate in ["L", "V"]:
            self.KNR2VL(inp)

    def L(self, inp, nstate):
        if nstate in ["O", "U", "A", "I"]:
            self.han.set_middle(inp)
        elif nstate == "V":
            self.L2V(inp)

    def L2V(self, inp):
        if self.han.get_last_of_pre2fix() == "null":
            self.KNR2VL(inp)
        else:
            double_last = self.dc[(self.han.get_last_of_pre2fix(), self.han.comb())]
            self.han.set_last_of_pre2fix(double_last)
            self.han.set_prefix(self.han.get_pre2fix())
            self.han.clear()
            self.han.set_first(inp)


# Test code
'''
settings.txt: %s\n%s
1. input way = {"console", "{filename}"}
2. print way = {"only result", "with process"}
'''
def eval_settings(settings):
    try:
        l_set = [w.strip() for w in open(settings, "r")]
        if l_set[0] != "console":
            l_set[0] = open(l_set[0], "r")
        print("settings.txt found: '{0}', '{1}'".format(l_set[0].name, l_set[1]))
        return l_set
    except:
        return ["console", "with process"]

def main(settings):
    l_set = eval_settings(settings)

    mode = 0
    while mode not in ["1", "2"]:
        mode = str(input("select mode: "))
        if mode not in ["1", "2"]:
            print("This program do not provide 'mode {0}'".format(mode))

    test_hangeul = HangeulAutomata("hangeulInput.txt", mode)
    test_string = ""
    while True:
        if l_set[0] == "console":
            input_string = input("mode: {0} type: ".format(mode))
        else:
            input_string = " ".join([w.strip() for w in l_set[0]])
            print("mode: {0} file typed: {1}".format(mode, input_string))
            l_set[0] = "console"

        if input_string == "":
            test_string = ""
        elif input_string == "/q":
            break
        elif input_string == "/d":
            try:
                test_string = test_string[:-1]
            except:
                pass
        elif input_string == "/c":
            mode = ["2", "1"][int(mode)-1]
            test_hangeul = HangeulAutomata("hangeulInput.txt", mode)
        else:
            test_string = test_string + input_string

        if l_set[1] == "only result":
            test_hangeul.print_str(test_string)
        else:
            test_hangeul.print_process_str(test_string)

def open_program():
    for line in open("open.txt", "r"):
        print(line.strip())

if __name__ == '__main__':
    open_program()
    main("settings.txt")