from fileinput import FileInput

def removeRules(folder, size):
	with FileInput(files=[f'{folder}/rulewerk-rules-{size}.txt'], inplace=True) as f:
		for line in f:
			if line != '':
				line = remRule(line)
				print(line)

def remRule(s):
	t = s.split(",")
	t.remove(t[len(t)-1])
	return ",".join(t)+"."

def addPredicates(folder, pred, type):
	with FileInput(files=[f'{folder}/rulewerk-{type}.txt'], inplace=True) as f:
		for line in f:
			if line != '':
				line = addPred(line, pred)
				print(line)

def addPred(s, pred):
	t = s.strip().split("\t")
	return pred+"("+", ".join(t)+")."

# addPredicates("sql-10","Out","hold")
removeRules("sql-10","small")

# new subsection for examples and not good for EL.
#

