program exe;

var n, i, np, erro, i2,i3, p1, p2: integer;
    a1, a2, a3, c1, c2,c3, ac1, ac2, op: string;
    aip, aop1: array[1..1000] of string;
    aop2: array[1..1000] of integer;
    ps: boolean;


begin

ps:= false;

readln(n);

for i:= 1 to n do
   begin

      readln(aip[i]);

   end;
readln(a1);
readln(a2);
readln(a3);

c1:= copy(a1, 3, 1);
val(c1, np, erro);

c2:= copy(a1, 5, 9999);
c2:= c2 + ' ';

for i:= 1 to np do
   begin

      p1:= pos(' ', c2);
      c3:= copy(c2,1,p1-1);
      c2:= copy(c2, p1 +1, 999);

      for i2:= 1 to n do
         begin

            p2:= pos(c3, aip[i2]);
            if p2 > 0 then
               begin
                  writeln('encontrei', c3, ' no', aip[i2]);
                  aop2[i]:= i2;

               end;

         end;


   end;

for i:= 1 to np do
   begin

   if i = 1 then
      begin
      end
   else
      begin

         write(' ');
      end;
   write(aop2[i]);

   end;

writeln();
c1:= copy(a2, 3, 1);
val(c1, np, erro);

c2:= copy(a2, 5, 999);
c2:= c2 + ' ';
for i:= 1 to np do
   begin
      p1:= pos(' ', c2);
      c3:= copy(c2,1, p1-1);
      val(c3, i3, erro);
      c2:= copy(c2, p1+1, 999);
      aop1[i]:= aip[i3];


   end;

for i:= 1 to np do
  begin

     if i = 1 then
        begin

        end
     else
        begin
           write(' ');

        end;

     write(aop1[i]);

  end;
writeln();


end.