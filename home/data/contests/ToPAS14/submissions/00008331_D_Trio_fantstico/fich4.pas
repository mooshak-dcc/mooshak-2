program ex4;
var valor,data:array[1..24] of real;
    i:integer;
    me,med,medt:real;
begin
    for i:= 1 to 24 do readln(data[i],valor[i]);
    me:=(valor[24]+valor[23]+valor[22])/3;
    med:=(valor[1]+valor[13])/2;
    medt:=int((med+me)/2);
    writeln(medt:0:0);
end.
