'''
CS311 project 2
Python 3.5
'''

import re
import FA
import ply.lex as lex
import ply.yacc as yacc


class ASTNode():

    def __init__(self, val=None, children=None):
        if children:
            self.children = children
        else:
            self.children = []
        self.val = val
        self.eNFA = PreeNFA()

    def num_child(self):
        return len(self.children)

    def is_leaf(self):
        return self.children == []


class PreeNFA:

    def __init__(self):
        self.state = []
        self.alphabet = []
        self.init_state = None
        self.final_state = []
        self.t_func_list = {}

    def add_state(self):
        state = len(self.state)
        self.state.append(state)
        return state

    def add_alphabet(self, inp):
        self.alphabet.append(inp)
        self.alphabet = FA.sls(self.alphabet)

    def combine_s_a_f(self, enfa):
        ls = len(self.state)
        # combine states
        self.state += [i + ls for i in enfa.state]
        enfa.init_state += ls
        enfa.final_state = [i + ls for i in enfa.final_state]
        # combine alphabet
        self.alphabet += enfa.alphabet
        self.alphabet = FA.sls(self.alphabet)
        # combine t_func
        temp_t_func_list = []
        for (k, v) in enfa.t_func_list.items():
            temp_t_func_list.append(((k[0] + ls, k[1] + ls), v))
        self.t_func_list = dict(list(self.t_func_list.items()) + temp_t_func_list)

    def make_uni(self, a1):
        init_state = self.add_state()
        final_state = self.add_state()
        self.add_alphabet(a1)
        self.init_state = init_state
        self.final_state = [final_state]
        self.t_func_list[(init_state, final_state)] = a1

    def combine_alter(self, enfa):
        self.combine_s_a_f(enfa)
        init_state = self.add_state()
        final_state = self.add_state()

        self.t_func_list[(init_state, self.init_state)] = "?"
        self.t_func_list[(init_state, enfa.init_state)] = "?"
        self.t_func_list[(self.final_state[0], final_state)] = "?"
        self.t_func_list[(enfa.final_state[0], final_state)] = "?"

        self.init_state = init_state
        self.final_state = [final_state]
        return self

    def combine_concat(self, enfa):
        self.combine_s_a_f(enfa)
        init_state = self.add_state()
        final_state = self.add_state()

        self.t_func_list[(init_state, self.init_state)] = "?"
        self.t_func_list[(self.final_state[0], enfa.init_state)] = "?"
        self.t_func_list[(enfa.final_state[0], final_state)] = "?"

        self.init_state = init_state
        self.final_state = [final_state]
        return self

    def combine_star(self):
        init_state = self.add_state()
        final_state = self.add_state()

        self.t_func_list[(init_state, self.init_state)] = "?"
        self.t_func_list[(self.final_state[0], final_state)] = "?"
        self.t_func_list[(init_state, final_state)] = "?"
        self.t_func_list[(self.final_state[0], self.init_state)] = "?"

        self.init_state = init_state
        self.final_state = [final_state]
        return self

    def make_eNFA_file(self):
        output_nfa = open("NFA.txt", "w")
        str_t_func_list = [str(k[0]) + " | " + str(v) + " -> " + str(k[1]) for (k, v) in self.t_func_list.items()]
        output_nfa.writelines("<state> " + ", ".join(str(s) for s in self.state) + "\n")
        output_nfa.writelines("<alphabet> " + ", ".join(str(a) for a in self.alphabet) + "\n")
        output_nfa.writelines("<t_func> " + ", ".join(str_t_func_list) + "\n")
        output_nfa.writelines("<init_state> " + str(self.init_state) + "\n")
        output_nfa.writelines("<final_state> " + ", ".join(str(s) for s in self.final_state))
        output_nfa.close()

        output_nfa = open("NFA.txt", "r")
        for line in output_nfa:
            print(line.strip())
        output_nfa.close()

# List of tokens
tokens = ('ALTER', 'STAR', 'LPAREN', 'RPAREN', 'ALPHA')
t_ALTER = '\+'
t_STAR = '\*'
t_LPAREN = '\('
t_RPAREN = '\)'
t_ALPHA = '[a-zA-Z0-9?]'

# function lex
def t_error(t):
    print("Illegal character {0}".format(t.value[0]))
    t.lexer.skip(1)


# precedence of yacc
precedence = (('left', 'ALTER'),
            ('left', 'ALPHA'),
            ('left', 'CONCAT'),
            ('right', 'STAR'),)

root_node_list = []

# function yacc
def p_expression_alpha(t):
    'expression : ALPHA'
    t[0] = ASTNode(val=t[1])
    root_node_list.append(t[0])

def p_expression_group(t):
    'expression : LPAREN expression RPAREN'
    t[0] = t[2]
    root_node_list.append(t[0])

def p_expression_alter(t):
    'expression : expression ALTER expression'
    t[0] = ASTNode(t[2], [t[1],t[3]])
    root_node_list.append(t[0])

def p_expression_concat(t):
    'expression : expression expression %prec CONCAT'
    t[0] = ASTNode("&", [t[1], t[2]])
    root_node_list.append(t[0])

def p_expression_star(t):
    'expression : expression STAR'
    t[0] = ASTNode(t[2], [t[1]])
    root_node_list.append(t[0])

def p_error(t):
    print("Syntax error at '%s'" % t.value)


def traverse(node, i):
    if node.is_leaf():
        print(str(i)+" leaf: " + node.val)
        return [node.val]
    else:
        for n in node.children:
            traverse(n, i+1)
        print(str(i)+" val: " + node.val)

def traverse_to_make_eNFA(node):
    if node.num_child() == 0:
        node.eNFA.make_uni(node.val)
    elif node.num_child() == 1:
        traverse_to_make_eNFA(node.children[0])
        node.eNFA = node.children[0].eNFA.combine_star()
    else:
        for n in node.children:
            traverse_to_make_eNFA(n)
        if node.val == "&":
            node.eNFA = node.children[0].eNFA.combine_concat(node.children[1].eNFA)
        elif node.val == "+":
            node.eNFA = node.children[0].eNFA.combine_alter(node.children[1].eNFA)

def open_program():
    for line in open("open.txt", "r"):
        print(line.strip())

# Test code
if __name__ == '__main__':
    open_program()
    data = input("Input Regexp> ")

    lexer = lex.lex()
    lexer.input(data)

    parser = yacc.yacc()
    parser.parse(data)

    print("\n############ Tree Traversal ############")
    root_node = root_node_list.pop()
    traverse(root_node, 0)

    print("\n############ eNFA ############")
    traverse_to_make_eNFA(root_node)
    root_node.eNFA.make_eNFA_file()

    test_NFA = FA.NFA("NFA.txt")
    test_DFA = test_NFA.NFA_2_DFA()
    print("\n############ DFA ############")
    test_DFA.prt_DFA()

    test_m_DFA = test_NFA.NFA_2_mDFA()
    print("\n############ minimized DFA ############")
    test_m_DFA.prt_DFA()

    while True:
        test_string = str(input("\ntype string: "))
        print(test_m_DFA.is_accepted(test_string))