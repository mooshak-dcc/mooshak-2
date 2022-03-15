program exa;

var input, c1: string;
    ano, mes, dia, p1, erro, a, b, c: integer;
    d, e, dj: longint;
    d1, e1: real;

begin

c:= 0;

readln(input);


input:= input + ' ';

p1:= pos(' ', input);
c1:= copy(input, 1, p1- 1);
val(c1, ano, erro);

input:= copy(input, p1+1, 999);

p1:= pos(' ', input);
c1:= copy(input,1, p1-1 );
val(c1, mes, erro);
input:= copy(input, p1+1, 999);

p1:= pos(' ', input);
c1:= copy(input, 1, p1-1);
val(c1, dia, erro);



if(mes < 3) then
   begin

      ano:= ano - 1;
      mes := mes + 12;

   end;

   a:= trunc(ano / 100);
   b:= trunc(a / 4);


if((ano > 1582)) then
   begin

   if(ano = 1582) then
   begin

   if (mes > 10) then
   begin

      if(mes= 10) then
      begin

      if(dia > 15) then
      begin

         c:= 2 - a + b;

      end;

      end
      else
      begin

         c:= 2 - a + b;

      end;

   end;

   end
   else
   begin

   c:= 2 - a + b;

   end;



   end;


if (((dia < 4) and (mes < 10) and (ano< 1582)) or ((dia = 4) and (mes = 10) and (ano= 1582)))then
   begin

      c:= 0;

   end;


p1:= ano + 4716;
d:= trunc(365.25 * p1);

p1:= mes + 1;

e:= trunc(30.6001  * p1);





dj := e + d + dia + c  - 1524;

writeln(dj);

end.