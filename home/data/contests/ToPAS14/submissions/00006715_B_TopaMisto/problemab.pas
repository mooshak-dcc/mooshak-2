program problemab;
var
num: array [1..3000] of integer;
d,k,r,f,n,n2,x,z :integer;
begin
 read (n);
 n:=n2;
 for k:=1 to n do
  begin
   read (num[k]);
  end;
 for k:=2 to n2 do
  begin
   d:=Num[k];
   z:=k-1;
   r:=num[z];
   x:=k+2;
   f:=num[x];
   if (d<(2*R)) and (d>(2*f)) then
                           writeln (d);
   end;
end.
