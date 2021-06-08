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

# addPredicates("nearlyscc","NSCC","output-plus")
# removeRules("nearlyscc","small")



Good morning. For last week, I have some small updates.
- The Datalog(S) transformation works with the modification from last week for the empty set.
It manages to compute the ancestor closure example. For the next step, I would like to see
why it produces a lot of nulls during reasoning. Or is this an expected result?
- For the synthesis, I believe there is still some problem with the generation process.
I picked 4 small benchmark from the paper, and two of them is very slow and (potentially) incorrect.
I would like to see whether I can make the process more efficient so I can debug it.

