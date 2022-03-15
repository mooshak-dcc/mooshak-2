Program probA;
var n,detetados,i:longint;
	a:array[1..1000]of longint;


begin

readln(n);




for i:=1 to n do
begin
	readln(A[i]);	
end;	


detetados:=0;




for i:=2 to (n-1) do
begin

	if i=1 then
	begin
		if A[i]>(a[i+1]*2) then
		begin
			detetados:=detetados+1;
		end;
		
	end
		else
					if (A[i]>A[i-1]*2) and (A[i]>A[i+1]*2) then
					detetados:=detetados+1;

end;						


//if A[n]>a[n-1]*2 then
	//detetados:=detetados+1;




writeln(detetados);
end.


