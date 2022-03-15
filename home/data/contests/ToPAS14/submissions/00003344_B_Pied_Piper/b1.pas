Program probA;
var n,detetados,i:word;
	a:array[1..1000]of word;


//>>>>>>>>>>>>>>>>>programa principal>>>>>>>><
begin

readln(n);

for i:=1 to n do
	readln(A[i]);
	


detetados:=0;



for i:=1 to n-1 do
	if (A[i]>A[i+1]*2) then
		if i=1 then
			detetados:=detetados+1
				else
					if A[i]>A[i-1]*2 then
						detetados:=detetados+1;



writeln(detetados);

end.


