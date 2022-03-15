
all: pass safeexec

pass: pass.c
	cc -o pass -g pass.c -lcrypt -ldl


safeexec:
	cd SafeExec ; make 
	cp SafeExec/safeexec .
	chmod u+s,o+x safeexec



clean:
	rm -f safeexec pass pass.o
