program ex3;
var
        i,x,y,ns,soma,somat,x2,y2,x3,x4:integer;
        x1,y1,sub,sub1:array[1..200] of integer;
        sitio:string;
        sitio1:array[1..200] of string;

begin
        readln(x,y,sitio);
        readln(ns);
        if ns=0 then writeln('Nenhum')
        else begin
        for i:=1 to ns do begin
                readln(x1[i],y1[i],sitio1[i]);
                if sitio1[i]=sitio then begin
                        if x1[i]>x then sub[i]:=x1[i]-x
                        else sub[i]:= x-x1[i];
                        if y1[i]>y then sub1[i]:=y1[i]-y
                        else sub1[i]:=y-y1[i];
                end;
        end;
        somat:=600;
        for i:= 1 to ns do begin
                if sitio1[i]=sitio then begin
                        soma:= sub[i]+sub1[i];
                        if soma<somat then begin
                                somat:=soma;
                                x2:=x1[i];
                                y2:=y1[i];
                        end;
                end;
        end;
        x3:=x2-x;
        x4:=x3*x3;
        writeln(x2,' ',y2,' ',sitio,' ',x4);
        end;
end.

end.
