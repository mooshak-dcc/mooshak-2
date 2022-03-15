Program Indicador_mais_ou_menos;
Var
 NUm: array [1..24,1..2] of string;
 soma,media,So,Me,Fi,Fil:Real;
 T,Z,P,S,D,B,E:Real;
 L,k:integer;
Begin
  For L:=1 to 24 do begin
                    Read(Num [l,1]);
                    Read(Num[l,2]);
                    end;
  Val(Num[24,2],P,k);
  val(Num[23,2],S,k);
  Val(Num[22,2],D,k);
  soma:=P+S+D;
  media:=soma/3;
  Val(Num[1,2],B,k);
  Val(Num[13,2],E,k);
  So:= B+E;
  Me:=So/2;
  Fi:= Me+media;
  Fil:= Fi/2;
  T:= round (Fil);
  Z:=T-1;
  Writeln (Z);
 end.