__author__ = 'todokaist'

'''
Python 2.7
2015 NLP with Python Final Project
Categorizing distributed dialogues by interests
'''

'''
Input: mixed script
script form: "person: speech"
Output(printed): number of read lines \t accuracy \t divied group by interest
'''

'''
Input example(mixResult.txt)
- mixed script of House M.D. and Friends
...
Melanie: Why are you late?
Rebecca: You're not going to like the answer.
Melanie: I already know the answer.
Rebecca: I missed the bus.
Melanie: I don't doubt it, no bus stops near Brad's. You spent the night, the alarm didn't work. Or maybe it did.
MONICA: There's nothing to tell! He's just some guy I work with!
JOEY: C'mon, you're going out with the guy! There's gotta be something wrong with him!
...
'''

'''
Golden output: [['Wilson', 'Foreman', 'Cameron', 'Rebecca', 'Cuddy', 'House', 'Chase'], ['MONICA', 'RACHEL', 'PAUL', 'CHANDLER', 'JOEY', 'PHOEBE', 'ROSS']]
Output example(printed)
50	0.5	[['Rebecca', 'CHANDLER'], []]
100	1.0	[['Wilson', 'House', 'Rebecca'], ['MONICA', 'ROSS', 'CHANDLER', 'JOEY']]
150	1.0	[['Cuddy', 'Wilson', 'Foreman', 'House', 'Rebecca'], ['MONICA', 'RACHEL', 'ROSS', 'JOEY', 'CHANDLER']]
200	1.0	[['Cuddy', 'Wilson', 'Foreman', 'Rebecca', 'House', 'Cameron'], ['MONICA', 'JOEY', 'RACHEL', 'CHANDLER', 'ROSS']]
250	0.6875	[['Cuddy', 'Foreman', 'Cameron', 'Chase'], ['MONICA', 'RACHEL', 'House', 'JOEY', 'ROSS', 'CHANDLER', 'Wilson', 'PHOEBE', 'Rebecca']]
300	1.0	[['Foreman', 'Wilson', 'Cameron', 'Rebecca', 'Cuddy', 'House', 'Chase'], ['MONICA', 'CHANDLER', 'ROSS', 'RACHEL', 'PHOEBE', 'PAUL', 'JOEY']]
350	0.8	[['Foreman', 'Cameron', 'Rebecca', 'Cuddy', 'Wilson', 'House', 'RACHEL', 'Chase', 'ROSS'], ['MONICA', 'PHOEBE', 'PAUL', 'CHANDLER', 'JOEY']]
400	1.0	[['Wilson', 'Foreman', 'Cameron', 'Rebecca', 'Cuddy', 'House', 'Chase'], ['MONICA', 'RACHEL', 'PAUL', 'CHANDLER', 'JOEY', 'PHOEBE', 'ROSS']]
450	0.789473684211	[['Cuddy', 'Wilson', 'Cameron', 'Rebecca', 'Foreman', 'House', 'RACHEL', 'PAUL', 'Chase'], ['MONICA', 'PHOEBE', 'CHANDLER', 'ROSS', 'JOEY']]
500	0.764705882353	[['Cuddy', 'Wilson', 'Foreman', 'House', 'Cameron', 'Rebecca', 'MONICA', 'PAUL', 'Chase'], ['RACHEL', 'PHOEBE', 'CHANDLER', 'ROSS', 'JOEY']]
550	0.631578947368	[['Foreman', 'House', 'Cameron', 'Rebecca', 'Cuddy', 'MONICA', 'Wilson', 'RACHEL', 'ROSS'], ['PHOEBE', 'PAUL', 'Chase', 'CHANDLER', 'JOEY']]
600	0.631578947368	[['Wilson', 'Foreman', 'House', 'Cameron', 'Cuddy', 'MONICA', 'RACHEL', 'Rebecca', 'ROSS'], ['PHOEBE', 'PAUL', 'Chase', 'CHANDLER', 'JOEY']]
650	0.588235294118	[['Cuddy', 'Wilson', 'Foreman', 'House', 'Cameron', 'Rebecca', 'MONICA', 'RACHEL', 'ROSS'], ['PHOEBE', 'PAUL', 'Chase', 'CHANDLER', 'JOEY']]
700	0.705882352941	[['Foreman', 'House', 'Cameron', 'Rebecca', 'MONICA', 'Wilson', 'RACHEL'], ['PHOEBE', 'PAUL', 'JOEY', 'CHANDLER', 'Chase', 'ROSS']]
'''

import re
import nltk
from nltk.corpus import brown
from collections import defaultdict
from nltk.corpus import wordnet as wn
import time

def fileToWordList(file, cnt):
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
            tempSpeaker = word[:-1]
            returnDict[tempSpeaker].append([])
        else:
            returnDict[tempSpeaker][-1].append(word.lower())
    return returnDict
#-------------------------------------

def NNPTag(taggedL, speakerL):
    for (i, (w, t)) in enumerate(taggedL):
        if w in speakerL:
            taggedL[i] = (w, "NNP")

def ITag(taggedL):
    for (i, (w, t)) in enumerate(taggedL):
        if w == "i" or w == "i'm":
            taggedL[i] = (w, "I")

def wholePOStag(test, tagger, speakerL):
    returnDict = defaultdict(list)
    for (speaker, sents) in test.items():
        for aSent in sents:
            taggedTest = tagger.tag(aSent)
            NNPTag(taggedTest, speakerL)
            ITag(taggedTest)
            returnDict[speaker].append(taggedTest)
    return returnDict

# --------------------------------
def delNoSynset(dic):
    returnDic = defaultdict(list)
    for spk in dic.keys():
        for word in dic[spk]:
            try:
                if len(wn.synsets(word)) > 0:
                    returnDic[spk].append(word)
            except:
                pass
    return returnDic

def cutSpkByOrder(dic, num=14):
    returnDic = defaultdict(list)
    mostSpk = list(reversed(sorted([(len(sent), spk) for spk, sent in dic.items()])))[:num]
    for spk in [spk for cnt, spk in mostSpk]:
        for word in dic[spk]:
            returnDic[spk].append(word)
    return returnDic

def cutSpkByOrder2(dic, num=14):
    tmpdic = defaultdict(int)
    for spk in dic.keys():
        totalLen = 0
        for aSent in dic[spk]:
            totalLen += len(aSent)
        tmpdic[spk] = totalLen

    returnDic = defaultdict(list)
    mostSpk = list(reversed(sorted([(length, spk) for spk, length in tmpdic.items()])))[:num]
    for spk in [spk for length, spk in mostSpk]:
        for sent in dic[spk]:
            returnDic[spk].append(sent)
    return returnDic

def wordSim(w1, w2):
    _w1 = wn.synsets(w1)
    _w2 = wn.synsets(w2)

    sim = _w1[0].path_similarity(_w2[0])
    if sim:
        return sim
    else:
        return 0

def wordSim2(w1, w2):
    _w1 = wn.synsets(w1)
    _w2 = wn.synsets(w2)

    synsetPairList = [(sy1, sy2) for sy1 in _w1 for sy2 in _w2]

    sim = 0.0
    totNum = len(synsetPairList)

    for (sy1, sy2) in synsetPairList:
        psOne = sy1.path_similarity(sy2)

        if psOne:
            sim += psOne
        else:
            totNum -= 1
    if totNum:
        return sim/totNum
    else:
        return 0

def ignoreOrder(lt):
    returnList = []
    for x, y in lt:
        if not (y, x) in returnList:
            returnList.append((x, y))
    return returnList

def extractNoun(tagged):
    returnDic = defaultdict(list)
    for (spk, sents) in tagged.items():
        for aSent in sents:
            for (word, tag) in aSent:
                if re.search("^N.*", tag) and not re.search("NNP", tag):
                    returnDic[spk].append(word)
    return returnDic

def wnScoreDicWithPair(spkPairList, testDic):
    returnDic = defaultdict(list)
    for (p1, p2) in spkPairList:
        wordPairs = [(w1, w2) for w1 in testDic[p1] for w2 in testDic[p2]]
        sum = 0.0
        for (w1, w2) in wordPairs:
            sum += wordSim(w1, w2)
            #sum += wordSim2(w1, w2)
        returnDic[(p1, p2)] = sum/len(wordPairs)
    return returnDic

def cmpTuple(t1, t2):
    return set(list(t1)) == set(list(t2))

def setScore(setX, ambigSpk, score):
    pairContainSpk = [(p1, p2) for (p1, p2) in score.keys() if p1 == ambigSpk or p2 == ambigSpk]
    sum=0.0
    for spk in setX:
        for pair in pairContainSpk:
            if cmpTuple((spk, ambigSpk), pair):
                sum += score[pair]
    return sum/len(setX)

def chsSetByScr(setA, setB, ambigSet, score):
    _setA = [x for x in setA]
    _setB = [x for x in setB]
    for ambigSpk in ambigSet:
        scrA = setScore(setA, ambigSpk, score)
        scrB = setScore(setB, ambigSpk, score)
        if scrA > scrB:
            _setA.append(ambigSpk)
        else:
            _setB.append(ambigSpk)
    return [_setA, _setB]

def eval(lofl, ambig):
    correct = 0.0
    spkList = lofl[0]+lofl[1]+ambig
    for spk in spkList:
        if spk.isupper() and spk in lofl[1]:
            correct += 1
        elif not spk.isupper() and spk in lofl[0]:
            correct += 1
    return (correct)/len(spkList)

# result of sbcode
'''
Form: [[group A], [group B], [Ambiguous Speaker]]
'''
s50 = [[['Rebecca', 'CHANDLER'], []], []]
s100 = [[['Wilson', 'House', 'Rebecca'], ['MONICA', 'ROSS', 'CHANDLER']], ['JOEY']]
s150 = [[['Cuddy', 'Wilson', 'Foreman'], ['MONICA', 'RACHEL', 'ROSS', 'JOEY']], ['House', 'Rebecca', 'CHANDLER']]
s200 = [[['Cuddy', 'Wilson', 'Foreman', 'Rebecca'], ['MONICA', 'JOEY']], ['House', 'RACHEL', 'Cameron', 'CHANDLER', 'ROSS']]
s250 = [[['Cuddy', 'Foreman', 'Cameron', 'Chase'], ['MONICA', 'RACHEL', 'House', 'JOEY', 'ROSS', 'CHANDLER']], ['Wilson', 'PHOEBE', 'Rebecca']]
s300 = [[['Foreman', 'Wilson', 'Cameron', 'Rebecca'], ['MONICA', 'CHANDLER', 'ROSS']], ['Cuddy', 'RACHEL', 'House', 'Chase', 'PHOEBE', 'PAUL', 'JOEY']]
s350 = [[['Foreman', 'Cameron', 'Rebecca'], ['MONICA', 'PHOEBE', 'PAUL', 'CHANDLER', 'JOEY']], ['Cuddy', 'Wilson', 'House', 'RACHEL', 'Chase', 'ROSS']]
s400 = [[['Wilson', 'Foreman', 'Cameron', 'Rebecca'], ['MONICA', 'RACHEL', 'PAUL', 'CHANDLER', 'JOEY']], ['Cuddy', 'House', 'PHOEBE', 'Chase', 'ROSS']]
s450 = [[['Cuddy', 'Foreman', 'House', 'Cameron', 'Rebecca'], ['PHOEBE', 'PAUL', 'CHANDLER', 'JOEY']], ['MONICA', 'Wilson', 'RACHEL', 'Chase', 'ROSS']]
s500 = [[['Cuddy', 'Wilson', 'Cameron', 'Rebecca'], ['MONICA', 'PHOEBE', 'CHANDLER', 'ROSS', 'JOEY']], ['Foreman', 'House', 'RACHEL', 'PAUL', 'Chase']]
s550 = [[['Cuddy', 'Wilson', 'Foreman', 'House', 'Cameron', 'Rebecca'], ['RACHEL', 'PHOEBE', 'CHANDLER', 'ROSS', 'JOEY']], ['MONICA', 'PAUL', 'Chase']]
s600 = [[['Foreman', 'House', 'Cameron', 'Rebecca'], ['PHOEBE', 'PAUL', 'Chase', 'CHANDLER', 'JOEY']], ['Cuddy', 'MONICA', 'Wilson', 'RACHEL', 'ROSS']]
s650 = [[['Wilson', 'Foreman', 'House', 'Cameron'], ['PHOEBE', 'PAUL', 'Chase', 'CHANDLER', 'JOEY']], ['Cuddy', 'MONICA', 'RACHEL', 'Rebecca', 'ROSS']]
s700 = [[['Cuddy', 'Wilson', 'Foreman', 'House', 'Cameron', 'Rebecca'], ['PHOEBE', 'PAUL', 'Chase', 'CHANDLER', 'JOEY']], ['MONICA', 'RACHEL', 'ROSS']]

fss = [s50, s100, s150, s200, s250, s300, s350, s400, s500, s550, s600, s650, s700]

for index, i in enumerate([50*c for c in range(16) if c != 0]):

    mixResultRaw = open("mixResult.txt")
    wordListwithNumber = fileToWordList(mixResultRaw, i)
    testText = cutSpkByOrder2(classifySpeaker(wordListwithNumber))

    #testText = cutSpkByOrder2(classifySpeaker(fileToWordList(mixResultRaw)))
    brown_tagged_sents = brown.tagged_sents()
    size = int(len(brown_tagged_sents)/2)

    train_sents =brown_tagged_sents[:size]
    speakerList = [s.lower() for s in testText.keys()]

    t0 = nltk.DefaultTagger('NN')
    t1 = nltk.UnigramTagger(train_sents, backoff=t0)
    t2 = nltk.BigramTagger(train_sents, backoff=t1)

    taggedTest = wholePOStag(testText, t2, speakerList)
    nounInTest = extractNoun(taggedTest)
    modifiedTest = cutSpkByOrder(delNoSynset(nounInTest))

    spkPairs = ignoreOrder([(spk1, spk2) for spk1 in modifiedTest.keys() for spk2 in modifiedTest.keys() if not spk1 == spk2])
    scoreDic = wnScoreDicWithPair(spkPairs, modifiedTest)

    firstDividedSet = fss[index]

    setA = firstDividedSet[0][0]
    setB = firstDividedSet[0][1]
    ambigSet = firstDividedSet[1]

    chsSet = chsSetByScr(setA, setB, ambigSet, scoreDic)
    print str(i)+"\t"+str(eval(chsSet, ambigSet)) + "\t" + str(chsSet)