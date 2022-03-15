Program probD;
var a:array[1..25] of string[4];
	M:array[1..24 ] of string[2];
	Vendas:array[1..25] of word;
	i,n,aux:longint;
	media:longint;


begin


for i:=1 to 24 do
begin
	read(a[i],m[i]);
	readln(vendas[i]);
end;
n:=24;
media:=0;


for i:=24 downto 21 do
	media:=trunc((Vendas[i]+media)div (24-(i-1)));


aux:=23;
i:=0;
while aux<>0 do
begin	

	if m[24]=m[aux] then
	begin	
		i:=i+1;
		media:=trunc((media+vendas[aux])/ i);
	
	end;
	aux:=aux-1

end;


writeln(media);

end.


