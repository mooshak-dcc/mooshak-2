program problemaAA;

uses crt, strutils, sysutils;

var

  ano                                : integer;
  mes                                : integer;
  dia                                : integer;

  data                               : string;

  a                                  : integer;
  b                                  : integer;
  c                                  : integer;
  d                                  : real;
  e                                  : real;

  DJ                                 : real;

begin

  readln (data);

  mes := StrToInt(Copy (data,6,2));
  dia := StrToInt(Copy (data,9,2));
  ano := StrToInt(Copy (data,1,4));
  data := IntToStr(dia) + '/' + IntToStr(mes) + '/' + IntToStr(ano);

    If mes < 3 then
      begin
        ano := ano - 1;
        mes := mes + 12;

      end;

  a := trunc(ano/100);
  b := trunc(a/4);

   If data > '15/10/1582' then
     begin
       c := 2 - a + b;
     end;

   If data <= '4/10/1582' then
     begin;
       c := 0;
     end;

  d := trunc(365.25 * (ano + 4716));
  e := trunc(30.6001 * (mes + 1));

  DJ := d + e + dia + (c) - 1524;

  writeln (trunc(DJ));

end.
