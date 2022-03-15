
program exb;

var input, i, cd, conta: integer;
    aip: array[1..1000] of integer;

begin
  conta:= 0;
readln(input);

for i:= 1 to input do
   begin


      readln(aip[i]);

   end;

   for i:= 2 to input do
      begin

         cd := aip[i-1] * 2;

         if(aip[i]> cd ) then
            begin

               conta:= conta +1;

            end;

      end;
   writeln(conta);

end.