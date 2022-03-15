program probb;
var n, i, cont:integer;
    sinal:array[3..1000] of integer;
    dobro:array[3..1000] of integer;
begin
        read(n);
        for i:=1 to n do
                begin
                read(sinal[i]);
                end;

                cont:=0;

                for i:=1 to n do
                begin
                dobro[i]:=sinal[i]*2;
                end;

                for i:=1 to n do
                begin
                     if (dobro[i]>dobro[i+1]) and (dobro[i]>dobro[i+2]) and (dobro[i]>dobro[i-1]) and (dobro[i]>dobro[i-2]) then
                     cont:=cont+1;
                end;
        write(cont);
end.
