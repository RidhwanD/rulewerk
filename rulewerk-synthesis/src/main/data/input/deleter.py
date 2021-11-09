import os

cases = ["sql-01","sql-02","sql-03","sql-04","sql-05","sql-06","sql-07","sql-08","sql-09",
"sql-10","sql-11","sql-12","sql-13","sql-14","sql-15","traffic","small","abduce","ship",
"inflamation","animals","rvcheck","path","union-find","cliquer","rsg","1-object-1-type",
"1-type","1-object","escape","modref","andersen","1-call-site","2-call-site","nearlyscc", 
"sgen", "scc","buildwall","polysite","downcast"]

print(len(set(cases)))
itr = 100

for case in cases:
  if os.path.exists(case+f"/rulewerk-log.txt"):
      os.remove(case+f"/rulewerk-log.txt")
  if os.path.exists(case+f"/rulewerk-result.txt"):
      os.remove(case+f"/rulewerk-result.txt")
  for i in range(1,itr+1):
    if os.path.exists(case+f"/rulewerk-log-1-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-log-1-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-log-2-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-log-2-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-log-3-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-log-3-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-log-4-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-log-4-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-result-1-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-result-1-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-result-2-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-result-2-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-result-3-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-result-3-{i}-ds.txt")
    if os.path.exists(case+f"/rulewerk-result-4-{i}-ds.txt"):
      os.remove(case+f"/rulewerk-result-4-{i}-ds.txt")