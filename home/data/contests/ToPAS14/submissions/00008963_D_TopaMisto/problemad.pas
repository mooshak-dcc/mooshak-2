program problemad;
var
dados : array [1..24,1..2] of integer;
m,s,f,l:integer;

begin
 for l:=1 to 24 do
  begin
   read (dados[l,1]);
   read (dados[l,2]);
  end;
 m:=(dados[22,2]+dados[23,2]+dados[24,2]) div 3;
 s:=(dados[1,2]+dados[12,2]) div 2;
 f:=(s+M) div 2;
 writeln (f);
end.
