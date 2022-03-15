program problemaA;

uses crt, strutils, sysutils;

var

  ano                                : integer;
  mes                                : integer;
  dia                                : integer;

  data                               : string;

  a                                  : integer;
  b                                  : integer;
  c                                  : integer;
  d                                  : integer;
  e                                  : integer;

  DJ                                 : real;

begin

  readln (ano);
  readln (mes);
  readln (dia);

  data := IntToStr(dia) + '/' + IntToStr(mes) + '/' + IntToStr(ano);
  writeln (data);

    If mes < 3 then
      begin
        writeln ('O meu ano e = ', ano);
        ano := ano - 1;
        writeln ('O meu ano agora e = ',ano);
        writeln ('O meu mes e = ',mes);
        mes := mes + 12;
        writeln ('O meu mes agora e = ', mes);
      end;

  a := trunc(ano/100);
  b := trunc(a/4);

   If data > '15/10/1582' then
     begin
       c := 2 - a + b;
       writeln ('o meu c e = ',c);
     end;

   If data <= '4/10/1582' then
     begin
       c := 0;
     end;

  d := trunc(365.25 * (ano + 4716));
  e := trunc(30.6001 * (mes + 1));

  DJ := d + e + dia + c - 1524;

  writeln ('d=',d);
  writeln ('e= ', e);
  writeln ('o DJ e = ', trunc(DJ));

end.
