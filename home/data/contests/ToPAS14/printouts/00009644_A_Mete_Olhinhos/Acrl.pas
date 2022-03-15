Program AA;
var a,ano,i,mes,dia,code:integer;
c,d,e,dj,g:real;
b:real;
p,x:string;
Begin
        readln(ano,mes,dia);
        if (mes<3) then
        begin
            ano:=ano-1;
            mes:=mes+12;
        end;
        a:=ano div  100;
         b:=a div 4;
          if (ano>1582) then
        begin
            c:=2-a+b;
        end;
         if( (ano>1582) and (mes>10)) then
        begin
            c:=2-a+b;
        end;
        if ((ano>1582) and (mes=10)and (dia<=15)) then
        begin
           c:=2-a+b;
        end;

        d:=(365.25*(ano+4716));

        e:=(30.6001*(mes+1));
        dj:=(d+e+dia+c-1524);

            writeln(dj);



End.