Program X;
var vet:array[1..100]of integer;
i,tempo,n,j:integer;
media1,media2,media3:real;
Begin
        j:=0;
      for i:=1 to 24 do
      begin
          readln(tempo,n);
          if ((i=12) or (i=22)or (i=23) or (i=24)) then
          begin
               j:=j+1;
               vet[j]:=n;
          end;
      end;
      media1:=((vet[1]+vet[4])/2);
      media2:=((vet[2]+vet[3]+vet[4])/3);
      media3:=(media1+media2)/2;
      writeln(trunc(media3));

End.