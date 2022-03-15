program mirror;
CONST Prob_id = 'W';
      File_id = 'PROBLEM';

 const
  max = 10;
 type
  pattern = array[1..max, 1..max] of boolean;

 procedure rot90 (i, j, size: integer; var newi, newj: integer);
 begin
  newi := j;
  newj := size - i + 1;
 end;

 procedure invert (i, j, size: integer; var newi, newj: integer);
 begin
  newi := size - i + 1;
  newj := j;
 end;

 function equal (pat1, pat2: pattern; size: integer; reflect: boolean;
angle: integer): boolean;
  var
   i, j, k, newi, newj: integer;
   found: boolean;
 begin
  i := 1;
  found := false;
  while (i <= size) and not found do
   begin
    j := 1;
    while (j <= size) and not found do
     begin
      if reflect then
       invert(i, j, size, newi, newj)
      else
       begin
       newi := i;
       newj := j;
       end;
      for k := 1 to angle do
       rot90(newi, newj, size, newi, newj);
      found := pat1[i, j] <> pat2[newi, newj];
      j := j + 1;
     end;
    i := i + 1;
   end;
  equal := not found;
 end;

 var
  line: string;
  oldpat, newpat: pattern;
  i, j, k: integer;
  found: boolean;
begin
 repeat
  readln(k);
  if k <> 0 then
   begin
    for i := 1 to k do
     begin
      readln(line);
      for j := 1 to k do
       oldpat[i, j] := line[j] = 'x';
      for j := k + 2 to 2 * k + 1 do
       newpat[i, j - k - 1] := line[j] = 'x';
     end;
    found := equal(oldpat, newpat, k, false, 0);
    if found then
     begin writeln('Preserved') end
    else
     begin
      for i := 1 to 3 do
       if not found then
       if equal(oldpat, newpat, k, false, i) then
       begin
       writeln('Rotated through ', i * 90:1, ' degrees');
       found := true;
       end;
      if not found then
       begin
       found := equal(oldpat, newpat, k, true, 0);
       if found then
         begin writeln('Reflected') end
       else
       begin
       for i := 1 to 3 do
       if not found then
       if equal(oldpat, newpat, k, true, i) then
       begin
       writeln('Reflected and rotated through ', i * 90:1, ' degrees');
       found := true;
       end;
       if not found then
         begin writeln('Improper') end;
       end;
       end;
     end;
   end;
 until k = 0;

end.