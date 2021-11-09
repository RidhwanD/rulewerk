import os
import glob

cases = ["sql-01","sql-02","sql-03","sql-04","sql-05","sql-06","sql-07","sql-08","sql-09",
"sql-10","sql-11","sql-12","sql-13","sql-14","sql-15","traffic","small","abduce","ship",
"inflamation","animals","rvcheck","path","union-find","cliquer","rsg","1-object-1-type",
"1-type","1-object","escape","modref","andersen","1-call-site","2-call-site","nearlyscc", 
"sgen", "scc","buildwall","polysite","downcast"]

for case in cases:
    files = glob.glob(case+'/*')
    for f in files:
        os.remove(f)
