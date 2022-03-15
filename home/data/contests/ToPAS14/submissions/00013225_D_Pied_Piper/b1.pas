Program probD;
var a:array[1..25] of string[4];
	M:array[1..24 ] of string[2];
	Vendas:array[1..25] of word;
	i,aux:longint;
	media:real;


begin


for i:=1 to 24 do
begin
	read(a[i],m[i]);
	readln(vendas[i]);
end;

aux:=0;

media:=0.0;
for i:=24 downto 22 do
begin
aux:=aux+1;
media:=(media+vendas[i])/aux;
end;


aux:=0;
for i:=23 downto 1 do
	if m[i]=m[24] then
	begin
		aux:=aux+1;
		media:=(media+vendas[i]) /aux;
	
	end;


writeln(trunc(media));



end.


