__author__ = 'todokaist'

import re
import random

def sliceparent(op):
    inputtxt  = open(op+".txt", 'r')
    outputtxt = open(op+"-modi.txt", 'w')

    for line in inputtxt:
        p = re.compile("\(.*?\)|\[.*?\]")
        line = p.sub("", line)
        line = line.strip()
        outputtxt.write(line+'\n')

    inputtxt.close()
    outputtxt.close()

def mix(aText, bText):
    _aText = [line for line in list(open(aText, "r")) if line != '\n']
    _bText = [line for line in list(open(bText, "r")) if line != '\n']
    outputtxt = open("mixResult.txt", 'w')
    indexList = [0 for a in _aText]+[1 for b in _bText]
    random.shuffle(indexList)

    aCnt = 0
    bCnt = 0
    for index in indexList:
        if index == 0:
            outputtxt.write(_aText[aCnt])
            aCnt += 1
        else:
            outputtxt.write(_bText[bCnt])
            bCnt += 1
    outputtxt.close()
