import matplotlib
import matplotlib.pyplot as plt
import numpy as np

expnum = 3

# sizes = [i for i in range(100000, 2000001, 100000)]
# ylabels = ["Query", "Rules Generated", "Facts Generated", "Parsing Time", "Translating Time", "Reasoning Time", "Num. of Answers"]
# filenames = ["query", "rules", "facts", "parset", "transt", "reasont", "answers"]

# a1 = {k: [0 for i in range(50)] for k in sizes}
# a2 = {k: [0 for i in range(50)] for k in sizes}
# a3 = {k: [0 for i in range(50)] for k in sizes}

# size = 1500000
# graphOf = 5
# ylabel = ylabels[graphOf]

# for num in range(expnum):
# 	for i in range(len(sizes)):
# 		with open(f'1-{sizes[i]}-{num+1}.csv', 'r') as reader:
# 			next(reader)
# 			j = 0
# 			for line in reader.readlines():
# 				a1[sizes[i]][j] += int(line.split(',')[graphOf].strip())
# 				j += 1
# 		with open(f'2-{sizes[i]}-{num+1}.csv', 'r') as reader:
# 			next(reader)
# 			j = 0
# 			for line in reader.readlines():
# 				a2[sizes[i]][j] += int(line.split(',')[graphOf].strip())
# 				j += 1
# 		# with open(f'3-{size[i]}-{num+1}.csv', 'r') as reader:
# 		# 	next(reader)
# 		# 	j = 0
# 		# 	for line in reader.readlines():
# 		# 		a3[sizes[i]][j] += (int(line.split(',')[graphOf].strip())
# 		# 		j += 1

# print(a1)

# ########################### BAR CHART #####################################

# labels = [i for i in range(50)]
# d1 = [i / expnum for i in a1[size]]
# d2 = [i / expnum for i in a2[size]]
# # d3 = a3[size] / expnum

# x = np.arange(len(labels))  # the label locations
# width = 0.35  # the width of the bars

# fig, ax = plt.subplots()
# rects1 = ax.bar(x - width/3, d1, width, label='Translation 1')
# rects2 = ax.bar(x + width/3, d2, width, label='Translation 2')
# # rects3 = ax.bar(x + width/3, d3, width, label='Translation 3')

# # Add some text for labels, title and custom x-axis tick labels, etc.
# ax.set_ylabel(ylabel)
# ax.set_title("Comparison of "+ylabel+" by Translation")
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
# plt.ylim(0,55000000000)
# plt.show()

##################################### BY SIZE #########################################

sizes = [i for i in range(100000, 2000001, 100000)]
ylabels = ["Query", "Rules Generated", "Facts Generated", "Parsing Time", "Translating Time", "Reasoning Time", "Num. of Answers"]

a1 = {k: [0 for i in range(len(sizes))] for k in range(50)}
a2 = {k: [0 for i in range(len(sizes))] for k in range(50)}
a3 = {k: [0 for i in range(len(sizes))] for k in range(50)}

query = 10
graphOf = 6
ylabel = ylabels[graphOf]

for num in range(expnum):
	for i in range(len(sizes)):
		j = 0
		with open(f'1-{sizes[i]}-{num+1}.csv', 'r') as reader:
			next(reader)
			for line in reader.readlines():
				a1[j][i] += int(line.split(',')[graphOf].strip())
				j += 1
		j = 0
		with open(f'2-{sizes[i]}-{num+1}.csv', 'r') as reader:
			next(reader)
			for line in reader.readlines():
				a2[j][i] += int(line.split(',')[graphOf].strip())
				j += 1
		# with open(f'3-{size[i]}.csv', 'r') as reader:
		# 	next(reader)
		# 	for line in reader.readlines():
		# 		a3[sizes[i]].append(int(line.split(',')[graphOf].strip()))

print(a1)

########################### BAR CHART #####################################

labels = [str(int(i/1000))+"K" for i in range(100000, 2000001, 100000)]
d1 = [i / expnum for i in a1[query]]
d2 = [i / expnum for i in a2[query]]
# d3 = a3[query] / expnum

x = np.arange(len(labels))  # the label locations
width = 0.35  # the width of the bars

fig, ax = plt.subplots()
rects1 = ax.bar(x - width/3, d1, width, label='Translation 1')
rects2 = ax.bar(x + width/3, d2, width, label='Translation 2')
# rects3 = ax.bar(x + width/3, d3, width, label='Translation 3')

# Add some text for labels, title and custom x-axis tick labels, etc.
ax.set_ylabel(ylabel)
ax.set_title("Comparison of "+ylabel+" by Translation")
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
plt.ylim(0,50000000000)
plt.show()





