import csv

cases = ["sql-01","sql-02","sql-03","sql-04","sql-05","sql-06","sql-07","sql-08","sql-09",
"sql-11","sql-12","sql-13","sql-14","sql-15","traffic","small","abduce","ship","inflamation",
"animals","rvcheck","path","union-find","cliquer","rsg","1-object-1-type","1-type","1-object",
"escape","modref","andersen","1-call-site","2-call-site","nearlyscc", "sgen", "scc","buildwall"]
scenario = ["Exp","All Delta","","","","Delta + Datalog(S)","","","","All Datalog(S)","","",""]
header = ["Case", "Running time", "Iteration", "Rulewerk call", "Z3 call","Running time", "Iteration", "Rulewerk call", "Z3 call","Running time", "Iteration", "Rulewerk call", "Z3 call"]
# scene = [2,3]
expnum = 100

print(len(cases))

f = open('result.csv', 'w')
writer = csv.writer(f)
writer.writerow(scenario)
writer.writerow(header)

for case in cases:
	a1 = [0,0,0,0]
	a2 = [0,0,0,0]
	a3 = [0,0,0,0]
	a4 = [0,0,0,0]
	idx = 0
	for num in range(1, expnum+1):
		try:
			idx += 1
			with open(f'{case}/rulewerk-log-2-{num}.txt', 'r') as reader:
				lines = reader.readlines()
				a2[0] += int(lines[0]) / 1000000
				a2[1] += int(lines[1])
				a2[2] += int(lines[2])
				a2[3] += int(lines[3])
		except:
			pass
			# print("No file")
	if idx == expnum:
		print(a2)
		d2 = [x/expnum for x in a2]
	else:
		d2 = a2

	idx = 0
	for num in range(1, expnum+1):
		try:
			idx += 1
			with open(f'{case}/rulewerk-log-3-{num}-ds.txt', 'r') as reader:
				lines = reader.readlines()
				a3[0] += int(lines[0]) / 1000000
				a3[1] += int(lines[1])
				a3[2] += int(lines[2])
				a3[3] += int(lines[3])
		except:
			pass
			# print("No file")
	if idx == expnum:
		d3 = [x/expnum for x in a3]
	else:
		d3 = a3

	idx = 0
	for num in range(1, expnum+1):
		try:
			idx += 1
			with open(f'{case}/rulewerk-log-4-{num}.txt', 'r') as reader:
				lines = reader.readlines()
				a4[0] += int(lines[0]) / 1000000
				a4[1] += int(lines[1])
				a4[2] += int(lines[2])
				a4[3] += int(lines[3])
		except:
			pass
			# print("No file")
	if idx == expnum:
		d4 = [x/expnum for x in a4]
	else:
		d4 = a4
	
	output = [case] + d2 + d3 # + d4

	writer.writerow(output)
	print(output)

f.close()
