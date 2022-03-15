program problemaa;
var ano, mes, dia, a,b,d,c,dj,e:integer ;

begin
 read (ano);
 read (mes);
 readln (dia);
 ano:=ano-1;
 mes:=mes+12;
 a:=ano div 100;
 b:=a div 4;
 if (ano>15) and (mes>10) and (ano >1582) then
                                           c:=2-A+B
                                          else
                                           begin
                                            if (ano<=4) and (mes<=1582) then
                                                                         c:=0
                                           end;

d:= round (365.25*(ano+4716));
e:= round (30.6001*(mes+1));
dj:=d+e+dia+c-1524;
writeln (dj);

end.
