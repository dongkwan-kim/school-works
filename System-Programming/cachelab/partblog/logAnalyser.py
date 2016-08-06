__author__ = "todokaist"

wf = open("log-m.txt", "w")

for line in list(open("log.txt", "r")):
    print(line.strip())
    wf.write(line)
wf.close()
