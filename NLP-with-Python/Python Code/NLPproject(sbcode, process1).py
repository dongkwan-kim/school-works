__author__ = 'hogumac'

'''
Python 2.7
2015.06.10
assume that extra is not considered
'''
import re

import math

import nltk
from nltk.corpus import brown
from nltk.corpus import stopwords

from collections import defaultdict

def fileToWordList(file):
    returnList = []
    for line in file:
        for word in line.split():
            if re.search("\W$", word):
                if word[-1] == ":":
                    returnList.append(word)
                else:
                    returnList.append(re.split("\W+$", word)[0])
            else:
                returnList.append(word)
    return returnList

def fileToWordList2(file, cnt):
    # 722
    returnList = []

    for line in file:

        if cnt <= 0:
            break

        for word in line.split():
            if re.search("\W$", word):
                if word[-1] == ":":
                    returnList.append(word)
                else:
                    returnList.append(re.split("\W+$", word)[0])
            else:
                returnList.append(word)
        cnt -= 1

    returnList = [w for w in returnList if w is not ""]
    return returnList

def classifySpeaker(wordList):
    speakerList = list(set([word[:-1] for word in wordList if word[0].istitle() and word[-1] == ":"]))
    returnDict = defaultdict(list)
    tempSpeaker = ""
    for word in wordList:
        if word[:-1] in speakerList:
            tempSpeaker = word
        else:
            returnDict[tempSpeaker[:-1]].append(word.lower())

    try: del returnDict['Sidney']
    except: pass

    try: del returnDict['Melanie']
    except: pass

    try: del returnDict['Guy']
    except: pass


    return returnDict

def isStopwords(word):
    if word in stopwords.words('english'):
        return True
    else:
        return False

def removeStopwords(speakerDict):
    returnDict = defaultdict(list)
    for (speaker, words) in speakerDict.items():
        returnDict[speaker] += [word for word in words if not isStopwords(word)]
    return returnDict

def rid_extar(Dict) :
    returnDict = defaultdict(list)
    for people in Dict.items() :
        if len(people[1]) > 50 :
            returnDict[people[0]] += people[1]
    return returnDict



def num_same_words (human1_words, human2_words) :
    cnt = 0
    for words in human1_words :
        if words in human2_words :
            cnt = cnt + 1
    return  (100*cnt)/len(human1_words)

def seperate_into_two (dict) :
    # define two group
    resR = []
    #define most suda-jaeng-e
    most = 0
    num_most_words = 0
    for i in range(len(dict.items())):
        if len(dict.items()[i][1]) > num_most_words :
            num_most_words = len(dict.items()[i][1])
            most = i
    for people in dict.items() :
        # temp TWO
        A = []
        B = []
        temp = []
        l = len(dict.items())
        for i in range(l) :
            temp.append((people[0], dict.items()[i][0], num_same_words(people[1],dict.items()[i][1])))
        avg = 0

        for case in range(len(temp)) :
            avg += temp[case][2]
        avg = (avg-100)/(len(temp)-1)
        for i in range((len(temp))) :
            if temp[i][2] >= avg :
                A.append(dict.items()[i][0])
            else :
                B.append(dict.items()[i][0])
        AandB = sorted((A,B))
        resR.append(AandB)
    return resR

def gABaS(lll):
    spkl = lll[0][0]+lll[0][1]
    frdf = defaultdict(int)

    for ll in lll:
        fr0 = len([w for w in ll[0] if w.isupper()])
        ho0 = len([w for w in ll[0] if not w.isupper()])
        if fr0 > ho0:
            for spk in ll[0]:
                frdf[spk] += 1
        else:
            for spk in ll[1]:
                frdf[spk] += 1

    return frdf

def avg(l):
    r = 0.0
    for e in l:
        r +=e
    return r/len(l)

def stdev(values):
  if len(values) < 2:
    return None

  sd = 0.0
  sum = 0.0
  meanValue = avg(values)

  for i in range(0, len(values)):
    diff = values[i] - meanValue
    sum += diff * diff

  sd = math.sqrt(sum / (len(values) ))
  return sd


for i in [50*c for c in range(16)]:
    mixResultRaw = open("test-----mixResult.txt")
    mixResultDict = removeStopwords(rid_extar(classifySpeaker(fileToWordList2(mixResultRaw, i))))
    if len(mixResultDict) is not 0 :
        ll = seperate_into_two(mixResultDict)

        asdf= gABaS(ll)

        _avg = avg(asdf.values())
        _stdev = (stdev(asdf.values()))

        bc = _avg - _stdev +1
        uc = _avg + _stdev -1

        gA = []
        gB = []
        aS = []
        for spk, fr in asdf.items():
            if fr < bc:
                gA.append(spk)
            elif fr > uc:
                gB.append(spk)
            else:
                aS.append(spk)

        print "s" + str(i)+" = " + str([[gA, gB], aS])
    else :
        print "PP"