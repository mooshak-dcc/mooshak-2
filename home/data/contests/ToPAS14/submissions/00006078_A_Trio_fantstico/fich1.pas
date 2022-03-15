program ex1;
var ano,mes,dia,a,b,c:integer;
    d,dj,e:real;
begin
        readln(ano,mes,dia);
        if(mes<3) then begin
                ano:=ano-1;
                mes:=mes+12;
        end;
         a:=ano div 100;
         b:=a div 4;
        if (ano=1582) and (mes=10) and (dia>15) then c:=2-a+b ;
        if (ano=1582) and (mes>10) then c:=2-a+b;
        if (ano>1582) then c:=2-a+b;
        if (ano=1582) and (mes=10) and (dia<=4) then c:=0;
        if (ano=1582) and (mes<=10) then c:=0;
        if (ano<=1582) then c:=0;
        d:=int(365.25*(ano+4716));
        e:=int(30.6001*(mes+1));
        dj:=d+e+dia+c-1524;
        writeln(dj:0:0);
end.
