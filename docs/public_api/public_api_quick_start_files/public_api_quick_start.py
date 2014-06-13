with open('public_api_quick_start.md') as f:
    lines = f.readlines()

first = True
for line in lines:
    if line.startswith('#'):
       if first:
           first = False;
       else:
           line = '---\n\n\n'
    print line 
