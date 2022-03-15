program proba;
var ano, mes, dia:integer;
    a, b, c, d ,e, dj:real;
begin
        read(ano, mes, dia);
        if mes<3 then
        begin
             ano:=ano-1;
             mes:=mes+12;
        end;
        a:=ano/100;
        b:=a/4;
        if (dia>=15) and (mes>=10) and (ano>=1582) then
        c:=2-a+b;
        if (dia<=4) and (mes<=10) and (ano<=1582) then
        c:=0;
        d:=365.25*(ano+4716);
        e:=30.6001*(mes+1);
        dj:=d+e+dia+c-1524;
        write(dj:2:0);
end.