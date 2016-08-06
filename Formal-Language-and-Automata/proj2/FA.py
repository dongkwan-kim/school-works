'''
CS311 pre-project 2-1 as module FA(Finite Automata)
Python 3.5
'''

import re


def sls(l):
    return sorted(list(set(l)))


def is_inter_empty(l1, l2):
    return list(set(l1).intersection(set(l2))) == []


def alphabet_2_string(alphabet, length):
    string = [""]
    tmp_string = []
    for i in range(length):
        for s in string:
            for a in alphabet:
                tmp_string.append(s + str(a))
        string = [w for w in tmp_string]
    return  [""] + string


class NFA:

    def __init__(self, f):
        self.name = f.split(".txt")[0]
        g = re.compile("\w+")
        for line in open(f, "r"):
            parsed = g.findall(line)
            if parsed[0] == 'state':
                self.state = parsed[1:]
            elif parsed[0] == "alphabet":
                self.alphabet = parsed[1:] + ["?"]
            elif parsed[0] == "init_state":
                self.init_state = parsed[1]
            elif parsed[0] == "final_state":
                self.final_state = parsed[1:]
            elif parsed[0] == "t_func":
                self.t_func = TransitionFunctionNFA(line, self.state)

    def get_closure(self, state_list, marked_list):
        closure_dic = {}
        for st in state_list:
            if "?" in self.t_func.g_dic[st].keys():
                marked_list.append(st)
                new_state_list = sls(self.t_func.get(st, "?"))
                for emt in new_state_list:
                    if emt in marked_list:
                        new_state_list.remove(emt)
                closure_dic[st] = sls([st] + self.union_v(self.get_closure(new_state_list, marked_list)))
            else:
                closure_dic[st] = [st]
            marked_list.clear()
        return closure_dic

    def union_v(self, closure):
        return_list = []
        for v in closure.values():
            return_list += v
        return return_list

    def get_next_closure_state(self, p_state_list, inp, closure):
        n_c_s_list = []
        n_c_list = []
        for parse_st in p_state_list:
            if inp in self.t_func.g_dic[parse_st].keys():
                n_c_s_list += self.t_func.get(parse_st, inp)
        for (cl, p_cl) in closure.items():
            if cl in n_c_s_list:
                n_c_list += p_cl
        return sls(n_c_list)

    def NFA_2_DFA(self):
        closure = self.get_closure(self.state, [])
        d_alphabet = self.alphabet[:-1]
        d_init_state = "or".join(closure[self.init_state])
        d_state = {d_init_state: closure[self.init_state]}
        d_t_func = []
        new_state = dict([w for w in d_state.items()])
        while len(new_state) != 0:
            # make list of next state
            tmp_state = {}
            for (dst, parse_st) in new_state.items():
                for inp in d_alphabet:
                    n_c_s_list = self.get_next_closure_state(parse_st, inp, closure)

                    if len(n_c_s_list) == 0:
                        # If there is no transition function, add empty state
                        new_state = "empty"
                    else:
                        new_state = "or".join(n_c_s_list)

                    new_func = dst + " | " + inp + " -> " + new_state
                    if new_state in d_state.keys():
                        d_t_func.append(new_func)
                    else:
                        # input new state to d_state && input new transition function
                        d_state[new_state] = n_c_s_list
                        tmp_state[new_state] = n_c_s_list
                        d_t_func.append(new_func)
            new_state = tmp_state

        d_final_state = ["or".join(d_state[st]) for (st, cl) in d_state.items() if not is_inter_empty(cl, self.final_state)]

        output_dfa = open("DFA(from "+self.name+").txt", "w")
        output_dfa.writelines("<state> " + ", ".join(d_state.keys()) + "\n")
        output_dfa.writelines("<alphabet> " + ", ".join(d_alphabet) + "\n")
        output_dfa.writelines("<t_func> " + ", ".join(d_t_func) + "\n")
        output_dfa.writelines("<init_state> " + d_init_state + "\n")
        output_dfa.writelines("<final_state> " + ", ".join(d_final_state))
        output_dfa.close()
        return DFA("DFA(from "+self.name+").txt")

    def NFA_2_mDFA(self):
        temp_DFA = self.NFA_2_DFA()
        possble_input = alphabet_2_string(temp_DFA.alphabet, len(temp_DFA.state) - 1)
        relation_state = sls([tuple(sorted((a, b))) for a in temp_DFA.state for b in temp_DFA.state if a != b])
        filling_table = dict([(rel, True) for rel in relation_state])

        for (a, b) in relation_state:
            for p_inp in possble_input:
                a_dest = temp_DFA.get_dest_of_state(a, p_inp)
                b_dest = temp_DFA.get_dest_of_state(b, p_inp)
                if (a_dest in temp_DFA.final_state) ^ (b_dest in temp_DFA.final_state):
                    filling_table[(a, b)] = False
                    break

        for (s, tf) in filling_table.items():
            if tf and (s[0] in temp_DFA.state) and (s[1] in temp_DFA.state):
                temp_DFA.unify_states(s[0], s[1])

        return temp_DFA


class TransitionFunctionNFA():
    # present_state | input -> Next_state
    g = re.compile("\w+ \| \S+ -> \w+")
    g_dic = {}

    def __init__(self, input_data, input_state):
        self.input_data = input_data
        func_list = self.g.findall(self.input_data)
        for fu in func_list:
            lst = fu.split(" ")
            if lst[0] in self.g_dic.keys():
                if lst[2] in self.g_dic[lst[0]].keys():
                    self.g_dic[lst[0]][lst[2]].append(lst[4])
                else:
                    self.g_dic[lst[0]][lst[2]] = []
                    self.g_dic[lst[0]][lst[2]].append(lst[4])
            else:
                self.g_dic[lst[0]] = {}
                self.g_dic[lst[0]][lst[2]] = []
                self.g_dic[lst[0]][lst[2]].append(lst[4])
        for st in input_state:
            if st not in self.g_dic.keys():
                self.g_dic[st] = {}

    def get(self, present_state, inp):
        try:
            return self.g_dic[present_state][inp]
        except:
            print("Invalid variable >>> present_state: {0}  input: {1}".format(present_state, inp))
            return None


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
                self.t_func = TransitionFunctionDFA(line)

    def is_accepted(self, string):
        if self.get_dest(string) in self.final_state:
            return True
        else:
            return False

    def get_dest(self, string):
        return self.get_dest_of_state(self.init_state, string)

    def get_dest_of_state(self, arbit_state, string):
        pnt_state = arbit_state
        for char in string:
            pnt_state = self.t_func.get(pnt_state, char)
        return pnt_state

    def unify_states(self, a, b):
        for (inp, n_state) in self.t_func.g_dic[b].items():
            self.t_func.g_dic[a][inp] = n_state
        for (s_state, inp_n_state) in self.t_func.g_dic.items():
            for (inp, n_state) in inp_n_state.items():
                if n_state == b:
                    self.t_func.g_dic[s_state][inp] = a
        del self.t_func.g_dic[b]

        del self.state[self.state.index(b)]

        if b in self.final_state:
            del self.final_state[self.final_state.index(b)]
        if b == self.init_state:
            self.init_state = a

    def prt_DFA(self):
        print("<states> " + str(self.state))
        print("<alphabet> " + str(self.alphabet))
        print("<transition function>")
        self.t_func.print_t_func("\t")
        print("<initial state> " + self.init_state)
        print("<final state> " + str(self.final_state))


class TransitionFunctionDFA:
    # present_state | input -> Next_state
    g = re.compile("\w+ \| \S+ -> \w+")
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

    def get(self, present_state, inp):
        try:
            return self.g_dic[present_state][inp]
        except:
            print("Invalid variable >>> present_state: {0}  input: {1}".format(present_state, inp))
            return None

    def print_t_func(self, margin):
        for (i, alpha_f) in self.g_dic.items():
            for (alpha, f) in alpha_f.items():
                print(margin + "{0} | {1} -> {2}".format(i, alpha, f))