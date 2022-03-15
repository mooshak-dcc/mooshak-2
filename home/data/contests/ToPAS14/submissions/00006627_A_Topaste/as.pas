Program ola;
var ano,mes,dia,a,b,c,d,e,dj:double;
Begin
   read(ano);
   read(mes);
   read(dia);

   if(mes<3)then
   begin
        ano:=ano-1;
        mes:=mes+12;
   end;

   a:=trunc(ano/100);
   b:=trunc(a/4);


   if(ano>1582)then
   begin
    c:=2-a+b;
   end else
   if(mes>10)then
   begin
    c:=2-a+b;
   end else
   if(dia>15)then
   begin
    c:=2-a+b;
   end else
   begin
    c:=0;
   end;

   d:=trunc(365.25*(ano+4716));
   e:=trunc(30.6001*(mes+1));
   dj:=trunc(d+e+dia+c-1524);

   writeln(dj);
end.
