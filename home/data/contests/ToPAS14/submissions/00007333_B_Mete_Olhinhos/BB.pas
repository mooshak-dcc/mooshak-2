Program A    ;
var  vet : array [1..100] of integer;
 n,x,aa,b,cont,i,j:integer;
Begin
   cont:=0;
   readln(n);
   for i:=1 to n do
      begin
        readln(x);
        vet[i]:=x;
      end;

      for j:=1 to n do
        begin
          aa:=vet[j]*2;
          b:=vet[j+2]*2;

          if ((vet[j+1]>aa)and(vet[j+1]>b))then cont:=cont+1;



        end;
          writeln(cont);
          cont:=0;

End.