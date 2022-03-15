program problemaE;

uses crt, strutils, sysutils;

var

  dic                  : array [1..1000] of string;


  n                    : integer;
  i                    : integer;
  k                    : integer;
  conta                : integer;
  x                    : integer;
  conta_palavra        : integer;
  y                    : integer;

  TT                   : integer;

  linha                : string;
  text_temp            : string;
  outputF              : string;
  ytemp                : string;

  C                    : string;
begin

readln(n);

y := 0;
conta_palavra := 0;
outputf := '';
text_temp := '';
conta := 0;

 For i:= 1 to n do
  begin
    readln (dic[i]);
  end;

  Repeat

   readln(linha);

   C:= copy(linha,2,1);

   If copy(linha,1,1) = '1' then
      begin
        TT := length(linha);
       for k:= 1 to tt do
         begin
           if copy(linha,k,1) = ' ' then
             begin
               conta:=conta+1;

             end;
           if conta = 2 then x := k +1;

         end;//end do for
       repeat
       x:= x+1;
       if copy(linha,x,1) <> ' ' then
        begin
          text_temp:=text_temp + copy(linha,x,1);

        end
        else
        begin
        for i :=1 to n-1 do
         begin
           if dic[i] = text_temp then
            begin
             outputF:=outputF +' '+ IntToStr(i);
             conta_palavra := conta_palavra +1 ;
            end;
         end;
        end;
       until inttostr(conta_palavra) = c;

      end;
   If copy (linha,1,1) = '2' then
      begin
       for k:= 1 to tt do
        begin
         if copy(linha,k,1) = ' ' then
          begin
           conta := conta+1;

          end;

          if conta = 2 then x:= k+1;

          repeat

          x:=x+1;
          if copy(linha,x,1) <> ' '  then
           begin
            ytemp:= copy(linha,x,1);
            outputF:=outputf+dic[y];

            end
            else
            begin

            conta_palavra:= conta_palavra+1;
           end;
          until inttostr(conta_palavra) = c;

           end;

        end;


   Until copy(linha,1,1) = '0';




end.