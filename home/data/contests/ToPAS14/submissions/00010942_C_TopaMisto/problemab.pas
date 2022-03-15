program problemab;
var
num: array [1..3000] of integer;
d,k,r,f,n,x,z,y,p :integer;
begin
 read (n);
 for k:=1 to n do
  begin
   read (num[k]);
  end;
 for p:=2 to n do
  begin
   d:=Num[p];
   z:=p-1;
   r:=num[z];
   x:=p+2;
   f:=num[x];
   if (d>(2*R)) and (d>(2*f)) then
                               y:=y+1;
   end;
   if (num[1])>(2*num[2]) then y:=y+1;
   writeln (y);
end.
