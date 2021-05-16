import matplotlib
import matplotlib.pyplot as plt
import numpy as np

expnum = 2

sizes = [500000, 1000000, 1500000, 2000000]
CRPQ = [2,3]
Disj = [1,2,3]
Conj = [1,3,5]
mapping = {'1':0, '3':1, '5':2}

ylabels = ["Query", "Rules Generated", "Facts Generated", "Parsing Time", "Translating Time", "Reasoning Time", "Num. of Answers"]
filenames = ["query", "rules", "facts", "parset", "transt", "reasont", "answers"]

# ================================ BY CRPQ ===================================== #

Dis = 3
Con = 5
Conv = True
Suffix = "_mem"
# if not Conv:
# 	Suffix = "-nc"
size = 1000000
graphOf = 5
ylabel = ylabels[graphOf]
mapping = {'2':0, '3':1}

a1 = {k: [0 for i in CRPQ] for k in range(10)}
a2 = {k: [0 for i in CRPQ] for k in range(10)}
a3 = {k: [0 for i in CRPQ] for k in range(10)}
a4 = {k: [0 for i in CRPQ] for k in range(10)}

for num in range(expnum):
	with open(f'new-1-{size}-{num+1}.csv', 'r') as reader:
		next(reader)
		for line in reader.readlines():
			header = line.split(',')[0].strip().split('_')
			if header[1] == f'{Dis}' and header[2] == f'{Con}' and int(header[0]) in CRPQ:
				a1[int(header[3])][mapping[header[0]]] += int(line.split(',')[graphOf].strip())
	with open(f'new-2-{size}-{num+1}.csv', 'r') as reader:
		next(reader)
		for line in reader.readlines():
			header = line.split(',')[0].strip().split('_')
			if header[1] == f'{Dis}' and header[2] == f'{Con}' and int(header[0]) in CRPQ:
				a2[int(header[3])][mapping[header[0]]] += int(line.split(',')[graphOf].strip())
	if not Conv:
		with open(f'new-1-{size}-{num+1}-nc.csv', 'r') as reader:
			next(reader)
			for line in reader.readlines():
				header = line.split(',')[0].strip().split('_')
				if header[1] == f'{Dis}' and header[2] == f'{Con}' and int(header[0]) in CRPQ:
					a3[int(header[3])][mapping[header[0]]] += int(line.split(',')[graphOf].strip())
		with open(f'new-2-{size}-{num+1}-nc.csv', 'r') as reader:
			next(reader)
			for line in reader.readlines():
				header = line.split(',')[0].strip().split('_')
				if header[1] == f'{Dis}' and header[2] == f'{Con}' and int(header[0]) in CRPQ:
					a4[int(header[3])][mapping[header[0]]] += int(line.split(',')[graphOf].strip())

print(a1)
print(a2)

# Start Grapher
query = 4

labels = CRPQ
d1 = np.array([i / (expnum * 1000000) for i in a1[query]])
d2 = np.array([i / (expnum * 1000000) for i in a2[query]])
d3 = np.array([i / (expnum * 1000000) for i in a3[query]])
d4 = np.array([i / (expnum * 1000000) for i in a4[query]])

x = np.arange(len(labels))  # the label locations
width = 0.35  # the width of the bars

fig, ax = plt.subplots()
if Conv:
	rects1 = ax.bar(x - width/2, d1, width, label='Translation 1')
	rects2 = ax.bar(x + width/2, d2, width, label='Translation 2')
if not Conv:
	rects1 = ax.bar(x - width/2, d1, width/4, label='Translation 1')
	rects2 = ax.bar(x - width/4, d3, width/4, label='Translation 1 - No Converse')
	rects3 = ax.bar(x, d2, width/4, label='Translation 2')
	rects4 = ax.bar(x + width/4, d4, width/4, label='Translation 2 - No Converse')

# Add some text for labels, title and custom x-axis tick labels, etc.
ax.set_ylabel(ylabel)
ax.set_title("Comparison of "+ylabel+" by CRPQ Conjunction")
ax.set_xticks(x)
ax.set_xticklabels(labels)
ax.legend()


def autolabel(rects):
	"""Attach a text label above each bar in *rects*, displaying its height."""
	for rect in rects:
		height = rect.get_height()
		ax.annotate('{}'.format(height),
					xy=(rect.get_x() + rect.get_width() / 2, height),
					xytext=(0, 3),  # 3 points vertical offset
					textcoords="offset points",
					ha='center', va='bottom')


# autolabel(rects1)
# autolabel(rects2)
# autolabel(rects3)

fig.tight_layout()

# plt.savefig(f'query-{filenames[graphOf]}-{size}',bbox_inches='tight')
plt.ylim(0,8000)
plt.show()


# ================================ BY Disjunction ===================================== #

# CR = 3
# Con = 3
# Conv = True
# Suffix = ""
# # if not Conv:
# # 	Suffix = "-nc"
# size = 1000000
# graphOf = 5
# ylabel = ylabels[graphOf]

# a1 = {k: [0 for i in Disj] for k in range(10)}
# a2 = {k: [0 for i in Disj] for k in range(10)}
# a3 = {k: [0 for i in Disj] for k in range(10)}
# a4 = {k: [0 for i in Disj] for k in range(10)}

# for num in range(expnum):
# 	with open(f'new-1-{size}-{num+1}.csv', 'r') as reader:
# 		next(reader)
# 		for line in reader.readlines():
# 			header = line.split(',')[0].strip().split('_')
# 			if header[0] == f'{CR}' and header[2] == f'{Con}' and int(header[1]) in Disj:
# 				a1[int(header[3])][int(header[1])-1] += int(line.split(',')[graphOf].strip())
# 	with open(f'new-2-{size}-{num+1}.csv', 'r') as reader:
# 		next(reader)
# 		for line in reader.readlines():
# 			header = line.split(',')[0].strip().split('_')
# 			if header[0] == f'{CR}' and header[2] == f'{Con}' and int(header[1]) in Disj:
# 				a2[int(header[3])][int(header[1])-1] += int(line.split(',')[graphOf].strip())
# 	if not Conv:
# 		with open(f'new-1-{size}-{num+1}-nc.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[2] == f'{Con}' and int(header[1]) in Disj:
# 					a3[int(header[3])][int(header[1])-1] += int(line.split(',')[graphOf].strip())
# 		with open(f'new-2-{size}-{num+1}-nc.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[2] == f'{Con}' and int(header[1]) in Disj:
# 	 				a4[int(header[3])][int(header[1])-1] += int(line.split(',')[graphOf].strip())

# # Start Grapher
# query = 5

# labels = Disj
# d1 = np.array([i / (expnum) for i in a1[query]])
# d2 = np.array([i / (expnum) for i in a2[query]])
# d3 = np.array([i / (expnum) for i in a3[query]])
# d4 = np.array([i / (expnum) for i in a4[query]])

# print(d1,d2)

# x = np.arange(len(labels))  # the label locations
# width = 0.35  # the width of the bars

# fig, ax = plt.subplots()
# if Conv:
# 	rects1 = ax.bar(x - width/2, d1, width, label='Translation 1')
# 	rects2 = ax.bar(x + width/2, d2, width, label='Translation 2')
# if not Conv:
# 	rects1 = ax.bar(x - width/2, d1, width/4, label='Translation 1')
# 	rects2 = ax.bar(x - width/4, d3, width/4, label='Translation 1 - No Converse')
# 	rects3 = ax.bar(x, d2, width/4, label='Translation 2')
# 	rects4 = ax.bar(x + width/4, d4, width/4, label='Translation 2 - No Converse')

# # Add some text for labels, title and custom x-axis tick labels, etc.
# ax.set_ylabel(ylabel)
# ax.set_title("Comparison of "+ylabel+" by Number of Disjunction")
# ax.set_xticks(x)
# ax.set_xticklabels(labels)
# ax.legend()


# def autolabel(rects):
# 	"""Attach a text label above each bar in *rects*, displaying its height."""
# 	for rect in rects:
# 		height = rect.get_height()
# 		ax.annotate('{}'.format(height),
# 					xy=(rect.get_x() + rect.get_width() / 2, height),
# 					xytext=(0, 3),  # 3 points vertical offset
# 					textcoords="offset points",
# 					ha='center', va='bottom')


# # autolabel(rects1)
# # autolabel(rects2)
# # autolabel(rects3)

# fig.tight_layout()

# # plt.savefig(f'query-{filenames[graphOf]}-{size}',bbox_inches='tight')
# plt.ylim(0,8000000000)
# plt.show()


# ================================ BY Inner Conjunction ===================================== #

# CR = 3
# Dis = 3
# Conv = True
# Suffix = ""
# # if not Conv:
# # 	Suffix = "-nc"
# size = 1000000
# graphOf = 5
# ylabel = ylabels[graphOf]
# mapping = {'1':0, '3':1, '5':2}

# a1 = {k: [0 for i in Conj] for k in range(10)}
# a2 = {k: [0 for i in Conj] for k in range(10)}
# a3 = {k: [0 for i in Conj] for k in range(10)}
# a4 = {k: [0 for i in Conj] for k in range(10)}

# for num in range(expnum):
# 	with open(f'new-1-{size}-{num+1}.csv', 'r') as reader:
# 		next(reader)
# 		for line in reader.readlines():
# 			header = line.split(',')[0].strip().split('_')
# 			if header[0] == f'{CR}' and header[1] == f'{Dis}' and int(header[2]) in Conj:
# 				a1[int(header[3])][mapping[header[2]]] += int(line.split(',')[graphOf].strip())
# 	with open(f'new-2-{size}-{num+1}.csv', 'r') as reader:
# 		next(reader)
# 		for line in reader.readlines():
# 			header = line.split(',')[0].strip().split('_')
# 			if header[0] == f'{CR}' and header[1] == f'{Dis}' and int(header[2]) in Conj:
# 				a2[int(header[3])][mapping[header[2]]] += int(line.split(',')[graphOf].strip())
# 	if not Conv:
# 		with open(f'new-1-{size}-{num+1}-nc.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and int(header[2]) in Conj:
# 					a3[int(header[3])][mapping[header[2]]] += int(line.split(',')[graphOf].strip())
# 		with open(f'new-2-{size}-{num+1}-nc.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and int(header[2]) in Conj:
# 					a4[int(header[3])][mapping[header[2]]] += int(line.split(',')[graphOf].strip())

# # Start Grapher
# query = 3

# labels = [f'{i} - {i+1}' for i in Conj]
# d1 = np.array([i / (expnum) for i in a1[query]])
# d2 = np.array([i / (expnum) for i in a2[query]])
# d3 = np.array([i / (expnum) for i in a3[query]])
# d4 = np.array([i / (expnum) for i in a4[query]])

# print(d1,d2)

# x = np.arange(len(labels))  # the label locations
# width = 0.35  # the width of the bars

# fig, ax = plt.subplots()
# if Conv:
# 	rects1 = ax.bar(x - width/2, d1, width, label='Translation 1')
# 	rects2 = ax.bar(x + width/2, d2, width, label='Translation 2')
# if not Conv:
# 	rects1 = ax.bar(x - width/2, d1, width/4, label='Translation 1')
# 	rects2 = ax.bar(x - width/4, d3, width/4, label='Translation 1 - No Converse')
# 	rects3 = ax.bar(x, d2, width/4, label='Translation 2')
# 	rects4 = ax.bar(x + width/4, d4, width/4, label='Translation 2 - No Converse')

# # Add some text for labels, title and custom x-axis tick labels, etc.
# ax.set_ylabel(ylabel)
# ax.set_title("Comparison of "+ylabel+" by Number of Conjunction in Disjunction")
# ax.set_xticks(x)
# ax.set_xticklabels(labels)
# ax.legend()


# def autolabel(rects):
# 	"""Attach a text label above each bar in *rects*, displaying its height."""
# 	for rect in rects:
# 		height = rect.get_height()
# 		ax.annotate('{}'.format(height),
# 					xy=(rect.get_x() + rect.get_width() / 2, height),
# 					xytext=(0, 3),  # 3 points vertical offset
# 					textcoords="offset points",
# 					ha='center', va='bottom')


# # autolabel(rects1)
# # autolabel(rects2)
# # autolabel(rects3)

# fig.tight_layout()

# # plt.savefig(f'query-{filenames[graphOf]}-{size}',bbox_inches='tight')
# plt.ylim(0,8000000000)
# plt.show()


# ================================ BY Size and Star Depth ===================================== #

# CR = 3
# Dis = 3
# Conj = 5
# query = 4
# graphOf = 5
# trans = 1
# ylabel = ylabels[graphOf]

# a1 = {k: [0 for i in range(len(sizes))] for k in range(10)}
# a2 = {k: [0 for i in range(len(sizes))] for k in range(10)}
# a3 = {k: [0 for i in range(len(sizes))] for k in range(10)}
# a4 = {k: [0 for i in range(len(sizes))] for k in range(10)}
# a5 = {k: [0 for i in range(len(sizes))] for k in range(10)}
# a6 = {k: [0 for i in range(len(sizes))] for k in range(10)}

# for num in range(expnum):
# 	for i in range(len(sizes)):
# 		j = 0
# 		with open(f'new-1-{sizes[i]}-{num+1}.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and header[2] == f'{Conj}':
# 					a1[j][i] += int(line.split(',')[graphOf].strip())
# 					j += 1
# 		j = 0
# 		with open(f'new-2-{sizes[i]}-{num+1}.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and header[2] == f'{Conj}':
# 					a2[j][i] += int(line.split(',')[graphOf].strip())
# 					j += 1
# 		j = 0
# 		with open(f'new-1-{sizes[i]}-{num+1}-s1.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and header[2] == f'{Conj}':
# 					a3[j][i] += int(line.split(',')[graphOf].strip())
# 					j += 1
# 		j = 0
# 		with open(f'new-2-{sizes[i]}-{num+1}-s1.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and header[2] == f'{Conj}':
# 					a4[j][i] += int(line.split(',')[graphOf].strip())
# 					j += 1
# 		j = 0
# 		with open(f'new-1-{sizes[i]}-{num+1}-s2_2.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and header[2] == f'{Conj}':
# 					a5[j][i] += int(line.split(',')[graphOf].strip())
# 					j += 1
# 		j = 0
# 		with open(f'new-2-{sizes[i]}-{num+1}-s2_2.csv', 'r') as reader:
# 			next(reader)
# 			for line in reader.readlines():
# 				header = line.split(',')[0].strip().split('_')
# 				if header[0] == f'{CR}' and header[1] == f'{Dis}' and header[2] == f'{Conj}':
# 					a6[j][i] += int(line.split(',')[graphOf].strip())
# 					j += 1

# print(a1)

# # Start Grapher
# labels = [str(int(i/1000))+"K" for i in range(500000, 2000001, 500000)]
# if trans == 1:
# 	d1 = [i / expnum for i in a1[query]]
# 	d2 = [i / expnum for i in a3[query]]
# 	d3 = [i / expnum for i in a5[query]]
# else:
# 	d1 = [i / expnum for i in a2[query]]
# 	d2 = [i / expnum for i in a4[query]]
# 	d3 = [i / expnum for i in a6[query]]

# x = np.arange(len(labels))  # the label locations
# width = 0.15  # the width of the bars

# fig, ax = plt.subplots()
# rects1 = ax.bar(x - width, d1, width, label='No Kleene Star')
# rects2 = ax.bar(x, d2, width, label='Kleene Star Depth 1')
# rects3 = ax.bar(x + width, d3, width, label='Kleene Star Depth 2')

# # Add some text for labels, title and custom x-axis tick labels, etc.
# ax.set_ylabel(ylabel)
# ax.set_title("Comparison of "+ylabel+" by Kleene Star Depth")
# ax.set_xticks(x)
# ax.set_xticklabels(labels)
# ax.legend()


# def autolabel(rects):
# 	"""Attach a text label above each bar in *rects*, displaying its height."""
# 	for rect in rects:
# 		height = rect.get_height()
# 		ax.annotate('{}'.format(height),
# 					xy=(rect.get_x() + rect.get_width() / 2, height),
# 					xytext=(0, 3),  # 3 points vertical offset
# 					textcoords="offset points",
# 					ha='center', va='bottom')


# # autolabel(rects1)
# # autolabel(rects2)
# # autolabel(rects3)

# fig.tight_layout()
# plt.ylim(0,50000000000)
# # plt.ylim(0,40000000)
# plt.show()



# Interesting things:
# - Grow as expected for all linearly as number of crpq, disj, conj grow.
# - The first translation grow as expexted as size grow, and as star depth grows.
# - Second translation somehow is better in depth 2 than 1. Need depth 3 to confirm if it is anomaly
# What I thing interesting and possible with the two topics:
# - They are both interesting to a degree, but I am not exactly see any usage of trying to convert the process to ASP
#   Especially if the resulting ASP program have no performance improvement.
#   The second topics is more usable practically I think, for many real world cases.
# - I see some steps that can be done for the second topics as in the slide (not concrete yet, but 
# 	useful for a start).
#   The first topic is a lot harder. The program I provide earlier rely on processing phi as CNF in ASP.
#   It require asserting new clause for every result of the provenance. It is not something available
#   in a lot of solver. Alpha also missing some possibly useful things (aggregate, etc).

# ============================== Meeting 27-04-2021 ==================================
# Project:
# Add memory into consideration as well.
# Statements about the encoding.
# proof by experiments results.
# repeatable experiments program and data available.
# implementation: unit testing correctness using sw principle.
# epsilon free-ness: without minimizing.
# Low and high: from full memory.
# -> go for smaller examples.

# Thesis (DatalogS):
# Idea: extend expressiveness via set -> exponential problem.
# Encode hard problem into I/O specification.
# Possibility: Smaller program to generate exponential number of facts from small input.
# Task: find this cases -> smaller rules. = powerset problem
# Why choose DS and expected results.
