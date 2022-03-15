Program probA;
var ano,mes,dia:longint;
a,b,d,e,c,dj:longint;



//>>>>>>>>>>>>>>>>>programa principal>>>>>>>><
begin


readln(ano,mes,dia);


if mes<3 then 
begin
	ano:=ano-1;
	mes:=mes+12;

end;


A:=ano div 100;
b:=A div 4;


if (ano>1582) and (mes>10) and (dia>15) then
begin
	c:=2-A+b;
end;

if (ano<=1582) and (mes<=10) and (dia<=4) then
	c:=0;
	
	
if (mes=10) and (dia=4) and (ano=1582) then
begin
	dia:=15;
	mes:=10;
	ano:=1582;
end;


d:=trunc(365.25*(ano+4716));
e:=trunc(30.6001*(mes+1));

dj:=d+e+dia+c-1524;

writeln(dj);






end.


