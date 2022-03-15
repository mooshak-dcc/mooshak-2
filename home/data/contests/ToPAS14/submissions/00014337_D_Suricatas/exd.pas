program exd;

var aip: array[1..100] of string;
    i, um, erro,erro2, conta, media1,media2, mediat, total, mea, nv: integer;
    c1, c2: string;

begin

for i:= 1 to 24 do
   begin


      readln(aip[i]);

   end;

c1:= copy(aip[24], 5, 2);
val(c1, um, erro);
um:= um;
conta:= 0;
total:= 0;

for i:= 1 to 23 do
   begin

      c1:= copy(aip[i], 5,2);
      val(c1, mea, erro);

      if mea = um then
         begin
             conta:= conta + 1;
             c2:= copy(aip[i], 8, 999);
             val(c2, nv,erro2);

             total:= total + nv;
         end;


   end;


  media1:= trunc(total/conta);

  total:= 0;
  for i:= 22 to 24 do
     begin

     c2:= copy(aip[i], 8, 999);
             val(c2, nv,erro2);

             total:= total + nv;

     end;


  media2:= trunc(total/3);

  mediat:= trunc((media1 + media2)/2);

  writeln(mediat);


end.