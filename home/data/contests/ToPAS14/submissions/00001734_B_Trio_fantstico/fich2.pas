program ex2;
var
        seq:array[1..997] of integer;
        n,i,cont:integer;
begin
        readln(n);
        cont:=0;
        for i:= 1 to n do
                readln(seq[i]);
        for i:= 2 to n-1 do begin
                if (seq[i]>seq[i-1]*2) and (seq[i]>seq[i+1]) then cont:=cont+1;
        end;
        writeln(cont);
end.